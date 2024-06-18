package com.diaconia.flattojson.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VSAMRecord {
    private int taxPayerIdentificationNumber;
    private LocalDate mftTp;
    private int trans;
    private int reasonCode3; //I am using an int not a list, because the number of reason codes is fixed and Mongo should be slightly more efficient with fields than with arrays
    private int xrefTaxPayerIdentificationNumber;
}
