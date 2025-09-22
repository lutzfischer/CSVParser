# CSV Condition Parser

This document describes how to use the `CsvConditionParser` to parse condition definitions from strings and create `CsvCondition` objects for filtering CSV data.

## Overview

The `CsvConditionParser` allows you to define filtering conditions using a natural string syntax instead of manually creating condition objects. It supports complex expressions with logical operators, parentheses for grouping, and various comparison operations.

## Supported Syntax

### Column References
- Use square brackets to reference columns: `[column name]` or `[column index]`
- Examples: `[name]`, `[age]`, `[0]`, `[1]`

### String Operations
- `contains`: Check if one string contains another
- `equals` or `=`: Check for exact string equality
- `matches`: Check against a regular expression pattern

### Numeric Operations
- `=`: Equal to
- `!=`: Not equal to (note: not yet implemented in all condition classes)
- `<`: Less than
- `<=`: Less than or equal to
- `>`: Greater than
- `>=`: Greater than or equal to

### Logical Operations
- `AND`: Logical AND operation
- `OR`: Logical OR operation
- `NOT`: Logical NOT operation

### Grouping
- `()`: Use parentheses to group expressions and control precedence

### Literals
- String literals: `"value"` or `'value'`
- Numeric literals: `123`, `123.45`

## Expression Examples

### Simple Conditions
```java
"[status] = 'active'"                    // String equality
"[age] >= 30"                           // Numeric comparison
"[name] contains 'John'"                // String contains
```

### Field-to-Field Comparisons
```java
"[column A] contains [column B]"        // One field contains another
"[salary] >= [minimum_salary]"          // Numeric field comparison
"[first_name] = [last_name]"            // Field equality
```

### Logical Combinations
```java
"[status] = 'active' AND [age] >= 30"
"[department] = 'Engineering' OR [department] = 'Marketing'"
"NOT [status] = 'inactive'"
```

### Complex Expressions with Grouping
```java
"([age] < 30 AND [salary] > 50000) OR ([department] = 'Management' AND [status] = 'active')"
"([department] = 'Engineering' OR [department] = 'Marketing') AND [salary] >= 60000"
```

## Usage Example

```java
import org.rappsilber.data.csv.CsvParser;
import org.rappsilber.data.csv.condition.CsvCondition;
import org.rappsilber.data.csv.condition.CsvConditionParser;

// Create your CSV parser
CsvParser csvParser = new CsvParser();
csvParser.openFile(csvFile, true); // true = has header

// Create the condition parser
CsvConditionParser conditionParser = new CsvConditionParser(csvParser);

// Parse a condition string
String conditionString = "[status] = 'active' AND [age] >= 30";
CsvCondition condition = conditionParser.parse(conditionString);

// Use the condition while reading CSV data
while (csvParser.next()) {
    if (condition.fits(csvParser)) {
        // Process matching row
        System.out.println("Matching row: " + getCurrentRowData(csvParser));
    }
}
```

## Integration with Existing CSV Framework

The parser integrates seamlessly with your existing CSV condition framework:

- All parsed conditions implement the `CsvCondition` interface
- Supports both `fits(CsvParser csv)` and `fits(int row, CSVRandomAccess csv)` methods
- Works with existing condition classes like `CsvConditionAnd`, `CsvConditionOr`, etc.
- Automatically resolves column names using the CSV parser's header information

## Error Handling

The parser throws `CsvConditionParser.ParseException` for various parsing errors:

- Syntax errors in the expression
- Unknown column names
- Invalid operators
- Mismatched parentheses
- Invalid numeric values

Example error handling:
```java
try {
    CsvCondition condition = conditionParser.parse(conditionString);
    // Use condition...
} catch (CsvConditionParser.ParseException e) {
    System.err.println("Failed to parse condition: " + e.getMessage());
}
```

## Operator Precedence

The parser follows standard logical operator precedence:

1. Parentheses `()` (highest precedence)
2. NOT operations
3. Comparison operations (`=`, `<`, `>`, `contains`, etc.)
4. AND operations
5. OR operations (lowest precedence)

## Performance Considerations

- The parser creates condition objects that are optimized for repeated evaluation
- Column name resolution is cached in the CSV parser
- Complex expressions with many OR conditions may be slower than AND conditions
- Consider using parentheses to optimize evaluation order for complex expressions

## Extending the Parser

To add support for new operators or condition types:

1. Add the operator to the tokenization patterns in `CsvConditionParser.tokenize()`
2. Add the operator handling in `CsvConditionParser.createCondition()`
3. Implement the corresponding `CsvCondition` class if it doesn't exist

## Limitations

- String patterns for `matches` operator must be valid Java regular expressions
- Field-to-field operations require both fields to exist in the CSV
- Numeric comparisons will attempt to parse string values as doubles
- Case sensitivity depends on the underlying string operations

## Testing

See `CsvConditionUsageExample.java` for comprehensive usage examples and test cases.