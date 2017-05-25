
package stretl.rbf;

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
 * RemoteBufferFramework.
 * Domyślnie dla każdego kanału tworzy 3 wyjścia.
 * @author Artur
 */
public class RemoteBufferFramework {
    private static final int BUFFER_HISTORY_SIZE = 1000;
    private static final int START_OUTPUT_PORT = 60200;
    
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
            Logger.getLogger(RemoteBufferFramework.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        LinkedList<Entry> sources = new LinkedList<>();
        
        if (args.length > 1) sources.add(new Entry<Integer, String>(Integer.decode(args[0]), args[1]));
        if (args.length > 3) sources.add(new Entry<Integer, String>(Integer.decode(args[2]), args[3]));
        if (args.length > 5) sources.add(new Entry<Integer, String>(Integer.decode(args[4]), args[5]));
        
        RBFModule module = new RBFModule(Thread.currentThread().getId());
        int channelNumber = 0;
        for (Entry<Integer, String> source : sources) {
            int port = source.getKey();
            String address = source.getValue();
            try {
                RBFChannel rbfChannel = new RBFChannel(port, module, BUFFER_HISTORY_SIZE);
                
                InetSocketAddress socketAddress = new InetSocketAddress(address, port);
                
                msg = "Initalizing bound to " + address + ":" + port;    
                System.out.println(msg);
                Logger.getLogger(RemoteBufferFramework.class.getName()).log(Level.INFO, msg);
                
                boolean connected = rbfChannel.createChannelInput(socketAddress);
                if (connected) {
                    msg = "Bound to " + address + ":" + port;
                    System.out.println(msg);
                    //Logger.getLogger(RemoteBufferFramework.class.getName()).log(Level.INFO, msg);
                        
                    for (int i = 0; i < 3; i++) {
                        createLocalhostOutput(rbfChannel, START_OUTPUT_PORT + ((channelNumber * 10) + i));
                    }
                    
                    module.addChannel(rbfChannel);
                }
                else
                {
                    rbfChannel = null;
                    msg = "Could not bound to " + address + ":" + port;
                    System.out.println(msg);
                    //Logger.getLogger(RemoteBufferFramework.class.getName()).log(Level.WARNING, msg);
                }
            } catch (IOException ex) {
                msg = "Exception when bounding to " + address + ":" + port;
                System.err.println(msg);;
                Logger.getLogger(RemoteBufferFramework.class.getName()).log(Level.SEVERE, msg, ex);
            }
            channelNumber++;
        }
        
        module.run();
    }
    
    private static void createLocalhostOutput(RBFChannel channel, int outPort) {
        channel.createChannelOutput(outPort);
        String msg  = "Created output at " + localHost + ":" + outPort;
        System.out.println(msg);
        Logger.getLogger(RemoteBufferFramework.class.getName()).log(Level.INFO, msg);        
    }
    
}
