package com.diaconia.flattojson.batch;

import com.diaconia.flattojson.model.VSAMRecord;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class CustomVSAMFieldSetMapper<VSAMRecord> extends BeanWrapperFieldSetMapper<VSAMRecord> implements FieldSetMapper<VSAMRecord> {
    @Override
    public VSAMRecord mapFieldSet(FieldSet fieldSet) throws BindException {
        VSAMRecord vsamRecord = super.mapFieldSet(fieldSet);
        //TODO Custom logic here
        return vsamRecord;
    }
}
