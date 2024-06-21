package com.diaconia.flattojson.batch;

import com.diaconia.flattojson.model.VSAMRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonObjectMarshaller;
import org.springframework.classify.Classifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;

import java.util.ArrayList;
import java.util.List;

public class MultipleFileItemWriterClassifier implements Classifier<VSAMRecord, ItemWriter<? super VSAMRecord>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FixedLengthTokenizerFactory.class);
    JsonObjectMarshaller<VSAMRecord> marshaller = new JacksonJsonObjectMarshaller<>();
    private static final int CLASSIFIER_SIZE = 10_000;
    List<JsonFileItemWriter> writers = new ArrayList<>(CLASSIFIER_SIZE);
    public MultipleFileItemWriterClassifier() {
        for(int i=0;i<CLASSIFIER_SIZE;i++){
            String fileName = "output/chunk"+i+".json";
            FileSystemResource file = new FileSystemResource(fileName);
            JsonFileItemWriter writer= new JsonFileItemWriter(file,marshaller);
            writer.setAppendAllowed(true);
            writer.open(new ExecutionContext());
            writer.setName(fileName);
            writers.add(i,writer);
        }
    }

    @Override
    public ItemWriter<? super VSAMRecord> classify(VSAMRecord vsamRecord) {
        int chunk=vsamRecord.getTaxPayerIdentificationNumber()%CLASSIFIER_SIZE;
        if(chunk>writers.size()){
            LOGGER.error("Chunk out of bounds");
            return null;
        }else{
            return writers.get(chunk);
        }
    }
    /*
     The larger the number of writers the slower things are.
     Some benchmarks/number of writers:
     200k: stopped at 13 minutes [this is far to slow for 200k records]
     20k: 2m
     1/3/5/10K:5s
     at roughly the 10k threshold we start to have issues presumably w/ memory

    public ItemWriter<? super VSAMRecord> classify(VSAMRecord vsamRecord) {
        String fileName = "output/" + vsamRecord.getTaxPayerIdentificationNumber() + ".json";
        FileSystemResource file = new FileSystemResource(fileName);
        JsonFileItemWriter writer = new JsonFileItemWriter(file, marshaller);
        writer.setAppendAllowed(true);
        writer.open(new ExecutionContext());
        writer.setName(fileName);
        return writer;
    }
     */
}
