/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.condition;

/**
 *
 * @author Lutz Fischer <lfischer@staffmail.ed.ac.uk>
 */
public abstract class CsvConditionList implements CsvCondition{
    protected CsvCondition[] conditions;

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

    @Override
    public String getField() {
        return null;
    }

}
