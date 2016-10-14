/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.filter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rappsilber.data.csv.CSVRandomAccess;
import org.rappsilber.data.csv.CsvParser;

/**
 *
 * @author lfischer
 */
public abstract class CsvAbstractFilter implements CSVFilter{


    @Override
    public boolean next(CsvParser csv) throws IOException {
        while (csv.next()) {
            if (fits(csv))
                return true;
        }
        return false;
    }
    
}
