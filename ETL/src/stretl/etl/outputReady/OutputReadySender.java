package stretl.etl.outputReady;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic implementation of IOutputReadySender inteface.
 * 
 * @author Artur
 */
public class OutputReadySender implements IOutputReadySender {
    
    /**
     * List of listeners for Output Ready event.
     */
    protected List<IOutputReadyListener> listeners = new ArrayList<>();
    
    @Override
    public void addListener(IOutputReadyListener listener) {
        listeners.add(listener);
    }
    
    @Override
    public void notifyAllListeners(OutputReadyArgs args) {
        listeners.stream().forEach((listener) -> {
            listener.outputReady(this, args);
        });
    }

    @Override
    public List<IOutputReadyListener> getListeners() {
        return listeners;
    }
}
