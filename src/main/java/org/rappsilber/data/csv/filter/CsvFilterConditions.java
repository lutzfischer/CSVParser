/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.filter;

import org.rappsilber.data.csv.CSVRandomAccess;
import org.rappsilber.data.csv.CsvParser;
import org.rappsilber.data.csv.condition.CsvCondition;

/**
 *
 * @author lfischer
 */
public class CsvFilterConditions extends CsvAbstractFilter{
    CsvCondition[] conditions;

    public CsvFilterConditions(CsvCondition[] conditions) {
        this.conditions = conditions;
    }

    
    @Override
    public boolean fits(CsvParser csv) {
        for (CsvCondition c : conditions) {
            if (!c.fits(csv))
                return false;
        }
        return true;
    }

    @Override
    public boolean fits(int row, CSVRandomAccess csv) {
        for (CsvCondition c : conditions) {
            if (!c.fits(row,csv))
                return false;
        }
        return true;
    }
    
}
