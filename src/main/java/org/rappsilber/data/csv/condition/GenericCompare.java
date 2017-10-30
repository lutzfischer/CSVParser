/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.condition;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Lutz Fischer <lfischer@staffmail.ed.ac.uk>
 */
public abstract class GenericCompare {
    public static Pattern isNumber = Pattern.compile("[0-9]*\\.?[0-9]+");


    static {
        DecimalFormat format=(DecimalFormat) DecimalFormat.getInstance();
        DecimalFormatSymbols symbols=format.getDecimalFormatSymbols();
        char sep   = symbols.getDecimalSeparator();        
        char group = symbols.getGroupingSeparator();     
        String exp   = symbols.getExponentSeparator();
        isNumber = Pattern.compile("([0-9"+group+"]*\\"+sep+"?[0-9"+group+"]+|)");
    }
            
    
    public static int numericPriorityCompare(Object o1, Object o2) {
        if (o1 instanceof Number && o2 instanceof Number) {
            return Double.compare(((Number)o1).doubleValue(),((Number)o2).doubleValue());
        } else {
            String v1 = o1.toString();
            Matcher m1 = isNumber.matcher(v1);
            if (m1.matches()) {
                String v2 = o2.toString();
                Matcher m2 = isNumber.matcher(v2);
                if (m2.matches()) {
                    return Double.compare(Double.parseDouble(v1),Double.parseDouble(v2));
                }
            }
            return v1.compareTo(o2.toString());
        }
    }    
}
