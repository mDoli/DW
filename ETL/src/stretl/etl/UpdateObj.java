package stretl.etl;

import java.util.LinkedList;
import java.util.List;
import stretl.common.Tuple;

/**
 * Shared object for temp values in ETL module application.
 * @author Artur Drzeniek
 */
public class UpdateObj {
    public volatile List<Tuple> backupList = new LinkedList<>();
    public volatile int transformerIndex = 0;
    public volatile boolean continueFlag = true;
    public volatile int failCounter = 0;
    public volatile int passCounter = 0;
    public volatile long actualPing = 0L;
    public volatile int runIdx = 0;
    public volatile boolean failed = false;
    public volatile long startMiliseconds = 0L;
    public volatile long endMiliseconds = 0L;
    
    public void reset() {
        transformerIndex = 0;
        continueFlag = true;
        failCounter = 0;
        passCounter = 0;
        backupList.clear();
        startMiliseconds = 0L;
        endMiliseconds = 0L;
    }
}
