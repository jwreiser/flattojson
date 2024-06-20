package com.diaconia.flattojson.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DataElement {
    private String name;
    private String type;
    private int position;
    private int length;
}
