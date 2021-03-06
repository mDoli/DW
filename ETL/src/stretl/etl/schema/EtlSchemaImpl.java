package stretl.etl.schema;

import stretl.etl.UpdateObj;
import stretl.etl.extractor.Extractor;
import stretl.etl.extractor.ExtractorImpl;
import stretl.etl.inserter.InserterImpl;
import stretl.etl.transformer.TransformerImpl;

/**
 * Implementacja 0 schematu ETL.
 * @author Artur Drzeniek
 */
public class EtlSchemaImpl extends EtlSchema {

    public EtlSchemaImpl(UpdateObj updateObj, int schemaId) {
        super(updateObj, schemaId);
    }
    
    @Override
    public void build(){
        addExtractor(new ExtractorImpl(source, updateObj, schemaId));
        addTransform(new TransformerImpl(schemaId));
        addTransform(new TransformerImpl(schemaId));
        addInserter(new InserterImpl(schemaId));
        
        getExtractor(0).addListener(getTransform(0));
        getTransform(0).addListener(getTransform(1));
        getTransform(1).addListener(getInserter(0));
    }

}
