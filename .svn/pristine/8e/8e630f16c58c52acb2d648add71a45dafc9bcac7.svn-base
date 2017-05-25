package stretl.rif;

import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import stretl.base.BaseModule;
import stretl.common.Entry;
import stretl.common.Tuple;

/**
 *
 * @author Artur
 */
public class RIFModule extends BaseModule {

    public RIFModule(long idModule) 
    {        
        super("RIF", idModule);
    }
    
    @Override
    public void run() {
        super.runAllChannels();

        while (!Thread.interrupted()) {
            if (channels.isEmpty())
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(this.getName())
                        .log(Level.WARNING, "Waiting interrupted.", ex);
                }
            }
            else {
                // 12.03.16
//                for (Map.Entry<Integer, Entry<Long, DatagramSocket>> entry : channelInputs.entrySet()) {
//                    Integer port = entry.getKey();
//                    Entry<Long, DatagramSocket> input = entry.getValue();                    
//                    DatagramSocket socket = input.getValue();
//                    if (socket.isClosed())
//                    {
//                        // Try reconnect
//                    }
//                    else
//                    {
//                        // Get statistics
//                    }
//                }
//                
//                for (Map.Entry<Integer, Entry<Long, DatagramSocket>> entry : channelOutputs.entrySet()) {
//                    Integer port = entry.getKey();
//                    Entry<Long, DatagramSocket> input = entry.getValue();                    
//                    DatagramSocket socket = input.getValue();
//                    
//                    if (socket.isClosed())
//                    {
//                        // Try reconnect
//                    }
//                    else
//                    {
//                        // Get statistics
//                    }
//                }
                
                // To delete
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(this.getName())
                        .log(Level.WARNING, "Waiting interrupted.", ex);
                }
                
                //OLD TODO: Zapisanie wszystkich krotek z input queue do pliku
                //writeTupleToFile();
                // Przepisanie do kolejki wyjściowej
                //12.03 inputQueue.drainTo(moduleOutput);
                
//                for (Tuple tuple : inputQueue) {
//                    if (!getModuleOutputQueue().contains(tuple)) {
//                        //System.out.println("Received " + tuple + " at RIF module");
//                        getModuleOutputQueue().add(tuple);
//                    }
//                    else
//                        tuple = null;
//                }
                
                // Kasowanko
                //getModuleOutputQueue().clear();
            }
        }
    }    

}
