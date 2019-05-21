/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.sort;

/**
 * implement a numeric compare of a given field for sorting the file
 */
public class CSVSortNumeric extends CSVSort {

    /**
     *
     * @param field
     */
    public CSVSortNumeric(int field) {
        super(field);
    }

    public int compare(Object[] row1, Object[] row2) {
        Double d1;
        Double d2;
        try {
            d1 = Double.parseDouble(row1[field].toString());
        } catch (Exception nfe) {
            d1 = Double.NEGATIVE_INFINITY;
        }
        try {
            d2 = Double.parseDouble(row2[field].toString());
        } catch (Exception nfe) {
            d2 = Double.NEGATIVE_INFINITY;
        }
        return d1.compareTo(d2);
    }
    
}
