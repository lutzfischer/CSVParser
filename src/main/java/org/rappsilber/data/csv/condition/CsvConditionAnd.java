/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.condition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.rappsilber.data.csv.CSVRandomAccess;
import org.rappsilber.data.csv.CsvParser;
import org.rappsilber.utils.RArrayUtils;

/**
 *
 * @author lfischer
 */
public class CsvConditionAnd extends CsvConditionList{

    public CsvConditionAnd() {
    }

    
    public CsvConditionAnd(CsvCondition[] conditions) {
        this.conditions = conditions;
    }
    
    
    
    @Override
    public boolean fits(int row, CSVRandomAccess csv) {
        for (CsvCondition c :  conditions)
            if (!c.fits(row, csv))
                return false;
        return true;
    }

    @Override
    public boolean fits(CsvParser csv) {
        for (CsvCondition c :  conditions)
            if (!c.fits(csv))
                return false;
        return true;
    }


    
    public String toString() {
        return "(" + RArrayUtils.toString(conditions, " AND ") + ")";
    }
    

        
 
        
    @Override
    public String getField() {
        return null;
    }

}
