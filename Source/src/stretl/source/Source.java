package stretl.source;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application provide functionality of Source module in StrETL Engine.
 * 
 * @author Artur Drzeniek
 */
public class Source {

    private static InetAddress localHost;
    
    /**
     * @param args the command line arguments.
     * Arguments are address and port to listen tuples.
     */
    public static void main(String[] args) {
        String msg;
        
        try {
            localHost = InetAddress.getLocalHost();        
            msg = "Initalizing Source module at: " + localHost.getHostAddress();
            System.out.println(msg);
            Logger.getLogger(Source.class.getName()).log(Level.INFO, msg);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Source.class.getName()).log(Level.SEVERE, "Source not initalized.", ex);
            return;
        }
        
        String address = localHost.getHostName();
        int port = 60000;
        LinkedList<Integer> outPorts = new LinkedList<>();
        
        if (args.length > 0) // Input address
            address = args[0];
        if (args.length > 1) // Input port
            port = Integer.decode(args[1]);
        if (args.length > 2) // Output ports
        {
            for (int i = 2; i < args.length; i++) {
                outPorts.add(Integer.decode(args[i]));
            }
        }
        
        InetSocketAddress addressToBound = new InetSocketAddress(address, port);
        
        try {
            msg = "Initalizing bound to " + address + ":" + port;
            System.out.println(msg);
            Logger.getLogger(Source.class.getName()).log(Level.INFO, msg);
            
            SourceModule module = new SourceModule(addressToBound);
            if (module.datagramSocket.isBound())
            {
                msg = "Bound to " + address + ":" + port;
                System.out.println(msg);
                Logger.getLogger(Source.class.getName()).log(Level.INFO, msg);
                
                for (Integer outPort : outPorts) {
                    createLocalhostOutput(module, outPort);
                }
                
                module.start();
            }
            else
            {
                msg = "Could not bound to " + address + ":" + port;
                System.out.println(msg);
                Logger.getLogger(Source.class.getName()).log(Level.INFO, msg);
            }
        } catch (SocketException ex) {
            msg = "Exception when bounding to " + address + ":" + port;
            System.err.println(msg);
            Logger.getLogger(Source.class.getName()).log(Level.SEVERE, msg, ex);
        }
        
    }
    
    private static void createLocalhostOutput(SourceModule module, Integer outPort) {
        module.createChannelOutput(outPort);
        String msg = "Output created at " + localHost + ":" + outPort;
        System.out.println(msg);
        Logger.getLogger(Source.class.getName()).log(Level.INFO, msg);        
    }
    
}
