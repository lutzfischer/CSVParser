/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.sort;

/**
 * base class that is used for comparing rows during sorting
 */
public abstract class CSVSort {
    int field;

    /**
     *
     * @param field
     */
    public CSVSort(int field) {
        this.field = field;
    }

    public abstract int compare(Object[] row1, Object[] row2);
    
}
