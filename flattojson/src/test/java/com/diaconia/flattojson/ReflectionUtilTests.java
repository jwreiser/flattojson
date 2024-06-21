package com.diaconia.flattojson;

import com.diaconia.flattojson.model.VSAMRecord;
import com.diaconia.flattojson.util.ReflectionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionUtilTests {

    private ReflectionUtil reflectionUtil= new ReflectionUtil();

    @Test
    void hasProperty_whenPropertyExists_returnsTrue() {

        assertTrue(reflectionUtil.hasProperty(new VSAMRecord(), "taxPayerIdentificationNumber"));
    }

    @Test
    void hasProperty_whenPropertyDoesNotExist_returnsFalse() {
        assertFalse(reflectionUtil.hasProperty(new VSAMRecord(), "taxPayerIdentificationNumberGARBAGE"));
    }

    @Test
    void hasProperty_whenPropertyNameIsNull_returnsFalse() {
        assertFalse(reflectionUtil.hasProperty(new VSAMRecord(),null));;
    }
}
