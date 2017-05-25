package stretl.etl.backup;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import stretl.dataprovider.DataProvider;
import stretl.etl.outputReady.OutputReadyArgs;
import stretl.etl.outputReady.IOutputReadyListener;
import stretl.etl.outputReady.IOutputReadySender;
import stretl.etl.schema.SchemaElement;

/**
 * Basic class representing module element that create check point of data from input.
 * @author Artur Drzeniek
 */
public class CheckPoint extends SchemaElement implements IOutputReadyListener {
        
    protected DataProvider dataProvider = new DataProvider();

    public CheckPoint(int schemaId) {
        super(schemaId);
    }
    
    @Override
    public void outputReady(IOutputReadySender sender, OutputReadyArgs args) {
        input = args.data;
        if (!input.isEmpty() )
            createBackup();
    }
    
    private boolean createBackup() {
        // Random saving delay
//        try {
//            Thread.sleep((int)(Math.random() * input.size()));
//        } catch (InterruptedException ex) {
//            Logger.getLogger(CheckPoint.class.getName()).log(Level.SEVERE, null, ex);
//        }

        try {            
            dataProvider.saveBackup(input, schemaId);
        } catch (SQLException e) {
            Logger.getLogger(this.getClass().getName())
                    .log(Level.SEVERE, String.format("Create backup failed in {0}", this.toString()) , e);
            return false;
        }
        return true;
    }
}
