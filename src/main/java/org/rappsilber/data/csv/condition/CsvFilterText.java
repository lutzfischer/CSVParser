/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.condition;

import java.io.IOException;
import org.rappsilber.data.csv.CSVRandomAccess;
import org.rappsilber.data.csv.CsvParser;

/**
 *
 * @author lfischer
 */
public class CsvFilterText implements CsvCondition{

    public CsvFilterText() {
    }
    
    String innerBracket(String text) {
       int bc = 1;
       for (int i = 0; i<=text.length(); i ++) {
           
           if (text.charAt(i) == ')') {
               if (--bc == 0) {
                   return text.substring(0,i);
               }
           } else if (text.charAt(i) == '(') {
               bc ++;
           }
       }
       return text;
    }
    
    
    public CsvFilterText(String conditions) {
        
    }
    
    
    public boolean next(CsvParser csv) throws IOException {
//        while (csv.next()) {
//            if (fits(csv))
//                return true;
//        }
        return false;
    }    

    @Override
    public boolean fits(int row, CSVRandomAccess csv) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean fits(CsvParser csv) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
