package stretl.etl.outputReady;

import java.util.LinkedList;
import java.util.List;
import stretl.common.Tuple;

/**
 *
 * @author Artur
 */
public class OutputReadyArgs {
    
    /**
     * Transformer output data.
     */
    public List<Tuple> data;
    
    public OutputReadyArgs(List<Tuple> data)
    {
        this.data = new LinkedList<>();
        this.data = data;
    }
}
