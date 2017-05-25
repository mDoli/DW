package stretl.etl.transformer;

import stretl.common.Tuple;

/**
 * Implementacja elementu Transformacji.
 * @author Artur Drzeniek
 */
public class TransformerImpl extends Transformer {

    public TransformerImpl(int schemaId) {
        super(schemaId);
    }
    
    public TransformerImpl(int schemaId, boolean isSuccessElement) {
        super(schemaId, isSuccessElement);
    }

    @Override
    public boolean transform() {        
        try {
            for (Tuple tuple : input) {
                Thread.sleep(2);
                tuple.setSchemaElementId(elementId);
            }
        } catch (InterruptedException ex) {
            //Logger.getLogger(TransformerImpl.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return isSuccessElement;
    }    
}
