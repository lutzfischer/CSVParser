/*
 * Copyright 2025 Lutz Fischer <lfischer at staffmail.ed.ac.uk>.
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

import java.io.BufferedReader;
import java.io.StringReader;
import org.rappsilber.data.csv.CsvParser;
import org.rappsilber.data.csv.condition.CsvCondition;
import org.rappsilber.data.csv.condition.CsvConditionParser;

/**
 * Practical usage example of CsvConditionParser
 * 
 * @author lfischer
 */
public class CsvConditionUsageExample {
    
    public static void main(String[] args) {
        try {
            // Create a sample CSV for demonstration
            String csvData = "name,age,salary,department,status\n" +
                           "John Doe,25,50000,Engineering,active\n" +
                           "Jane Smith,30,75000,Marketing,active\n" +
                           "Bob Johnson,35,60000,Engineering,inactive\n" +
                           "Alice Brown,28,55000,Sales,active\n" +
                           "Charlie Wilson,45,90000,Management,active";
            
            // Example usage: Filter CSV rows based on a condition string
            String conditionString = "[status] = 'active' AND [age] >= 30";
            filterCsvWithCondition(csvData, conditionString);
            
            System.out.println("\n================================================\n");
            
            // Example usage: Complex condition
            String complexCondition = "([department] = 'Engineering' OR [department] = 'Marketing') AND [Money] >= 60000";
            filterCsvWithCondition(csvData, complexCondition);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demonstrates filtering CSV data with a condition string
     */
    public static void filterCsvWithCondition(String csvData, String conditionString) {
        try {
            System.out.println("Filtering CSV with condition: " + conditionString);
            System.out.println("CSV Data:");
            System.out.println(csvData);
            System.out.println("\nResults:");
            
            // Create CSV parser
            CsvParser csvParser = new CsvParser();
            csvParser.openFile(new BufferedReader(new StringReader(csvData)), true, null);
            csvParser.setAlternative("Money", "salary");
            
            // Parse the condition
            CsvConditionParser conditionParser = new CsvConditionParser(csvParser);
            CsvCondition condition = conditionParser.parse(conditionString);
            
            System.out.println("Parsed condition: " + condition);
            System.out.println("\nMatching rows:");
            
            // Read and filter rows
            int rowNumber = 0;
            int matchCount = 0;
            
            while (csvParser.next()) {
                rowNumber++;
                if (condition.fits(csvParser)) {
                    matchCount++;
                    System.out.println("Row " + rowNumber + ": " + getCurrentRowAsString(csvParser));
                }
            }
            
            System.out.println("\nSummary: " + matchCount + " out of " + rowNumber + " rows matched the condition.");
            
        } catch (Exception e) {
            System.err.println("Error filtering CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gets a string representation of the current CSV row
     */
    private static String getCurrentRowAsString(CsvParser csvParser) {
        StringBuilder sb = new StringBuilder();
        String[] headers = csvParser.getHeader();
        
        for (int i = 0; i < headers.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(headers[i]).append("='").append(csvParser.getValue(i)).append("'");
        }
        
        return sb.toString();
    }
}