/*
 * Copyright 2015 Lutz Fischer <lfischer at staffmail.ed.ac.uk>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rappsilber.data.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.rappsilber.data.csv.CsvParser;
import org.rappsilber.data.csv.CSVRandomAccess;
import org.rappsilber.utils.StringUtils;

/**
 * Defines some standard alternatives for column names
 * Predominantly useful for proteomics related files.
 * Also provides {@link #levenshteinMatchHeadersALternatives} for matching found headers against the registered alternatives to see what column name fits what registered name the best
 * @author lfischer
 */
public abstract class ColumnAlternatives {
    /**
     * List of know aliases
     */
    public static String[][] COLUMN_ALIASES = new String[][]{
        {"matchid", "spectrummatchid", "match id", "spectrum match id", "psmid"},
        {"isdecoy", "is decoy", "reverse", "decoy"},
        {"isdecoy1", "is decoy 1", "is decoy1","reverse1", "decoy1", "protein 1 decoy"},
        {"isdecoy2", "is decoy 2", "is decoy2", "reverse2", "decoy2", "protein 2 decoy"},
        {"score", "match score", "match score", "pep score"},
        {"peptide1 score", "pep1 score", "score peptide1", "score pep1", "pep 1 score"},
        {"peptide2 score", "pep2 score", "score peptide2", "score pep2", "pep 2 score"},
        {"run", "run name", "raw file", "filename/id"},
        {"scan", "scan number", "ms/ms scan number", "spectrum number"},
        {"pep1 position", "peptide position1", "start1", "peptide position 1", "PepPos1"},
        {"pep2 position", "peptide position2", "start2", "peptide position 2", "PepPos2"},
        {"pep1 link pos", "link1", "peptide1 link pos", "peptide link1", "peptide link 1", "from site","LinkPos1"},
        {"pep2 link pos", "link2", "peptide2 link pos", "peptide link2", "peptide link 2" , "to site","LinkPos2"},
        {"lengthpeptide1", "peptide1 length", "peptide1 length", "peptide length 1", "length1"},
        {"lengthpeptide2", "peptide2 length", "peptide2 length", "peptide length 2", "length2"},
        {"peptide1", "peptide 1", "peptide", "modified sequence"},
        {"peptide2" , "peptide 2"},
        {"precursermz", "precursor mz", "experimental mass", "exp mass"},
        {"precursor charge", "precoursorcharge", "charge"},
        {"calculated mass", "calc mass", "theoretical mass"},
        {"description1", "fasta1"},
        {"description2", "fasta2"},
        {"protein1", "display protein1", "accession1"},
        {"protein2", "display protein2", "accession2"},};    

    
    private ColumnAlternatives() {}

        /**
     * set known alternatives header-names for the given CSV-file
     * @param csv 
     */
    public static void setupAlternatives(CsvParser csv) {
        setupAlternatives(csv, COLUMN_ALIASES);
    }
    
    /**
     * set known alternatives header-names for the given CSV-file
     * @param csv 
     * @param aliases a set of column names that should be treated as the same
     */
    public static void setupAlternatives(CsvParser csv, String[][]  aliases) {
        // setup all alaises
        for (int a = 0; a < aliases.length; a++) {
            String[] ta = aliases[a];
            if (ta[0].contains(" ")) {
                csv.setAlternative(ta[0], ta[0].replaceAll("\\s", "_"));
                csv.setAlternative(ta[0], ta[0].replaceAll("\\s", ""));
            }
            for (int i = 1; i < ta.length; i++) {
                if (ta[i].contains(" ")) {
                    csv.setAlternative(ta[0], ta[i].replaceAll("\\s", "_"));
                    csv.setAlternative(ta[0], ta[i].replaceAll("\\s", ""));
                }
                csv.setAlternative(ta[0], ta[i]);
            }
        }
    }
    

    /**
     * match column-names with alternatives with a bit of tolerance.
     * @param csv 
     */
    public static void levenshteinMatchHeadersALternatives(CsvParser csv) {
        levenshteinMatchHeadersALternatives(csv, new HashMap<String, String[]>());
    }
    
