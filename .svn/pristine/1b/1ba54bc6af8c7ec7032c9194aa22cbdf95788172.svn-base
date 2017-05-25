package stretl.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import stretl.common.CommandTuple;
import stretl.common.Entry;
import stretl.common.IModuleChannel;
import stretl.common.Tuple;


/**
 * Network Tuple Writer thread class.
 * @author Artur
 */
public class NetworkTupleWriter extends Thread {    
    
    private static final Logger LOGGER = Logger.getLogger(NetworkTupleWriter.class.getName());
    
    /**
     * Channel outputs.
     * Key: port, Value: entry(queueId, socket)
     */
    public LinkedHashMap<Integer, Entry<Long, DatagramSocket>> channelOutputs;
    
    /**
     * Key: queueId, Value: socketAddress
     */
    public LinkedHashMap<Long, SocketAddress> channelOutputAddr;
    
    /**
     * Channel containing this writer.
     */
    public IModuleChannel baseChannel;
    
    public NetworkTupleWriter()
    {
    }
    
    /**
     * Create network tuple writer object. It writes tuples to channel outputs.
     * @param channel Channel containing this NTW.
     */
    public NetworkTupleWriter(IModuleChannel channel) {
        if (channel == null) return;
                
        if (channel.getChannelOutputs() != null)
            this.channelOutputs = channel.getChannelOutputs();
        else
            this.channelOutputs = new LinkedHashMap<>();
        
        if (channel.getChannelOutputAddr() != null)
            this.channelOutputAddr = channel.getChannelOutputAddr();
        else
            this.channelOutputAddr = new LinkedHashMap<>();
        
        this.baseChannel = channel;
    }
    
    @Override
    public void run()
    {
        if (baseChannel == null) return;
        
        while(!Thread.interrupted()) 
        {                    
            if (channelOutputs.isEmpty())
            {
                LOGGER.log(Level.INFO, "Outputs are empty");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.WARNING, "Waiting writer interrupeted", ex);
                }
            }
            else
            {
                for (Map.Entry<Integer, Entry<Long, DatagramSocket>> entry : channelOutputs.entrySet()) 
                {
                    try 
                    {
                        Integer port = entry.getKey();                                
                        Entry<Long, DatagramSocket> inputEntry = entry.getValue();                                

                        Long queueId = inputEntry.getKey();
                        DatagramSocket client = inputEntry.getValue();

                        if (client.isClosed()) continue;
                                                 
                        LinkedBlockingQueue<Tuple> outputQueue = baseChannel.getOutputQueue(queueId);
                        if (outputQueue == null) continue;
                                                         
                        Tuple tuple = outputQueue.take();
                        if (tuple == null) continue;
                        SocketAddress address = channelOutputAddr.get(queueId);
                        sendTuple(tuple, client, address);                                
                        
                    } catch (InterruptedException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }
                }                          
            }
        }
        
        // Close connections
        for (Entry<Long, DatagramSocket> entry : channelOutputs.values()) {
            try (DatagramSocket socket = entry.getValue()) {
                socket.close();
            }
        }
    }
        
    /**
     * Method used to send tuple to other module on defined address.
     * @param tuple Tuple to send.
     * @param datagramSocket
     * @param address
     * @return Returns True if send finished with success, else False.
     */
    public boolean sendTuple(Tuple tuple, DatagramSocket datagramSocket, SocketAddress address)
    {
        try {
            byte[] byteTuple = Tuple.serialize(tuple); 
            // Tu nie może byc adresu w datagramie
            //SocketAddress address = datagramSocket.getLocalSocketAddress();
            DatagramPacket packet = new DatagramPacket(byteTuple, byteTuple.length, address);
            packet.setPort(((InetSocketAddress)address).getPort());
            datagramSocket.send(packet);
            return true;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Method used to send command to other module on localhost.
     * @param command ModuleCommand to send.
     * @param datagramSocket Output socket.
     * @param port Destination port.
     * @return Returns True if send finished with success, else False.
     */
    public boolean sendModuleCommand(CommandTuple command, DatagramSocket datagramSocket, int port)
    {        
        try {
            SocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), port);
            return sendModuleCommand(command, datagramSocket, address);            
        } catch (UnknownHostException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Method used to send command to other module on defined address.
     * @param command ModuleCommand to send.
     * @param datagramSocket Output socket.
     * @param address Destination adress.
     * @return Returns True if send finished with success, else False.
     */
    public boolean sendModuleCommand(CommandTuple command, DatagramSocket datagramSocket, SocketAddress address)
    {
        try {
            byte[] byteCommand = Tuple.serialize(command);            
            DatagramPacket packet = new DatagramPacket(byteCommand, byteCommand.length, address);
            if (datagramSocket.isClosed())
                return false;
            
            datagramSocket.send(packet);
            return true;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
