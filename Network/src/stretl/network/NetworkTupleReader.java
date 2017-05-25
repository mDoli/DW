package stretl.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import stretl.common.CommandTuple;
import stretl.common.Entry;
import stretl.common.IModule;
import stretl.common.IModuleChannel;
import stretl.common.Tuple;

/**
 *
 * @author Artur
 */
public class NetworkTupleReader extends Thread {
        
    public LinkedHashMap<Integer, Entry<Long, DatagramSocket>> channelInputs;
    public Entry<LinkedBlockingQueue<Tuple>, DatagramSocket> moduleInput;
    
    public IModuleChannel baseChannel;
    public IModule baseModule;
    
    public LinkedBlockingQueue<CommandTuple> moduleCommands = new LinkedBlockingQueue<>();
    
    public NetworkTupleReader(IModuleChannel channel) {
        if (channel == null) return;
        
        if (channel.getChannelInputs() != null)
            this.channelInputs = channel.getChannelInputs();
        else
            this.channelInputs = new LinkedHashMap<>();
        
        this.baseChannel = channel;
    }
    
    public NetworkTupleReader(IModule module)
    {
        if (module == null) return;
        if (module.getInput()!= null) {
            this.moduleInput = module.getInput();
        }
        this.baseModule = module;
    }
    
    @Override
    public void run(){
        
        if (baseChannel != null) {        
            while(!Thread.interrupted()) 
            {
                if (channelInputs.isEmpty())
                {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(this.getName())
                                .log(Level.WARNING, "Waiting reader interrupeted", ex);
                    }
                }
                else
                {
                    for (Map.Entry<Integer, Entry<Long, DatagramSocket>> entry : channelInputs.entrySet()) 
                    {
                        Integer port = entry.getKey();
                        Entry<Long, DatagramSocket> inputEntry = entry.getValue();  
                        Long queueId = inputEntry.getKey();
                        DatagramSocket socket = inputEntry.getValue(); 

                        LinkedBlockingQueue<Tuple> inputQueue = baseChannel.getInputQueue(queueId);                    
                        if (inputQueue == null) continue;
                        
                        Object tuple = receiveObject(socket);
                        
                        if (tuple instanceof CommandTuple)
                        {
                            //Logger.getLogger(this.getName()).log(Level.INFO, "Recived command at " + socket.getPort());
                            moduleCommands.add((CommandTuple)tuple);
                        }
                        else if (tuple instanceof Tuple) {
                            //Logger.getLogger(this.getName()).log(Level.INFO, "Recived tuple " + tuple.toString());
                            inputQueue.add((Tuple)tuple);
                        }
//                        
//                        // Odebranie komendy dla modułu
//                        CommandTuple command = receiveModuleCommand(socket, port);
//                        if (command != null)
//                            moduleCommands.add(command);
//
//                        // Odebranie krotki
//                        
//
//                        Tuple tuple = receiveTuple(socket);
//                        if (tuple == null) continue;
//
//                        inputQueue.add(tuple);                    
                    }
                }
            }
            // Close connections
            for (Entry<Long, DatagramSocket> entry : channelInputs.values()) {
                try (DatagramSocket socket = entry.getValue()) {
                    socket.close();
                }
            }
        }
        else if (baseModule != null)
        {
            while(!Thread.interrupted()) {
                LinkedBlockingQueue<Tuple> queue = moduleInput.getKey();
                DatagramSocket socket = moduleInput.getValue();
                
                Object tuple = receiveObject(socket);
                
                if (tuple instanceof CommandTuple)
                {
                    //Logger.getLogger(this.getName()).log(Level.INFO, "Recived command at " + socket.getPort());
                    moduleCommands.add((CommandTuple)tuple);
                }
                else if (tuple instanceof Tuple) {
                    //Logger.getLogger(this.getName()).log(Level.INFO, "Recived tuple at " + socket.getPort());
                    queue.add((Tuple)tuple);
                }
            }
            
            try (DatagramSocket socket = moduleInput.getValue()) {
                socket.close();
            }            
        }        
    }
    
    public Object receiveObject(DatagramSocket socket) {
        Object tuple = null;
        try {            
            byte[] byteTuple = new byte[512];            
            SocketAddress addr = socket.getLocalSocketAddress();
            DatagramPacket packet = new DatagramPacket(byteTuple, byteTuple.length);
            
            try {
                if (!socket.isClosed())
                    socket.receive(packet);
                //Logger.getLogger(this.getName()).log(Level.INFO, "Recived from " + ((InetSocketAddress)addr).getPort());
            } catch (SocketTimeoutException e) {
                //Logger.getLogger(this.getName()).log(Level.WARNING, "Timeout at " + ((InetSocketAddress)addr).getPort());
                return null;
            }    
            catch (SocketException e) {
                Logger.getLogger(this.getName()).log(Level.WARNING, "Socket closed at " + ((InetSocketAddress)addr).getPort());
                
                return null;
            }
            tuple = Tuple.deserializeToObj(byteTuple);
        } catch (ClassNotFoundException | IOException ex) {
            Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
        }
        return tuple;
    }
    
    public Tuple receiveTuple(DatagramSocket socket) {
        Tuple tuple = null;
        try {            
            byte[] byteTuple = new byte[512];            
            SocketAddress addr = socket.getLocalSocketAddress();
            DatagramPacket packet = new DatagramPacket(byteTuple, byteTuple.length);
            
            try {
                socket.receive(packet);
                Logger.getLogger(this.getName()).log(Level.INFO, "Recived from " + ((InetSocketAddress)addr).getPort());
            } catch (SocketTimeoutException e)
            {
                Logger.getLogger(this.getName()).log(Level.SEVERE, "Timeout at " + ((InetSocketAddress)addr).getPort());
                return null;
            }            
            tuple = Tuple.deserialize(byteTuple);
        } catch (ClassNotFoundException | IOException ex) {
            Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
        }
        return tuple;
    }
    
    public Tuple receiveTupleWithConnect(DatagramSocket socket) {
        Tuple tuple = null;
        if (socket == null) return tuple;
        try {            
            byte[] byteTuple = new byte[512];            
            SocketAddress addr = new InetSocketAddress(InetAddress.getLoopbackAddress(), 60200);
            DatagramPacket packet = new DatagramPacket(byteTuple, byteTuple.length);
            
            if (socket.isClosed()) return tuple;  
            try {
                
                socket.receive(packet);
                
                if (!socket.isConnected())
                    socket.connect(addr);
            
                tuple = Tuple.deserialize(byteTuple);
            } catch (SocketTimeoutException ex)
            {}
            
        } catch (ClassNotFoundException | IOException ex) {
            Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
        }
        return tuple;
    }
