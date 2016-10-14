/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.condition;

import org.rappsilber.data.csv.CSVRandomAccess;
import org.rappsilber.data.csv.CsvParser;

/**
 *
 * @author lfischer
 */
public interface CsvCondition {

    /**
     * returns whether the given row fits the condition
     * @param row
     * @return true if it does; false otherwise
     */
    boolean fits(int row, CSVRandomAccess csv);

    /**
     * returns whether the current row fits the condition
     * @param row
     * @return true if it does; false otherwise
     */
    boolean fits(CsvParser csv);
    
}
