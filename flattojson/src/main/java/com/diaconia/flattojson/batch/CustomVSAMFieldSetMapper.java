package com.diaconia.flattojson.batch;

import com.diaconia.flattojson.model.VSAMRecord;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class CustomVSAMFieldSetMapper extends BeanWrapperFieldSetMapper<com.diaconia.flattojson.model.VSAMRecord>
implements FieldSetMapper<com.diaconia.flattojson.model.VSAMRecord> {
    @Override
    public com.diaconia.flattojson.model.VSAMRecord mapFieldSet(FieldSet fieldSet) throws BindException {
        com.diaconia.flattojson.model.VSAMRecord vsamRecord = super.mapFieldSet(fieldSet);
        return vsamRecord;
    }
}
