package com.diaconia.flattojson.batch;

import com.diaconia.flattojson.model.VSAMRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.FlatFileItemReader;

public class ErrorNoticingFlatFileItemReader extends FlatFileItemReader<VSAMRecord> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorNoticingFlatFileItemReader.class);

    @Override
    protected VSAMRecord doRead() throws Exception{
        VSAMRecord record = null;
        try{
            record=super.doRead();
        }catch (Exception ex){
            LOGGER.error("Error reading record: "+ex.getMessage());
            throw ex;
        }
        return record;
    }
}
