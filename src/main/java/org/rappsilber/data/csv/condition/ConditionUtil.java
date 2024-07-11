/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.condition;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Lutz Fischer <lfischer@staffmail.ed.ac.uk>
 */
public class ConditionUtil {
    
    
    public CsvCondition fromString(String scon) throws ParseException {
        scon=scon.trim();
        if (scon.startsWith("(")) {
            if (scon.endsWith(")")) {
                scon=scon.substring(1,scon.length()-1);
            }
        }
        String sconL = scon.toLowerCase();
        /*
        regex magic: https://stackoverflow.com/questions/47162098/is-it-possible-to-match-nested-brackets-with-a-regex-without-using-recursion-or 
        */
        Pattern element = Pattern.compile("(?=\\()(?:(?=.*?\\((?!.*?\\1)(.*\\)(?!.*\\2).*))(?=.*?\\)(?!.*?\\2)(.*)).)+?.*?(?=\\1)[^(]*(?=\\2$)");
        Matcher m = element.matcher(scon);
        ArrayList<CsvCondition> list = new ArrayList<>();
        String lo = null;
        String last;
        while (m.find()) {
            String rest= sconL.substring(m.end());
            rest.trim();
            if (rest.length()>0) {
                String op = rest.substring(0,rest.indexOf(" "));
                if (lo == null || !lo.contentEquals(op))
                    throw new ParseException("different operants used in the same level of clause: " + lo + " vs " + op,m.end());
                else
                    lo = op;
                list.add(fromString(m.group()));
            } else 
                last = m.group();
        }
        
        
        // consume element wise
        return null;
    }    
}
