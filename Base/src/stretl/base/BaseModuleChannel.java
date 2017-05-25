package stretl.base;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import stretl.common.Entry;
import stretl.common.Enums;
import stretl.common.IModuleChannel;
import stretl.common.Tuple;
import stretl.network.NetworkTupleReader;
import stretl.network.NetworkTupleWriter;

/**
 * Base Module Channel.
 *
 * @author Artur Drzeniek <artudrz156@student.polsl.pl>
 */
public abstract class BaseModuleChannel extends Thread implements IModuleChannel {

    protected long channelId;
    
    protected BaseModule baseModule;

    protected ConcurrentHashMap<Long, LinkedBlockingQueue<Tuple>> inputQueues;
    protected ConcurrentHashMap<Long, LinkedBlockingQueue<Tuple>> outputQueues;
    
    protected LinkedHashMap<Integer, Entry<Long, DatagramSocket>> channelOutputs = new LinkedHashMap<>();    
    protected LinkedHashMap<Integer, Entry<Long, DatagramSocket>> channelInputs = new LinkedHashMap<>();
    
    public LinkedHashMap<Long, SocketAddress> channelOutputAddr = new LinkedHashMap<>(); 
    
    protected int[] outputPorts;
    protected int[] inputPorts;
    
    protected LinkedList<Long> inputQueueIds = new LinkedList<>();
    
    protected LinkedHashMap<Long, ServerSocket> servers = new LinkedHashMap<>();

    public Enums.ChannelState channelState = Enums.ChannelState.UNKNOWN;
    
    public NetworkTupleReader networkTupleReader = null;    
    public NetworkTupleWriter networkTupleWriter = null;
    
    protected BaseModuleChannel() {
        this.channelId = 0L;
        this.baseModule = null;
        this.inputQueues = new ConcurrentHashMap<>();
        this.outputQueues = new ConcurrentHashMap<>();
    }

    protected BaseModuleChannel(int idChannel, BaseModule baseModule) {
        this.channelId = idChannel;        
        this.baseModule = baseModule;
        this.inputQueues = new ConcurrentHashMap<>();
        this.outputQueues = new ConcurrentHashMap<>();
    }
    
    protected BaseModuleChannel(String name, long id, BaseModule baseModule) {
        super.setName(name);
        this.channelId = id;
        this.baseModule = baseModule;
        this.inputQueues = new ConcurrentHashMap<>();
        this.outputQueues = new ConcurrentHashMap<>();
    }

    @Override
    public long getIdChannel() {
        return channelId;
    }

    @Override
    public ConcurrentHashMap<Long, LinkedBlockingQueue<Tuple>> getInputQueues() {
        return inputQueues;
    }

    @Override
    public ConcurrentHashMap<Long, LinkedBlockingQueue<Tuple>> getOutputQueues() {
        return outputQueues;
    }

    @Override
    public LinkedBlockingQueue<Tuple> getInputQueue(long queueId) {        
        return inputQueues.getOrDefault(queueId, null);
    }

    @Override
    public LinkedBlockingQueue<Tuple> getOutputQueue(long queueId) {        
        return outputQueues.getOrDefault(queueId, null);
    }

    @Override
    public long createOutputQueue() {
        long queueId = outputQueues.size();
        outputQueues.put(queueId, new LinkedBlockingQueue<>());
        return queueId;
    }
    
    public long createInputQueue() {
        long queueId = inputQueues.size();
        inputQueueIds.add(queueId);
        inputQueues.put(queueId, new LinkedBlockingQueue<>());
        return queueId;
    }

    @Override
    public void deleteInputQueue(long idStream) {
        inputQueues.remove(idStream);
    }

    @Override
    public void deleteOutputQueue(long idStream) {
        outputQueues.remove(idStream);
    }

    public LinkedList<Long> getInputQueueIds() {
        return inputQueueIds;
    }
    
    public boolean createChannelInput(SocketAddress socketAddress) throws SocketTimeoutException {        
        Long queueId = createInputQueue();
        return createChannelInput(queueId, socketAddress);
    }
    
    public boolean createChannelInput(Long inputQueueId, SocketAddress socketAddress) throws SocketTimeoutException
    {
        try {
            // TO TU MUSI BYĆ BO TO JEST SOCKET SERWERA - NASŁUCHUJĄCY
            DatagramSocket socket = new DatagramSocket(socketAddress);
            socket.setSoTimeout(100);
            Entry<Long, DatagramSocket> inputEntry = new Entry<>(inputQueueId, socket);
            channelInputs.put(((InetSocketAddress)socketAddress).getPort(), inputEntry);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(BaseModuleChannel.class.getName()).log(Level.SEVERE, "Input not created: {0}", ex.getMessage());
            return false;
        }
    }

    public InetSocketAddress createChannelOutput(int port)
    {        
        try {    
            InetSocketAddress sendAddress = new InetSocketAddress(InetAddress.getLoopbackAddress(), port);
            
// W sokecie klienta nie moze byc adresy, Ten adres musi byc podany w datagramie
            DatagramSocket client = new DatagramSocket();
            
            Long queueId = createOutputQueue();
            channelOutputAddr.put(queueId, sendAddress);
            Entry<Long, DatagramSocket> outputEntry = new Entry<>(queueId, client);
            channelOutputs.put(port, outputEntry);
            
            return sendAddress;
        } catch (IOException ex) {
            Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
            return null;
        }        
    }        

    @Override
    public LinkedHashMap<Integer, Entry<Long, DatagramSocket>> getChannelOutputs() {
        return channelOutputs;
    }

    @Override
    public LinkedHashMap<Integer, Entry<Long, DatagramSocket>> getChannelInputs() {
        return channelInputs;
    }

    public LinkedHashMap<Long, SocketAddress> getChannelOutputAddr() {
        return channelOutputAddr;
    }
    
    
}
