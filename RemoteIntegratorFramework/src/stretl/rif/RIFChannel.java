package stretl.rif;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import stretl.base.BaseModule;
import stretl.base.BaseModuleChannel;
import stretl.common.CommandTuple;
import stretl.common.Entry;
import stretl.common.Tuple;
import stretl.dataprovider.DataProvider;
import stretl.network.NetworkTupleReader;
import stretl.network.NetworkTupleWriter;

/**
 * Klasa pojedynczego kanału przetwarzania.
 *
 * Obsługuje komendy z następnego modułu.
 * Korzysta tylko z pierwszej kolejki wejściowej kanału.
 * Bierze krotkę z kolejki.
 * Zapisuje krotę do bazy.
 * Kopiuje krotkę i przesyła strumieniem do modułu nasłuchującego. 
 * 
 * 
 * @author Artur Drzeniek <artudrz156@student.polsl.pl>
 */
public class RIFChannel extends BaseModuleChannel {

    private final static Logger LOG = Logger.getLogger(RIFChannel.class.getName());
    private final DataProvider dataProvider;
    private boolean dbIsConnected = false;
    
    /**
     * Konstruktor kanału modułu RIF.
     *
     * @param baseModule
     * @param idChannel Identyfikator tego kanału.
     */
    public RIFChannel(int idChannel, BaseModule baseModule) {
        super(idChannel, baseModule);        
        this.dataProvider = new DataProvider();
        networkTupleReader = new NetworkTupleReader(this);
        networkTupleWriter = new NetworkTupleWriter(this);
    }

    /**
     * Metoda działania pojedynczego kanału przetwarzania.
     */
    @Override
    public void run() {
        
        networkTupleReader.start();
        networkTupleWriter.start();
        
        dbIsConnected = dataProvider.connect();
        
        while (!Thread.interrupted()) 
        {         
            handleModuleCommands();
            
            // V2
            LinkedBlockingQueue<Tuple> inputQueue = getInputQueue(0L);
            if (inputQueue == null) continue;
            
//            if (inputQueue.isEmpty())
//            {
//                try {
//                    //Thread.sleep(1000);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(RIFChannel.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
            
            LinkedBlockingQueue<Tuple> tempQueue = new LinkedBlockingQueue<>();
            inputQueue.drainTo(tempQueue);
            LinkedBlockingQueue<Tuple> mergedQueue = new LinkedBlockingQueue<>();
            for (Tuple tuple : tempQueue) {
                if (!mergedQueue.contains(tuple)) {
                    mergedQueue.add(tuple);
                    inputQueue.remove(tuple);
                }                    
            }
            
            Tuple tuple = mergedQueue.poll();
            if (tuple != null) {
                
                // Zapis do bazy lub do pliku
                if (dbIsConnected) {
                    //dataProvider.saveTankTuple(tuple, this.channelId);
                    LOG.log(Level.FINE, "{0} saved to db.", tuple.toString());
                }
                LOG.log(Level.INFO, "{0}", tuple.toString());
                outputQueues.values().stream().forEach((outputQueue) -> {
                    Tuple tupleCopy = tuple.clone();  
                    outputQueue.add(tupleCopy);
                });
            }
            
        }
        
        dbIsConnected = !dataProvider.disconnect();
    }
    
    public LinkedList<Tuple> getDataFromDb(Integer idMeter, LocalDateTime startTime, LocalDateTime endTime) {
        LinkedList<Tuple> list = new LinkedList<>();        
        list.addAll(dataProvider.loadTankTuples(idMeter, startTime, endTime));        
        return list;
    }
    
    private void handleModuleCommands() 
    {
        if (networkTupleReader.moduleCommands.isEmpty()) return;
        
        CommandTuple command = networkTupleReader.moduleCommands.poll();
        handleModuleCommand(command);        
    }
    
    private void handleModuleCommand(CommandTuple command)
    {
        if (command == null) return;
        
        switch (command.getCommandType()) {
            case GET_TUPLES: 
            {
                LinkedList<Tuple> tuplesFromDb = getDataFromDb(command.getSource(), command.getDataStartTime(), command.getDataEndTime());
                InetSocketAddress sourceAddress = (InetSocketAddress)command.getSourceAddress();

                Entry<Long, DatagramSocket> queueAndSocket = networkTupleWriter.channelOutputs.get(sourceAddress.getPort());
                if (queueAndSocket != null) {
                    long queueId = queueAndSocket.getKey();
                    DatagramSocket outputSocket = queueAndSocket.getValue();
                    SocketAddress sendAddress = networkTupleWriter.channelOutputAddr.get(queueId);
                    for (Tuple tuple : tuplesFromDb) {

                        networkTupleWriter.sendTuple(tuple, outputSocket, sendAddress);
                    }
                }
            }
            case PING:
            {
                InetSocketAddress sourceAddress = (InetSocketAddress)command.getSourceAddress();
                Entry<Long, DatagramSocket> queueAndSocket = networkTupleWriter.channelOutputs.get(sourceAddress.getPort());
                if (queueAndSocket != null) {
                    DatagramSocket outputSocket = queueAndSocket.getValue();
                    command.setSendTime(LocalDateTime.now());
                    
                    networkTupleWriter.sendModuleCommand(command, outputSocket, sourceAddress);
                }
            }
        }        
    }
}
