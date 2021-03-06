package stretl.etl.transformer;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import stretl.common.Tuple;
import stretl.etl.outputReady.OutputReadyArgs;
import stretl.etl.outputReady.IOutputReadySender;
import stretl.etl.schema.SchemaElement;

/**
 * Data transformer class.
 * @author Artur Drzeniek
 */
public abstract class Transformer extends SchemaElement {

    /**
     * Transformer base constructor.
     * @param schemaId schema identifier.
     */
    public Transformer(int schemaId) {
        super(schemaId);
    }
    
    public Transformer(int schemaId, boolean isSuccessElement)
    {
        super(schemaId, isSuccessElement);
    }
    
    /**
     * Transforms data when implemented.
     * @return TRUE if transform end with success, otherwhise FALSE.
     */
    public abstract boolean transform();
    
    @Override
    public void outputReady(IOutputReadySender sender, OutputReadyArgs args) {
        input = new LinkedList<>();
        // Wybieranie tylko tych krotek ktore jeszcze tu nie były
        for (Tuple tuple : args.data) {
            if (tuple.getSchemaElementId() != elementId)
                input.add(tuple);
        }
        
        //Logger.getLogger(this.getClass().getSimpleName()).log(Level.INFO, "Transforming {0} tuples.", input.size());
        
        success = transform();
        
        // Transformowane byly tylko te ktore tu jeszcze nie 
        // byly a teraz na wyjscie ida wszystkie ktore tu weszły
        output = args.data;
        
        if (success) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.FINE, "Transform completed.");
            
            output.stream().forEach((tuple) ->  tuple.setSchemaElementId(elementId));
            
            notifyAllListeners(new OutputReadyArgs(output)); 
        }
        else {
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING, "Transform failed.");
        }
    }
    
    @Override
    public String toString() {
        return "Transformer{" + this.getClass().getSimpleName()+ '}';
    }
}
