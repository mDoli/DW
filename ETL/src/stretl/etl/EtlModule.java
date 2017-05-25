package stretl.etl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import stretl.base.BaseModule;
import stretl.common.Tuple;
import stretl.dataprovider.DataProvider;
import stretl.etl.schema.EtlSchema;
import stretl.etl.schema.EtlSchemaImplBackups1;
import stretl.etl.schema.EtlSchemaImplBackups2;
import stretl.etl.schema.EtlSchemaImplBackups3;
import stretl.network.NetworkTupleReader;
import stretl.etl.schema.IEtlSchema;

/**
 * ETL-RT module class.
 * 
 * @author Artur Drzeniek
 * @since 2016
 */
public class EtlModule extends BaseModule implements Runnable {
        
    private static final Logger LOGGER = Logger.getLogger(EtlModule.class.getName());
    
    /**
     * Flag describing that module is running.
     */
    public boolean isRunning = false;
    
    /**
     * Update object reference for volatile values.
     */
    UpdateObj updateObj;
    
    /**
     * Network tuple reader for reading commands to module.
     */
    NetworkTupleReader networkReader;
    
    /**
     * Collection of ETL schemas defined in ETL-RT module.
     */
    List<IEtlSchema> schemas = new LinkedList<>();
    
    /**
     * Thread pool to execute schemas.
     */
    ExecutorService executor;
    
    /**
     * Collection containing actually running schemas.
     */
    HashMap<Future<Boolean>, EtlSchema> runningSchemas = new HashMap<>();
    
    /**
     * Collection containing failed schemas.
     */
    HashMap<Future<Boolean>, EtlSchema> failedSchemas = new HashMap<>();    
    
    /**
     * Collection of schemas to recover.
     */
    List<IEtlSchema> recoverySchemas = new LinkedList<>();
    
    /**
     * Data provider object used load failed schemas.
     */
    DataProvider dataProvider = new DataProvider();
    
    /**
     * Number of tuples processed by this instance of ETL-RT module.
     */
    long tuplesProcessed = 0L;
    
    /**
     * Ctor of ETL-RT module.
     * @param id Module id.
     * @param updateObj Shared update object.
     */
    public EtlModule(int id, UpdateObj updateObj) {
        super("ETL-RT", id);
        this.updateObj = updateObj;
    }
    
    /**
     * Method initalizing module.
     * @return Return true if module initalizet succesfully.
     */
    public boolean Init() {  
        
        return true;
    }
        
    @Override
    public void run()
    {               
        List<Tuple> dataSource = new LinkedList<>();
        isRunning = true;
        updateObj.runIdx = 0;
        
        createNetworkTupleReader();
        
        createSchemas();
                
        buildSchemasWithReferenceToDataSource(dataSource);
        
        createThreadPoolForSchemas();
        
        // Connect to database
        dataProvider.connect();
        
        recoverFailedSchemasAfterModuleFail();
        
        // For testing: count module restart time.
        if (updateObj.startMiliseconds != 0L)
        {
            updateObj.endMiliseconds = System.currentTimeMillis();
            long diff = updateObj.endMiliseconds - updateObj.startMiliseconds;
            LOGGER.log(Level.INFO, "Module restart time: {0}", diff);
            
            updateObj.startMiliseconds = 0L;
            updateObj.endMiliseconds = 0L;
        }
        
        while (!this.isInterrupted()) 
        {
            LOGGER.log(Level.INFO, "TEST: Start time: {0}", System.currentTimeMillis());
            
            waitIfModuleInputQueueIsEmpty();
            
            recoverFailedSchemas();
            
            updateModuleDataSourceWithDataFromInputQueue(dataSource);
            
            runAllSchemasInThreadPoolAndReturnFutures(dataSource);
            
            checkSchemasFuturesResultsAndTakeAction();
            
            finishModuleIteration(dataSource);    
            
            LOGGER.log(Level.INFO, "TEST: End time: {0}", System.currentTimeMillis());
            //LOGGER.log(Level.INFO, "TEST: Tuples processed {0}", tuplesProcessed);            
        }
        
        // Disconnect from database.
        dataProvider.disconnect();
        
        // This will make the executor accept no new threads
        // and finish all existing threads in the queue
        executor.shutdown();
    }   

    private void finishModuleIteration(List<Tuple> dataSource) {
        runningSchemas.clear();
        dataSource.clear();
    }

    private void createNetworkTupleReader() {
        networkReader = new NetworkTupleReader(this);
        networkReader.start();
    }

