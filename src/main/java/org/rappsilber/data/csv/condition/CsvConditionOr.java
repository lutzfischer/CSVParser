/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.condition;

import org.rappsilber.data.csv.CSVRandomAccess;
import org.rappsilber.data.csv.CsvParser;
import org.rappsilber.utils.RArrayUtils;

/**
 *
 * @author lfischer
 */
public class CsvConditionOr implements CsvCondition{
    private CsvCondition[] conditions;

    public CsvConditionOr() {
    }

    
    public CsvConditionOr(CsvCondition[] conditions) {
        this.conditions = conditions;
    }
    
    

    @Override
    public boolean fits(int row, CSVRandomAccess csv) {
        for (CsvCondition c :  conditions)
            if (c.fits(row, csv))
                return true;
        return false;
    }

    @Override
    public boolean fits(CsvParser csv) {
        for (CsvCondition c :  conditions)
            if (c.fits(csv))
                return true;
        return false;
    }
    
    /**
     * @return the conditions
     */
    public CsvCondition[] getConditions() {
        return conditions;
    }

    /**
     * @param conditions the conditions to set
     */
    public void setConditions(CsvCondition[] conditions) {
        this.conditions = conditions;
    }
    
    /**
     * @param conditions the conditions to set
     */
    public void addCondition(CsvCondition condition) {
        if (conditions == null) {
            conditions = new CsvCondition[] {condition};
        } else {
            CsvCondition[] dummy = new CsvCondition[conditions.length+1];
            System.arraycopy(conditions, 0, dummy, 0, conditions.length);
            dummy[conditions.length] = condition;
            conditions = dummy;
        }
    }   
    
    public String toString() {
        return "(" + RArrayUtils.toString(conditions, " OR ") + ")";
    }
            
}