//    
//    public ModuleCommand receiveModuleCommand(DatagramSocket socket, int port) {
//        SocketAddress address = new InetSocketAddress(socket.getLocalAddress(), port);
//        return receiveModuleCommand(socket);            
//    }
//    
//    public ModuleCommand receiveModuleCommand(DatagramSocket socket) {
//        ModuleCommand command = null;
//        try {
//            byte[] byteCommand = new byte[2048];
//            DatagramPacket packet = new DatagramPacket(byteCommand, byteCommand.length);
//            //socket.setSoTimeout(2);
//            try {
//                socket.receive(packet);
//                command = ModuleCommand.serialize(byteCommand);
//            } catch (SocketTimeoutException e)
//            {
//                // No module command
//            }
//            //finally { socket.setSoTimeout(0); }
//        }
//        catch (ClassNotFoundException | IOException ex) {
//            Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
//        }
//        return command;
//    }

    @Override
    public void interrupt() {
        if (channelInputs != null)
        {
            for (Entry<Long, DatagramSocket> entry : channelInputs.values()) {
                try (DatagramSocket socket = entry.getValue()) {
                    if (socket != null)
                        socket.close();
                }
            }
        }
        if (moduleInput != null)
        {
            try (DatagramSocket socket = moduleInput.getValue()) 
            {
                if (socket != null)
                    socket.close();
            }   
        }
        super.interrupt(); 
    }
    
    
}
