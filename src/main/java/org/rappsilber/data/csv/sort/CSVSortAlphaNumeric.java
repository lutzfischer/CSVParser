/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.sort;

/**
 * implement a string compare of a given field for sorting the file
 */
public class CSVSortAlphaNumeric extends CSVSort {

    /**
     * constructor
     * @param field by what column to sort
     */
    public CSVSortAlphaNumeric(int field) {
        super(field);
    }

    @Override
    public int compare(Object[] row1, Object[] row2) {
        return row1[field].toString().compareTo(row2[field].toString());
    }
    
}
