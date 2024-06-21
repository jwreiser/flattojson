package com.diaconia.flattojson.batch;

import com.diaconia.flattojson.model.DataElement;
import com.diaconia.flattojson.model.VSAMRecord;
import com.diaconia.flattojson.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;

import java.util.List;
import java.util.stream.Collectors;

public class FixedLengthTokenizerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(FixedLengthTokenizerFactory.class);
    private static final ReflectionUtil reflectionUtil = new ReflectionUtil();

    public static void validateDataElements(List<DataElement> dataElements) {
        int position = 0;
        for (DataElement dataElement : dataElements) {
            if (dataElement.getPosition() != position) {
                LOGGER.error("Data elements must be in order");
                throw new IllegalArgumentException("Data elements must be in order");
            }
            position += dataElement.getLength();
        }
    }

    public static void validateVSAMRecord(List<DataElement> dataElements) {
        int position = 0;
        String error;
        for (DataElement dataElement : dataElements) {
            if (!reflectionUtil.hasProperty(new VSAMRecord(), dataElement.getName())) {
                error="VSAM object must have property "+dataElement.getName();
                LOGGER.error(error);
                throw new IllegalArgumentException(error);
            }
            position += dataElement.getLength();
        }
    }
    public static List<Range> buildRanges(List<DataElement> dataElements){
        validateDataElements(dataElements);
        validateVSAMRecord(dataElements);
        return dataElements.stream().map(de ->new Range(de.getPosition()+1,de.getPosition()+de.getLength())).collect(Collectors.toList());
    }
    public static FixedLengthTokenizer createFixedLengthTokenizer(List<DataElement> dataElements) {
        validateDataElements(dataElements);
        List<Range>ranges=buildRanges(dataElements);
        FixedLengthTokenizer fixedLengthTokenizer = new FixedLengthTokenizer();
        fixedLengthTokenizer.setNames(dataElements.stream().map(DataElement::getName).toArray(String[]::new));
        fixedLengthTokenizer.setColumns(ranges.toArray(new Range[0]));
        return fixedLengthTokenizer;
    }
}
