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
public class CsvConditionDoubleGreaterThen implements CsvCondition {
    int field;
    double value;
    String fieldName;

    /**
     * creates a new condition
     * @param field
     * @param value
     */
    public CsvConditionDoubleGreaterThen(int field, double value) {
        this(field, value, null);
    }

    public CsvConditionDoubleGreaterThen(int field, double value, CsvParser parser) {
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
        return csv.getDouble(field,row) > value;
    }
    
    /**
     * returns whether the current row fits the condition
     * @param row
     * @return true if it does; false otherwise
     */
    @Override
    public boolean fits(CsvParser csv) {
        return csv.getDouble(field) > value;
    }
    
    public String toString() {
        return "([" + fieldName + "] > " + value + ")";
    }    
        
}
