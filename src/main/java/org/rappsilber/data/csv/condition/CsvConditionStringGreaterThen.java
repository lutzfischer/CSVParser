/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.condition;

import org.rappsilber.data.csv.CSVRandomAccess;
import org.rappsilber.data.csv.CsvParser;

/**
 * CsvConditionStringEqual is used to find rows in a csv-file that have a specific value in the given column
 */
public class CsvConditionStringGreaterThen implements CsvCondition {
    int field;
    String value;
    String fieldName;

    /**
     * creates a new condition
     * @param field
     * @param value
     */
    public CsvConditionStringGreaterThen(int field, String value) {
        this(field, value, null);
    }

    public CsvConditionStringGreaterThen(int field, String value, CsvParser parser) {
        this.field = field;
        this.value = value;
        if (parser == null) {
            fieldName = "[" +field +"]";
        } else {
            fieldName = parser.getHeader(field);
        }
    }

    /**
     * returns whether the given row fits the condition
     * @param row
     * @return true if it does; false otherwise
     */
    @Override
    public boolean fits(int row, CSVRandomAccess csv) {
        return csv.getValue(field,row).compareTo(value)>0;
    }
    
    /**
     * returns whether the current row fits the condition
     * @param row
     * @return true if it does; false otherwise
     */
    @Override
    public boolean fits(CsvParser csv) {
        return csv.getValue(field).compareTo(value)>0;
    }
    
    
    public String toString() {
        return "([" + fieldName + "] > \"" + value + "\")";
    }        
}
