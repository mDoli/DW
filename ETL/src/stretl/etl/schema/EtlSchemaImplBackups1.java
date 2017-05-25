
package stretl.etl.schema;

import java.util.List;
import stretl.common.Tuple;
import stretl.etl.UpdateObj;
import stretl.etl.backup.CheckPoint;
import stretl.etl.extractor.ExtractorImpl;
import stretl.etl.inserter.InserterImpl;
import stretl.etl.transformer.Transformer;
import stretl.etl.transformer.TransformerImpl;


public class EtlSchemaImplBackups1 extends EtlSchema {

    public EtlSchemaImplBackups1(UpdateObj updateObj, int schemaId) {
        super(updateObj, schemaId);
    }

    @Override
    public void build() {
        
        addExtractor(new ExtractorImpl(source, updateObj, schemaId));
        
        addTransform(new TransformerImpl(schemaId));
        addTransform(new TransformerImpl(schemaId));
        addTransform(new TransformerImpl(schemaId));
        addTransform(new TransformerImpl(schemaId));
        
        addInserter(new InserterImpl(schemaId));
        
        for (int i = 0; i < allElements.size(); i++) {
            allElements.get(i).setElementId(i);
            allElements.get(i).setParentSchema(this);
        }
        
        getExtractor(0).addListener(getTransform(0));        
        getTransform(0).addListener(getTransform(1));        
        getTransform(1).addListener(getTransform(2));
        getTransform(2).addListener(getTransform(3));        
        getTransform(3).addListener(getInserter(0));
        
    }
    
}
