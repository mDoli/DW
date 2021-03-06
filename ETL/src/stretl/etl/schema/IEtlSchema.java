package stretl.etl.schema;

import java.util.List;
import stretl.common.Tuple;

/**
 *
 * @author Artur
 */
public interface IEtlSchema {
    public void build();
    public boolean process();
    public void setSource(List<Tuple> source);
    public Integer getId();
}
