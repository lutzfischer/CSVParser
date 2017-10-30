/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.condition;

import java.util.regex.Pattern;
import org.rappsilber.data.csv.CSVRandomAccess;
import org.rappsilber.data.csv.CsvParser;

/**
 * CsvConditionStringEqual is used to find rows in a csv-file that have a specific value in the given column
 */
public class CsvConditionStringMatches implements CsvCondition {
    int field;
    Pattern value;
    String fieldName;

    /**
     * creates a new condition
     * @param field
     * @param value
     */
    public CsvConditionStringMatches(int field, String value) {
        this(field, value, null);
    }

    public CsvConditionStringMatches(int field, String value, CsvParser parser) {
        this.field = field;
        this.value = Pattern.compile(value);
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
        return value.matcher(csv.getValue(field,row)).matches();
    }
    
    /**
     * returns whether the current row fits the condition
     * @param row
     * @return true if it does; false otherwise
     */
    @Override
    public boolean fits(CsvParser csv) {
        return value.matcher(csv.getValue(field)).matches();
    }

    public String toString() {
        return "([" + fieldName + "] ~ /" + value + "/)";
    }       
    
}