    private void createSchemas() {
        // Schemat ETL
        UpdateObj s0UpdObj = new UpdateObj();
        //schemas.add(new EtlSchemaImpl(s0UpdObj, 0));
        
        UpdateObj s2UpdObj = new UpdateObj();
        schemas.add(new EtlSchemaImplBackups1(s2UpdObj, 2));
        UpdateObj s3UpdObj = new UpdateObj();
        schemas.add(new EtlSchemaImplBackups2(s3UpdObj, 3));
        UpdateObj s4UpdObj = new UpdateObj();
        schemas.add(new EtlSchemaImplBackups3(s4UpdObj, 4));
        //UpdateObj s1UpdObj = new UpdateObj();
        //schemas.add(new EtlSchemaImplBackups(s1UpdObj, 1));
    }

    private void createThreadPoolForSchemas() {
        executor = Executors.newCachedThreadPool();
    }

    private void recoverFailedSchemasAfterModuleFail() {      
        long start = System.currentTimeMillis();
        try {
            List<Integer> schemaIdsFromBackup = dataProvider.readAllSchemaIdsFromBackupTable();
            for (Integer id : schemaIdsFromBackup) {
                try {
                    IEtlSchema schema = (IEtlSchema) schemas.stream()
                        .filter((s) -> Objects.equals(s.getId(), id))
                        .findFirst().get();
                
                if (schema != null)
                    recoverySchemas.add(schema);   
                }
                catch(Exception e) {}
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
        }
        
        if (recoverySchemas.size() > 0)
        {
            LOGGER.log(Level.INFO, "{0} schemas to recover.", recoverySchemas.size());
            
            for (IEtlSchema recoverySchema : recoverySchemas) {
                recoverySchema.setSource(dataProvider.readBackup(recoverySchema.getId()));
                Future<Boolean> future = executor.submit((EtlSchema)recoverySchema);
                LOGGER.log(Level.INFO, "{0} submited.", recoverySchema.toString());
                
                runningSchemas.put(future, ((EtlSchema)recoverySchema));                
            }
            
            for (Map.Entry<Future<Boolean>, EtlSchema> entry : runningSchemas.entrySet()) {
                try {
                    Future<Boolean> future = entry.getKey();
                    boolean success = future.get();
                    if (success) 
                    {
                        recoverySchemas.remove(entry.getValue());
                        LOGGER.log(Level.INFO, "{0} recovered", entry.getValue().toString());
                        
                        deleteSchemaBackupFromDatabase(entry.getValue());
                    }
                    else
                    {
                        LOGGER.log(Level.WARNING, "{0} could not be recovered", entry.getValue().toString());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    LOGGER.log(Level.SEVERE, ex.toString(), ex);
                }
            }
        }
        //long end = System.currentTimeMillis();        
        //LOGGER.log(Level.INFO, "Module recovery time {0}", end - start);        
    }   

    private void recoverFailedSchemas() {
        if (failedSchemas.isEmpty())
        {
            LOGGER.log(Level.FINE, "No failed schemas.");
        }
        else
        {
            for (Map.Entry<Future<Boolean>, EtlSchema> entry : failedSchemas.entrySet()) {
                EtlSchema schema = entry.getValue();
                LOGGER.log(Level.INFO, "Schema {0} backup start time {1}", new Object [] { 
                    schema.toString(), System.currentTimeMillis() });
                // Wczytanie danych do schematu z punktu kontrolnego
                schema.setSource(dataProvider.readBackup(schema.schemaId));
                
                try {
                    // Uruchomienie schematu
                    Future<Boolean> future = executor.submit((EtlSchema)schema);
                    LOGGER.log(Level.FINE, "{0} submited.", schema.toString());
                    
                    boolean success = false;
                    // Trzy proby ponownego uruchomienia schematu
                    for (int i = 0; i < 3; i++) {
                        // Oczekiwanie na wykonanie schematu
                        success = future.get();
                        
                        // tests
                        //success = true;
                        
                        // Gdy sie powiedzie zostanie przerwane powtarzanie
                        if (success)
                        {
                            LOGGER.log(Level.FINE, "{0} recovery success.", schema.toString());
                            break;
                        }
                        else
                        {
                            LOGGER.log(Level.WARNING, "{0} recovery failed {1} times",
                                    new Object[] { schema.toString(), i + 1 });
                        }
                    }
                    
                    // Jesli nadal sie nie powiodlo to trzeba usunac schemat
                    if (success == false)
                    {
                        LOGGER.log(Level.WARNING, "{0} recovery failed maximum times. Wouldn't be run again.", schema.toString());
                        if (runningSchemas.containsKey(future))
                        {
                            runningSchemas.remove(future);
                            LOGGER.log(Level.INFO, "{0} has been removed from running schemas colection.", schema.toString());
                        }
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    LOGGER.log(Level.SEVERE, "Recovery failed. " + ex.toString(), ex);
                }
                
                LOGGER.log(Level.INFO, "Schema {0} backup end time {1}", new Object [] { 
                    schema.toString(), System.currentTimeMillis() });
            }
            
            failedSchemas.clear();
        }
    }

    private void buildSchemasWithReferenceToDataSource(List<Tuple> moduleDataSource) {
        schemas.stream().forEach((schema) -> {
            schema.setSource(moduleDataSource);
            schema.build();
        });
    }

    private void checkSchemasFuturesResultsAndTakeAction() {
        for (Map.Entry<Future<Boolean>, EtlSchema> runningSchema : runningSchemas.entrySet()) {
            Future<Boolean> runningFuture = runningSchema.getKey();
            EtlSchema schema = runningSchema.getValue();
            
            try {
                // Pobiera wynik działania schematy.
                // Oczekuje na wynik jeśli trzeba czekać.
                boolean success = runningFuture.get();
                
                if (success) {
                    deleteSchemaBackupFromDatabase(schema);
                }
                else {
                    addSchemaToFailedSchemasCollection(runningFuture, schema);
                }
            } catch (InterruptedException | ExecutionException ex) {
                //LOGGER.log(Level.SEVERE, ex.toString(), ex);
                schema.updateObj.failed = true;
            } catch (CancellationException ex) {
                schema.updateObj.failed = true;
            }
        }
    }

    private void addSchemaToFailedSchemasCollection(Future<Boolean> runningFuture, EtlSchema schema) {
        LOGGER.log(Level.WARNING, "{0} failed. Added to recovery collection.", schema.toString());
        schema.updateObj.failed = true;
        // Nie usuwamy ostaniego punktu kontrolnego,
        // więc zostanie wczytany przy najstępnym uruchomieniu
        failedSchemas.put(runningFuture, schema);
    }

    private void deleteSchemaBackupFromDatabase(EtlSchema schema) {
        // W przypadku powodzenia mozna usunąc punkty kontrolne z bazy
        LOGGER.log(Level.FINE, "{0} finished with success", schema.toString());
        schema.updateObj.failed = false;
        try {
            dataProvider.deleteBackup(schema.schemaId);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
        }
    }

    private void updateModuleDataSourceWithDataFromInputQueue(List<Tuple> tankTuples) {
        // Pobranie danych do zrodla danych modulu
        LOGGER.log(Level.INFO, "TEST: Tuples in queue {0}", getModuleInputQueue().size());
        getModuleInputQueue().drainTo(tankTuples, 1000);
        tuplesProcessed += tankTuples.size();
        
    }

    /*
     Uruchomienie wszysctkich schematow w puli. Zwracana jest kolecja
     zawierajaca schemat oraz wynik jego działania.
    */
    private void runAllSchemasInThreadPoolAndReturnFutures(List<Tuple> tankTuples) {
        for (IEtlSchema schema : schemas) {            
            schema.setSource(tankTuples);     
            try {
                if (executor.isShutdown())
                    executor = Executors.newCachedThreadPool();
                Future<Boolean> future = executor.submit((EtlSchema)schema);
                LOGGER.log(Level.FINE, "{0} submited.", schema.toString());                        
                runningSchemas.put(future, ((EtlSchema)schema));
            }
            catch(Exception ex)
            {
                LOGGER.warning(ex.toString());  
            }            
        }
    }

    private void waitIfModuleInputQueueIsEmpty() {
        while (getModuleInputQueue().isEmpty())
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                
            }
        }
    }

    @Override
    public void interrupt() {
        // Przerwanie wybranego schematu
//        Future<Boolean> runningFuture = runningSchemas.keySet().iterator().next();
//        EtlSchema schema = runningSchemas.values().iterator().next();
//        if (runningFuture.cancel(true)) {            
//            schema.updateObj.runIdx = 0;
//            LOGGER.log(Level.WARNING, "{0} canceled", schema.toString());
//        }
//        else {            
//            LOGGER.log(Level.WARNING, "{0} not canceled", schema.toString());
//        }
        
        // Przerwanie dla całego modułu
        for (Future<Boolean> future : runningSchemas.keySet()) {
            future.cancel(true);
        }
        networkReader.interrupt();
        if (executor != null) executor.shutdownNow();
        isRunning = false;
        updateObj.failCounter++;
        super.interrupt(); 
    }
    
}
