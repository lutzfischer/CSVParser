/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Lutz Fischer <lfischer@staffmail.ed.ac.uk>
 */
public class CsvMultiParser extends CsvParser{
    File[] files;
    int currentParser = -1;
    boolean parseHeader = false;

    public CsvMultiParser(File[] files) {
        this.files = files;
    }

    public CsvMultiParser(File[] files, char delimiter) {
        super(delimiter);
        this.files = files;
    }

    public CsvMultiParser(File[] files, char delimiter, char quote) {
        super(delimiter, quote);
        this.files = files;
    }

    public void open(boolean hasHeader) throws IOException {
        parseHeader = hasHeader;
        if (files.length >0) {
            currentParser  = 0;
            super.openFile(files[currentParser], parseHeader);
        } else {
            throw new UnsupportedOperationException("No files given");
        }
    }

    @Override
    public boolean next() throws IOException {
        boolean ret = super.next();
        while (!ret && currentParser<files.length-1) {
            currentParser++;
            super.openFile(files[currentParser], parseHeader);
            ret = super.next();
        }
        return ret;
    }
    
    
    
    
}
