package stretl.etl.schema;

import java.util.LinkedList;
import java.util.List;
import stretl.common.Tuple;
import stretl.etl.outputReady.IOutputReadyListener;
import stretl.etl.outputReady.OutputReadySender;

/**
 * Basic schema element class.
 * @author Artur
 */
public abstract class SchemaElement extends OutputReadySender implements IOutputReadyListener {
    
    public int schemaId;    
    public int elementId;    
    public EtlSchema parentSchema;    
    public boolean success;
    protected boolean isSuccessElement = true;
    
    /**
     * Input tuple list.
     */
    public List<Tuple> input;
    
    /**
     * Input tuple list.
     */
    public List<Tuple> output;
    
    public SchemaElement(int schemaId)
    {
        this.schemaId = schemaId;
        this.output = new LinkedList<>();
    }
    
    public SchemaElement(int schemaId, boolean isSuccessElement)
    {
        this(schemaId);
        this.isSuccessElement = isSuccessElement;
    }
    
    public void setElementId(int id) {
        this.elementId = id;
    }
    
    public void setParentSchema(EtlSchema schema)
    {
        this.parentSchema = schema;
    }
    
    @Override
    public String toString()
    {
        return String.format("{0}({1})", this.getClass().getName(), elementId);
    }
}
