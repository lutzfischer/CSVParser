/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.filter;

import java.io.IOException;
import org.rappsilber.data.csv.CSVRandomAccess;
import org.rappsilber.data.csv.CsvParser;
import org.rappsilber.data.csv.condition.CsvCondition;

/**
 * General interface for filtering CSV-files
 *
 * @author lfischer
 */
public interface CSVFilter extends CsvCondition {

    boolean next(CsvParser csv) throws IOException;
}
