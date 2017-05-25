package stretl.common;

import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import stretl.common.Entry;
import stretl.common.Tuple;

/**
 * Interface of Module Channel
 *
 * @author Artur Drzeniek <artudrz156@student.polsl.pl>
 */
public interface IModuleChannel {

    public long getIdChannel();

    public ConcurrentHashMap<Long, LinkedBlockingQueue<Tuple>> getInputQueues();

    public ConcurrentHashMap<Long, LinkedBlockingQueue<Tuple>> getOutputQueues();

    public LinkedBlockingQueue<Tuple> getInputQueue(long idStream);

    public LinkedBlockingQueue<Tuple> getOutputQueue(long idStream);

    public long createOutputQueue();

    public void deleteInputQueue(long idStream);

    public void deleteOutputQueue(long idStream);
    
    public LinkedHashMap<Integer, Entry<Long, DatagramSocket>> getChannelOutputs();
    
    public LinkedHashMap<Integer, Entry<Long, DatagramSocket>> getChannelInputs();
    
    public LinkedHashMap<Long, SocketAddress> getChannelOutputAddr();
}
