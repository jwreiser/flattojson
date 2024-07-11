package com.diaconia.flattojson.batch;

import com.diaconia.flattojson.model.VSAMRecord;
import org.springframework.batch.item.ItemProcessor;

public class NoisyProcessor implements ItemProcessor<VSAMRecord, VSAMRecord> {
    @Override
    public VSAMRecord process(VSAMRecord item) throws Exception {
        System.err.println("Processing item: " + item);
        return item;
    }
}
