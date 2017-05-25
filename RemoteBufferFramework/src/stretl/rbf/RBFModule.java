package stretl.rbf;

import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import stretl.base.BaseModule;
import stretl.common.Entry;
import stretl.common.Tuple;

/**
 * Remote Buffer Framework Module Class.
 * 
 * 
 * @author Artur Drzeniek <artudrz156@student.polsl.pl>
 */
public class RBFModule extends BaseModule {

    @Override
    public void run() {
        super.runAllChannels();
        
        while (!Thread.interrupted()) {
            if (channels.size() > 0)
            {
                try {
                    Thread.sleep(1000);
                    
                    // Możliwe rozszerzone działanie RBF
                } catch (InterruptedException ex) {
                    Logger.getLogger(RBFModule.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public RBFModule(long idModule) {
        super("RBF-", idModule);
    }   

}
