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
public class CsvConditionEqualsField implements CsvCondition {
    int field1;
    String field2;

    /**
     * creates a new condition
     * @param field
     * @param value
     */
    public CsvConditionEqualsField(int field1, int field2) {
        this.field1 = this.field1;
        this.field2 = this.field2;
    }

    /**
     * returns whether the given row fits the condition
     * @param row
     * @return true if it does; false otherwise
     */
    @Override
    public boolean fits(int row, CSVRandomAccess csv) {
        return csv.getValue(field1,row).contentEquals(csv.getValue(field2,row));
    }
    
    /**
     * returns whether the current row fits the condition
     * @param row
     * @return true if it does; false otherwise
     */
    @Override
    public boolean fits(CsvParser csv) {
        return csv.getValue(field1).contentEquals(csv.getValue(field2));
    }
    
    
}
