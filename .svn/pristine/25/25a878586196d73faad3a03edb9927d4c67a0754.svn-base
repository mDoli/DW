package stretl.etl.outputReady;

import java.util.List;

/**
 * Event sender interface.
 * 
 * @author Artur Drzeniek <artudrz156@student.polsl.pl>
 * @since 2016
 */
public interface IOutputReadySender {
       
    /**
     * Adds new listener object.
     * @param listener Listener object.
     */
    public void addListener(IOutputReadyListener listener);
    
    /**
     * Notifies all interested listeners.
     * @param args Arguments passed to listeners.
     */
    public void notifyAllListeners(OutputReadyArgs args);
    
    /**
     * Gets listeners.
     * @return Listeners collection.
     */
    public List<IOutputReadyListener> getListeners();
}
