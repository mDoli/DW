package stretl.base;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import stretl.common.Entry;
import stretl.common.IModule;
import stretl.common.IModuleChannel;
import stretl.common.Tuple;

/**
 * Base module class.
 *
 * @author Artur Drzeniek <artudrz156@student.polsl.pl>
 */
public abstract class BaseModule extends Thread implements IModule {//implements IModuleChannel {

    protected DatagramSocket inputSocket;
    
    protected InetAddress localAddres;
    
    protected int port;
    
    protected String name;
        
    /**
     * Lista kanałów przetwarzania.
     */
    protected ConcurrentHashMap<Long, IModuleChannel> channels;

    /**
     * Identyfikatory kanałów jakie obecnie pracują w tym module RBF.
     */
    protected LinkedList<Long> channelsIds;
    
    public LinkedBlockingQueue<Tuple> inputQueue;
    
    public LinkedBlockingQueue<Tuple> outputQueue;
    
    /**
     *
     * @param name
     * @param id
     */
    protected BaseModule(String name, long id) {
        this.name = name + id;
        this.channels = new ConcurrentHashMap<>();
        this.channelsIds = new LinkedList<>();
    }
    
    public long addChannel(IModuleChannel newChannel) {
               
        channels.put(newChannel.getIdChannel(), newChannel);
        channelsIds.add(newChannel.getIdChannel());
        return newChannel.getIdChannel();
    }

    public void removeChannel(long idChannel) {
        channelsIds.remove(idChannel);
        channels.remove(idChannel);
    }

    public void runChannel(long idChannel) {
        IModuleChannel channel = channels.get(idChannel);
        if (channel != null)
            ((BaseModuleChannel)channel).start();
    }

    public void runAllChannels() {
        channels.values().stream().forEach((channel) -> {
            if (channel != null) 
                ((BaseModuleChannel)channel).start();
        });
    }
    
    public boolean createInput(SocketAddress socketAddress)
    {
        try {
            // TO TU MUSI BYĆ BO TO JEST SOCKET SERWERA - NASŁUCHUJĄCY
            inputSocket = new DatagramSocket(socketAddress);
            inputSocket.setSoTimeout(1000);
            inputQueue = new LinkedBlockingQueue<>();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(BaseModule.class.getName())
                    .log(Level.SEVERE, "Input not created: {0}", ex.getMessage());
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "Module " + super.toString();
    }
    
    protected LinkedBlockingQueue<Tuple> getModuleInputQueue()
    {
        return this.inputQueue;
    }
    
    protected LinkedBlockingQueue<Tuple> getModuleOutputQueue()
    {
        return this.outputQueue;     
    }
//
//    public String getName() {
//        return name;
//    }
    
    public InetAddress getLocalAddress()
    {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {            
            Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Entry<LinkedBlockingQueue<Tuple>, DatagramSocket> getInput() {
        return new Entry<>(inputQueue, inputSocket);
    }

    @Override
    public Integer getInputPort() {
        return port;
    }
    
    
}
