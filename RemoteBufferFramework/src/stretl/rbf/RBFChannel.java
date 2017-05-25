package stretl.rbf;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import stretl.base.BaseModule;
import stretl.base.BaseModuleChannel;
import stretl.common.Tuple;
import stretl.network.NetworkTupleReader;
import stretl.network.NetworkTupleWriter;

/**
 *
 * @author Artur Drzeniek <artudrz156@student.polsl.pl>
 */
public class RBFChannel extends BaseModuleChannel {

    private final LinkedHashMap<Object, Tuple> history;
            
    public RBFChannel(int idChannel, BaseModule baseModule, int historySize) throws IOException {
        super(idChannel, baseModule);
        
        this.history = new LinkedHashMap<Object, Tuple>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, Tuple> entry) {
                return size() > historySize;
            }
        };
        
    }
    
    @Override
    public void run() {
        super.networkTupleReader = new NetworkTupleReader(this);
        super.networkTupleWriter = new NetworkTupleWriter(this);
        networkTupleReader.start();
        networkTupleWriter.start();
        
        while (!Thread.interrupted()) {
            LinkedList<LinkedBlockingQueue<Tuple>> streams = new LinkedList<>(inputQueues.values());
            LinkedBlockingQueue<Tuple> inputQueue = null;
            if (!streams.isEmpty())
                inputQueue = streams.getFirst();
            if (inputQueue != null) {
                try {
                    Tuple tuple = inputQueue.take();
                    if (tuple != null) {
                        Logger.getLogger(RBFChannel.class.getName()).log(Level.INFO, tuple.toString());
                        if (tuple.size() > 2)
                            history.put(tuple.getTimeStamp(), tuple);
                        
                        outputQueues.values().stream().forEach((outputQueue) -> {
                            Tuple tupleCopy = tuple.clone();
                            outputQueue.add(tupleCopy);
                        });
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(RBFChannel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
