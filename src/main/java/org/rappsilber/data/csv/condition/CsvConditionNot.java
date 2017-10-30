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
public class CsvConditionNot implements CsvCondition{
    private CsvCondition inner;

    public CsvConditionNot() {
    }

    public CsvConditionNot(CsvCondition inner) {
        this.inner = inner;
    }
    
    
    
    @Override
    public boolean fits(int row, CSVRandomAccess csv) {
        return !inner.fits(row,csv);
    }

    @Override
    public boolean fits(CsvParser csv) {
        return !inner.fits(csv);
    }    

    /**
     * @return the inner
     */
    public CsvCondition getInner() {
        return inner;
    }

    /**
     * @param inner the inner to set
     */
    public void setInner(CsvCondition inner) {
        this.inner = inner;
    }
    
    public String toString() {
        return "(NOT "+ inner.toString() +" )";
    }      
        
}