    /**
     * match column-names with alternatives with a bit of tolerance.
     * @param csv 
     * @param nonAccpetedMapping maps expected column titles to realworld column titels that should not be mapped - even if they appear to be the nearest string
     */
    public static void levenshteinMatchHeadersALternatives(CsvParser csv, HashMap<String,String[]> nonAccpetedMapping) {
        ArrayList<HashSet<String>> allAlternatives = csv.getHeaderAlternatives();
        ArrayList<String> unmatchedHeaders = new ArrayList<String>();
        ArrayList<HashSet<String>> unmatchedAlternatives = new ArrayList<HashSet<String>>();
        HashSet<Integer> matchedColumns = new HashSet<Integer>();
        
        // get all unmatched alternatives
        for (HashSet<String> s : allAlternatives) {
            // first alternative
            String fa = s.iterator().next();
            // do we have a matched column ?
            Integer col = csv.getColumn(fa);
            
            if (col != null) { // yes 
                matchedColumns.add(col);
            } else { // no so we remember this alternative set
                unmatchedAlternatives.add(s);
            }
        }
        // get all headers without an alternative
        for (String s : csv.getHeader()) {
            Integer col = csv.getColumn(s);
            if (! matchedColumns.contains(col)) {
                unmatchedHeaders.add(s);
            }
        }
        
        double[][] dists = new double[unmatchedAlternatives.size()][unmatchedHeaders.size()];
        double globalmindist =Integer.MAX_VALUE;
        int globalminAlt = -1;
        int globalminheader = -1;
        int unmatchedHeaderCount = unmatchedHeaders.size();
        int unmatchedAlternativesCount = unmatchedAlternatives.size();
        HashSet<Character> space = new HashSet<Character>(4);
        space.add(' ');
        space.add('_');
        space.add('\t');
        
        // calculate the minmal distance for each alternative set and each header 
        for (int alt =0; alt< unmatchedAlternatives.size(); alt++) {
            HashSet<String> colAlternatives = unmatchedAlternatives.get(alt);
            HashSet<String> excludedMappings = new HashSet();
            for (String excluded : nonAccpetedMapping.keySet()) {
                if (colAlternatives.contains(excluded)) {
                    for (String e:nonAccpetedMapping.get(excluded))
                        excludedMappings.add(e);
                }
            }
            for (int h = 0; h < unmatchedHeaders.size(); h++) {
                String header = unmatchedHeaders.get(h);
                if (excludedMappings.contains(header))
                    continue;
                double mindist = Double.MAX_VALUE;
                
                alternatives: for ( String a : unmatchedAlternatives.get(alt)) {
//                    double dist = StringUtils.editCost(a.toLowerCase(), header.toLowerCase(), 1, 3) / (double)a.length();
                    double dist = StringUtils.editCost(a.toLowerCase(), header.toLowerCase(), 1.0, 0.5, 3.0,space) / (double)a.length();
                    for (String e : excludedMappings) {
                        double edist = StringUtils.editCost(e.toLowerCase(), header.toLowerCase(), 1.0, 0.5, 3.0,space) / (double)a.length();
                        // if this header matches an excluded mapping better then
                        // the expected - don't considere it
                        if (edist < dist)
                            continue alternatives;
                    }
                    
                    if (dist < mindist && dist < 0.7) {
                        mindist = dist;
                    }
                }
                dists[alt][h] = mindist;
                if (mindist < globalmindist) {
                    globalmindist = mindist;
                    globalminAlt = alt;
                    globalminheader = h;
                }
            }
        }
        

        while (unmatchedHeaderCount > 0 && unmatchedAlternativesCount>0) {
            
            // find the current minimal distance
            globalmindist = Double.MAX_VALUE;
            for (int alt =0; alt< unmatchedAlternatives.size(); alt++) {
                for (int h = 0; h < unmatchedHeaders.size(); h++) {
                    double dist = dists[alt][h];
                    if (dist < globalmindist) {
                        globalmindist = dist;
                        globalminAlt = alt;
                        globalminheader = h;
                    }
                }
            }
            
            if (globalmindist == Double.MAX_VALUE)
                break;
            
            String mfa = unmatchedAlternatives.get(globalminAlt).iterator().next();
            String mh = unmatchedHeaders.get(globalminheader);
            
            csv.setAlternative(mfa, mh);
            
            for (int h = 0; h< unmatchedHeaders.size(); h++) {
                dists[globalminAlt][h] = Double.MAX_VALUE;
            }
            unmatchedAlternativesCount --;
            
            for (int a = 0; a< unmatchedAlternatives.size(); a++) {
                dists[a][globalminheader] = Double.MAX_VALUE;
            }
            unmatchedHeaderCount --;
        }
    }
    
}
