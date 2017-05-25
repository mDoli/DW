package stretl.etl.schema;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import stretl.common.Tuple;
import stretl.etl.UpdateObj;
import stretl.etl.backup.CheckPoint;
import stretl.etl.extractor.Extractor;
import stretl.etl.inserter.Inserter;
import stretl.etl.transformer.Transformer;

/**
 *
 * @author Artur
 */
public abstract class EtlSchema implements IEtlSchema, Callable<Boolean> {
    private static final Logger LOGGER = Logger.getLogger(EtlSchema.class.getName());
    
    /**
     * Schema identyficator.
     */
    public int schemaId = 0;
    /**
     * Flag defining that schema was finished with succes.
     */
    public volatile boolean runWasSuccessful = false;    
    
    protected List<Extractor> extractors = new LinkedList<>();
    protected List<Transformer> transforms = new LinkedList<>();
    protected List<Inserter> inserters = new LinkedList<>();
    protected List<CheckPoint> checkPoints = new LinkedList<>();    
    protected List<SchemaElement> allElements = new LinkedList<>();    
    
    protected List<Tuple> source = new LinkedList<>();    
    public UpdateObj updateObj;
    
    public EtlSchema(UpdateObj updateObj, int schemaId) {
        this.updateObj = updateObj;
        this.schemaId = schemaId;
    }
    
    @Override
    public abstract void build();
    
    @Override
    public boolean process() {
        if (this.extractors.isEmpty())
            return false;
        
        boolean schemaSuccess = true;
        
        Extractor e = getExtractor(0);
        if (e != null) 
        {
            e.begin();
            // Sprawdzanie wszystkich elementow, czy zakonczyly sie poprawnie.
            for (SchemaElement element : allElements) {
                schemaSuccess = schemaSuccess & element.success;
                // Testy odtwarzania
                element.isSuccessElement = true;
            }
            LOGGER.log(Level.FINE, "{0} finished with {1}", new Object[] {this.toString(), schemaSuccess});
            return schemaSuccess;
        }
        else 
            return schemaSuccess;
    }    

    @Override
    public Integer getId() {
        return schemaId;
    }
    
    protected Extractor getExtractor(int id) {
        return this.extractors.get(id);
    }

    protected Transformer getTransform(int id) {
        return this.transforms.get(id);
    }
    
    protected Inserter getInserter(int id) {
        return this.inserters.get(id);
    }
    
    protected CheckPoint getCheckPoint(int id) {
        return this.checkPoints.get(id);
    }
    
    protected  void addExtractor(Extractor e) {
        this.extractors.add(e);
        this.allElements.add(e);
    }
    
    protected  void addTransform(Transformer t) {
        this.transforms.add(t);
        this.allElements.add(t);
    }
    
    protected  void addInserter(Inserter i) {
        this.inserters.add(i);
        this.allElements.add(i);
    }
    
    protected void addCheckPoint(CheckPoint cp) {
        this.checkPoints.add(cp);
    }

    @Override
    public String toString() {
        return "EtlSchema{" + "schemaId=" + schemaId + '}';
    }
    

    @Override
    public Boolean call() throws Exception {
        
        boolean success = false;
        try {
            success = process();
        } catch (CancellationException e) {
            // Ignore
        }
        
        if (success) updateObj.runIdx++;
        return success;
    }

    @Override
    public void setSource(List<Tuple> source) {
        if (this.source != null) this.source.clear();
        this.source.addAll(source);
    }
    
}
