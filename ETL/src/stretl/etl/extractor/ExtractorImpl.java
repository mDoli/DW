package stretl.etl.extractor;

import java.util.List;
import stretl.common.Tuple;
import stretl.etl.UpdateObj;
import stretl.etl.outputReady.IOutputReadySender;
import stretl.etl.outputReady.OutputReadyArgs;


public class ExtractorImpl extends Extractor {

    public ExtractorImpl(List<Tuple> source, UpdateObj updateObj, int schemaId) {
        super(source, updateObj, schemaId);
    }


    @Override
    public boolean extract() {
        if (input == null) return false;
        
        if (input.isEmpty())
        {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                
            }
        }
        
        if (updateObj.runIdx == 0 && !updateObj.backupList.isEmpty()) {
            input.addAll(updateObj.backupList);
            updateObj.backupList.clear();
        }
        
        input.stream().forEach((tuple) ->  tuple.setSchemaElementId(elementId));
        
        output = input;
        
        return true;
    }

    @Override
    public void outputReady(IOutputReadySender sender, OutputReadyArgs args) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
