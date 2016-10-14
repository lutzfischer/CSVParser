/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.condition;

import java.io.IOException;
import org.rappsilber.data.csv.CsvParser;

/**
 *
 * @author lfischer
 */
public class CsvFilter extends CsvConditionAnd{

    public CsvFilter() {
    }

    public CsvFilter(CsvCondition[] conditions) {
        super(conditions);
    }
    
    
    public boolean next(CsvParser csv) throws IOException {
        while (csv.next()) {
            if (fits(csv))
                return true;
        }
        return false;
    }    
    
}
