/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stretl.rif;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import stretl.common.Entry;

/**
 * Zdalny bufor integrujący.
 * Tworzy moduł.
 * Tworzy n kanałów transmisji (w tym przypadku 1) lol
 * W kanale tworzy 1 wejście i n wyjść
 * 
 * 
 * @author Artur
 */
public class RemoteIntegratorFramework {

    private static int ETL_INPUT = 60300;
    
    private static InetAddress localHost;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String msg;
        try {
            localHost = InetAddress.getLocalHost();
            msg = "Initalizing RBF at: " + localHost.getHostAddress();
            System.out.println(msg);
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(RemoteIntegratorFramework.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        // Args: 0 = ETL_INPUT = RIF_OUTPUT
        if (args.length > 0)
            ETL_INPUT = Integer.decode(args[0]);
        
        LinkedList<Entry> connectedStreams = new LinkedList<>();
        
        for (int i = 1; i < args.length; i += 2) {
            connectedStreams.add(new Entry<Integer, String>(Integer.decode(args[i]), args[i + 1]));
        }
        
        RIFModule module = new RIFModule(Thread.currentThread().getId());
        
        int channelNumber = 0;
        RIFChannel rifChannel = new RIFChannel(channelNumber, module);
        rifChannel.createInputQueue();
        module.addChannel(rifChannel);
        
        for (Entry<Integer, String> stream : connectedStreams) {
            int port = stream.getKey();
            String address = stream.getValue();
            try {
                InetSocketAddress socketAddress = new InetSocketAddress(address, port);
                
                msg = "Initalizing bound to " + address + ":" + port;    
                System.out.println(msg);
                Logger.getLogger(RemoteIntegratorFramework.class.getName()).log(Level.INFO, msg);
                
                boolean connected = rifChannel.createChannelInput(0L, socketAddress);
                if (connected) {
                    msg = "Bound to " + address + ":" + port;
                    System.out.println(msg);
                    //Logger.getLogger(RemoteBufferFramework.class.getName()).log(Level.INFO, msg);
                    createLocalhostOutput(rifChannel, ETL_INPUT);
//                    for (int i = 0; i < 3; i++) {
//                        createLocalhostOutput(rifChannel, START_OUTPUT_PORT + ((channelNumber * 10) + i));
//                    }
                }
                else
                {
                    msg = "Could not bound to " + address + ":" + port;
                    System.out.println(msg);
                    //Logger.getLogger(RemoteBufferFramework.class.getName()).log(Level.WARNING, msg);
                }
            } catch (IOException ex) {
                msg = "Exception when bounding to " + address + ":" + port;
                System.err.println(msg);
                Logger.getLogger(RemoteIntegratorFramework.class.getName()).log(Level.SEVERE, msg, ex);
            }
            channelNumber++;
        }
        
        module.run();
    }
    
    private static void createLocalhostOutput(RIFChannel channel, int outPort) {
        channel.createChannelOutput(outPort);
        String msg  = "Created output at " + localHost + ":" + outPort;
        System.out.println(msg);
        Logger.getLogger(RemoteIntegratorFramework.class.getName()).log(Level.INFO, msg);        
    }
}
