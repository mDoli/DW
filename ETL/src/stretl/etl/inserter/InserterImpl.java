package stretl.etl.inserter;

import java.util.logging.Level;
import java.util.logging.Logger;
import stretl.common.Tuple;

/**
 * Implementacja elementu insetora.
 * @author Artur Drzeniek.
 */
public class InserterImpl extends Inserter {

    /**
     * Konstruktor modułu insertora.
     * @param schemaId Identyfikator schematu.
     */
    public InserterImpl(int schemaId) {
        super(schemaId);
    }
    
    @Override
    public boolean insert() {
        try {
            for (Tuple tuple : input) {
                if (tuple != null) {
                    //Logger.getLogger(this.getClass().getSimpleName()).log(Level.INFO, "{0} inserted", tuple.toString());
                    long opening = tuple.getOpeningTimestamp();
                    long closing = System.currentTimeMillis();
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Tuple from {0} travel time {1}",
                            new Object[] { tuple.getSource(), closing - opening });
                    tuple = null;
                }
            }
        } catch (Exception ex) { return false; }
        return true;
    }
}
