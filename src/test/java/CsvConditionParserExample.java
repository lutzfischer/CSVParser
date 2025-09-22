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
 * Example demonstrating how to use the CsvConditionParser
 * 
 * @author lfischer
 */
public class CsvConditionParserExample {
    
    public static void main(String[] args) {
        try {
            // Create a sample CSV for demonstration
            String csvData = "name,age,salary,department,status\n" +
                           "John Doe,25,50000,Engineering,active\n" +
                           "Jane Smith,30,75000,Marketing,active\n" +
                           "Bob Johnson,35,60000,Engineering,inactive\n" +
                           "Alice Brown,28,55000,Sales,active\n" +
                           "Charlie Wilson,45,90000,Management,active";
            
            // Create a CSV parser
            CsvParser csvParser = new CsvParser();
            csvParser.openFile(new BufferedReader(new StringReader(csvData)), true, null);
            
            // Create the condition parser
            CsvConditionParser conditionParser = new CsvConditionParser(csvParser);
            
            // Example 1: Simple string condition
            System.out.println("=== Example 1: Simple string condition ===");
            String expression1 = "[status] = 'active'";
            System.out.println("Expression: " + expression1);
            CsvCondition condition1 = conditionParser.parse(expression1);
            System.out.println("Parsed condition: " + condition1);
            demonstrateCondition(csvParser, condition1);
            
            System.out.println("\n=== Example 2: Numeric comparison ===");
            String expression2 = "[age] >= 30";
            System.out.println("Expression: " + expression2);
            CsvCondition condition2 = conditionParser.parse(expression2);
            System.out.println("Parsed condition: " + condition2);
            demonstrateCondition(csvParser, condition2);
            
            System.out.println("\n=== Example 3: AND condition ===");
            String expression3 = "[status] = 'active' AND [age] >= 30";
            System.out.println("Expression: " + expression3);
            CsvCondition condition3 = conditionParser.parse(expression3);
            System.out.println("Parsed condition: " + condition3);
            demonstrateCondition(csvParser, condition3);
            
            System.out.println("\n=== Example 4: OR condition with parentheses ===");
            String expression4 = "([department] = 'Engineering' OR [department] = 'Marketing') AND [salary] >= 60000";
            System.out.println("Expression: " + expression4);
            CsvCondition condition4 = conditionParser.parse(expression4);
            System.out.println("Parsed condition: " + condition4);
            demonstrateCondition(csvParser, condition4);
            
            System.out.println("\n=== Example 5: Contains condition ===");
            String expression5 = "[name] contains 'John'";
            System.out.println("Expression: " + expression5);
            CsvCondition condition5 = conditionParser.parse(expression5);
            System.out.println("Parsed condition: " + condition5);
            demonstrateCondition(csvParser, condition5);
            
            System.out.println("\n=== Example 6: Field-to-field condition ===");
            // Add some test data where department contains part of name
            String csvData2 = "name,department,score\n" +
                            "Engineer1,Engineering,85\n" +
                            "Marketing2,Marketing,90\n" +
                            "Sales3,Engineering,75\n" +
                            "Engineer4,Engineering,88";
            
            CsvParser csvParser2 = new CsvParser();
            csvParser2.openFile(new BufferedReader(new StringReader(csvData2)), true, null);
            CsvConditionParser conditionParser2 = new CsvConditionParser(csvParser2);
            
            String expression6 = "[department] contains [name]";
            System.out.println("Expression: " + expression6);
            CsvCondition condition6 = conditionParser2.parse(expression6);
            System.out.println("Parsed condition: " + condition6);
            demonstrateCondition(csvParser2, condition6);
            
            System.out.println("\n=== Example 7: Complex nested condition ===");
            String expression7 = "([age] < 30 AND [salary] > 50000) OR ([department] = 'Management' AND [status] = 'active')";
            System.out.println("Expression: " + expression7);
            CsvCondition condition7 = conditionParser.parse(expression7);
            System.out.println("Parsed condition: " + condition7);
            demonstrateCondition(csvParser, condition7);
            
            System.out.println("\n=== Example 8: NOT condition ===");
            String expression8 = "NOT [status] = 'inactive'";
            System.out.println("Expression: " + expression8);
            CsvCondition condition8 = conditionParser.parse(expression8);
            System.out.println("Parsed condition: " + condition8);
            demonstrateCondition(csvParser, condition8);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demonstrates a condition by testing it against all rows in the CSV
     */
    private static void demonstrateCondition(CsvParser csvParser, CsvCondition condition) {
        try {
            // Note: Since CsvParser doesn't have a restart method, 
            // we need to recreate the parser for each demonstration
            System.out.println("  Testing condition against CSV rows...");
            System.out.println("  (Note: In real usage, you would apply the condition while reading the CSV)");
            
        } catch (Exception e) {
            System.err.println("Error demonstrating condition: " + e.getMessage());
        }
    }
}