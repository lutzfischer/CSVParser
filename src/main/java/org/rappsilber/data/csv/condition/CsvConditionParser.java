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
package org.rappsilber.data.csv.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.rappsilber.data.csv.CsvParser;

/**
 * Parser for CSV condition expressions that converts string representations
 * into CsvCondition objects.
 * 
 * Supported syntax:
 * - Column references: [column name] or [column index]
 * - String operations: contains, equals, matches
 * - Numeric operations: =, !=, <, <=, >, >=
 * - Logical operations: AND, OR, NOT
 * - Parentheses for grouping: (expression)
 * - String literals: "value" or 'value'
 * - Numeric literals: 123, 123.45
 * 
 * Example expressions:
 * - "[column A] contains [column B]"
 * - "[column C] >= 10 AND [status] = 'active'"
 * - "([price] > 100 OR [discount] > 0.5) AND [category] contains 'electronics'"
 * 
 * @author lfischer
 */
public class CsvConditionParser {
    
    private CsvParser csvParser;
    private String expression;
    private List<Token> tokens;
    private int currentTokenIndex;
    
    /**
     * Token types for the parser
     */
    public enum TokenType {
        COLUMN,           // [column name]
        STRING_LITERAL,   // "value" or 'value'
        NUMERIC_LITERAL,  // 123 or 123.45
        MISSING,          // missing
        OPERATOR,         // =, !=, <, <=, >, >=, contains, equals, matches
        LOGICAL_AND,      // AND
        LOGICAL_OR,       // OR
        LOGICAL_NOT,      // NOT
        LEFT_PAREN,       // (
        RIGHT_PAREN,      // )
        EOF               // End of input
    }
    
    /**
     * Represents a token in the expression
     */
    public static class Token {
        TokenType type;
        String value;
        int position;
        
        public Token(TokenType type, String value, int position) {
            this.type = type;
            this.value = value;
            this.position = position;
        }
        
        @Override
        public String toString() {
            return type + "(" + value + ")";
        }
    }
    
    /**
     * Exception thrown when parsing fails
     */
    public static class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }
        
        public ParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Creates a new parser
     * @param csvParser The CSV parser to use for column name resolution
     */
    public CsvConditionParser(CsvParser csvParser) {
        this.csvParser = csvParser;
    }
    
    /**
     * Parses a condition expression string into a CsvCondition
     * @param expression The expression to parse
     * @return The parsed condition
     * @throws ParseException If the expression cannot be parsed
     */
    public CsvCondition parse(String expression) throws ParseException {
        if (expression == null || expression.trim().isEmpty()) {
            throw new ParseException("Expression cannot be null or empty");
        }
        
        this.expression = expression.trim();
        this.tokens = tokenize(this.expression);
        this.currentTokenIndex = 0;
        
        CsvCondition result = parseOrExpression();
        
        if (getCurrentToken().type != TokenType.EOF) {
            throw new ParseException("Unexpected token at position " + getCurrentToken().position + ": " + getCurrentToken().value);
        }
        
        return result;
    }
    
    /**
     * Tokenizes the input expression
     */
    private List<Token> tokenize(String expression) throws ParseException {
        List<Token> tokens = new ArrayList<>();
        
        // Regex patterns for different token types
        Pattern columnPattern = Pattern.compile("\\[([^\\]]+)\\]");
        Pattern stringPattern = Pattern.compile("(['\"])([^'\"]*?)\\1");
        Pattern regexPattern = Pattern.compile("/([^/]*)/");  // Regex pattern like /pattern/
        Pattern numericPattern = Pattern.compile("\\b\\d+(?:\\.\\d+)?\\b");
        Pattern operatorPattern = Pattern.compile("\\b(?:contains|equals|matches)\\b|~|[!=<>]=?");
        Pattern logicalPattern = Pattern.compile("\\b(?:AND|OR|NOT)\\b", Pattern.CASE_INSENSITIVE);
        Pattern parenPattern = Pattern.compile("[()]");
        
        int pos = 0;
        while (pos < expression.length()) {
            // Skip whitespace
            if (Character.isWhitespace(expression.charAt(pos))) {
                pos++;
                continue;
            }
            
            boolean matched = false;
            
            // Try to match column reference
            Matcher columnMatcher = columnPattern.matcher(expression.substring(pos));
            if (columnMatcher.lookingAt()) {
                tokens.add(new Token(TokenType.COLUMN, columnMatcher.group(1), pos));
                pos += columnMatcher.end();
                matched = true;
                continue;
            }
            
            // missing?
            String remaining = expression.substring(pos).trim().toLowerCase();
            if (remaining.startsWith("missing")) {
                tokens.add(new Token(TokenType.MISSING, "missing", pos));
                pos += "missing".length();
                matched = true;
                continue;
            } else if (remaining.startsWith("null")) {
                tokens.add(new Token(TokenType.MISSING, "null", pos));
                pos += "null".length();
                matched = true;
                continue;
            } else if (remaining.startsWith("none")) {
                tokens.add(new Token(TokenType.MISSING, "none", pos));
                pos += "none".length();
                matched = true;
                continue;
            }
            
            // Try to match string literal
            Matcher stringMatcher = stringPattern.matcher(expression.substring(pos));
            if (stringMatcher.lookingAt()) {
                tokens.add(new Token(TokenType.STRING_LITERAL, stringMatcher.group(2), pos));
                pos += stringMatcher.end();
                matched = true;
                continue;
            }
            
            // Try to match regex pattern like /pattern/
            Matcher regexMatcher = regexPattern.matcher(expression.substring(pos));
            if (regexMatcher.lookingAt()) {
                tokens.add(new Token(TokenType.STRING_LITERAL, regexMatcher.group(1), pos));
                pos += regexMatcher.end();
                matched = true;
                continue;
            }
            
            // Try to match numeric literal
            Matcher numericMatcher = numericPattern.matcher(expression.substring(pos));
            if (numericMatcher.lookingAt()) {
                tokens.add(new Token(TokenType.NUMERIC_LITERAL, numericMatcher.group(), pos));
                pos += numericMatcher.end();
                matched = true;
                continue;
            }
            
            // Try to match logical operators
            Matcher logicalMatcher = logicalPattern.matcher(expression.substring(pos));
            if (logicalMatcher.lookingAt()) {
                String op = logicalMatcher.group().toUpperCase();
                TokenType type = op.toUpperCase().equals("AND") ? TokenType.LOGICAL_AND : 
                               op.toUpperCase().equals("OR") ? TokenType.LOGICAL_OR : TokenType.LOGICAL_NOT;
                tokens.add(new Token(type, op, pos));
                pos += logicalMatcher.end();
                matched = true;
                continue;
            }
            
            // Try to match comparison operators
            Matcher operatorMatcher = operatorPattern.matcher(expression.substring(pos));
            if (operatorMatcher.lookingAt()) {
                tokens.add(new Token(TokenType.OPERATOR, operatorMatcher.group(), pos));
                pos += operatorMatcher.end();
                matched = true;
                continue;
            }
            
            // Try to match parentheses
            Matcher parenMatcher = parenPattern.matcher(expression.substring(pos));
            if (parenMatcher.lookingAt()) {
                String paren = parenMatcher.group();
                TokenType type = paren.equals("(") ? TokenType.LEFT_PAREN : TokenType.RIGHT_PAREN;
                tokens.add(new Token(type, paren, pos));
                pos += parenMatcher.end();
                matched = true;
                continue;
            }
            
            if (!matched) {
                throw new ParseException("Unexpected character at position " + pos + ": '" + expression.charAt(pos) + "'");
            }
        }
        
        tokens.add(new Token(TokenType.EOF, "", expression.length()));
        return tokens;
    }
    
    /**
     * Gets the current token
     */
    private Token getCurrentToken() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex);
        }
        return new Token(TokenType.EOF, "", expression.length());
    }
    
    /**
     * Advances to the next token
     */
    private void nextToken() {
        if (currentTokenIndex < tokens.size() - 1) {
            currentTokenIndex++;
        }
    }
    
    /**
     * Expects a specific token type and advances
     */
    private Token expect(TokenType expectedType) throws ParseException {
        Token token = getCurrentToken();
        if (token.type != expectedType) {
            throw new ParseException("Expected " + expectedType + " but found " + token.type + " at position " + token.position);
        }
        nextToken();
        return token;
    }
    
    /**
     * Parses OR expressions (lowest precedence)
     */
    private CsvCondition parseOrExpression() throws ParseException {
        CsvCondition left = parseAndExpression();
        
        while (getCurrentToken().type == TokenType.LOGICAL_OR) {
            nextToken(); // consume OR
            CsvCondition right = parseAndExpression();
            left = new CsvConditionOr(new CsvCondition[]{left, right});
        }
        
        return left;
    }
    
    /**
     * Parses AND expressions (higher precedence than OR)
     */
    private CsvCondition parseAndExpression() throws ParseException {
        CsvCondition left = parseNotExpression();
        
        while (getCurrentToken().type == TokenType.LOGICAL_AND) {
            nextToken(); // consume AND
            CsvCondition right = parseNotExpression();
            left = new CsvConditionAnd(new CsvCondition[]{left, right});
        }
        
        return left;
    }
    
    /**
     * Parses NOT expressions (higher precedence than AND)
     */
    private CsvCondition parseNotExpression() throws ParseException {
        if (getCurrentToken().type == TokenType.LOGICAL_NOT) {
            nextToken(); // consume NOT
            CsvCondition condition = parseNotExpression();
            return new CsvConditionNot(condition);
        }
        
        return parseComparisonExpression();
    }
    
    /**
     * Parses comparison expressions (highest precedence)
     */
    private CsvCondition parseComparisonExpression() throws ParseException {
        if (getCurrentToken().type == TokenType.LEFT_PAREN) {
            nextToken(); // consume (
            CsvCondition condition = parseOrExpression();
            expect(TokenType.RIGHT_PAREN);
            return condition;
        }
        
        return parseComparison();
    }
    
    /**
     * Parses a single comparison operation
     */
    private CsvCondition parseComparison() throws ParseException {
        Token leftOperand = getCurrentToken();
        
        if (leftOperand.type != TokenType.COLUMN) {
            throw new ParseException("Expected column reference at position " + leftOperand.position);
        }
        nextToken();
        
        Token operator = getCurrentToken();
        if (operator.type != TokenType.OPERATOR) {
            throw new ParseException("Expected operator at position " + operator.position);
        }
        nextToken();
        
        Token rightOperand = getCurrentToken();
        nextToken();
        
        return createCondition(leftOperand, operator, rightOperand);
    }
    
    /**
     * Creates a condition based on the parsed tokens
     */
    private CsvCondition createCondition(Token leftOperand, Token operator, Token rightOperand) throws ParseException {
        int leftField = resolveColumnIndex(leftOperand.value);
        
        switch (operator.value.toLowerCase()) {
            case "contains":
                if (rightOperand.type == TokenType.COLUMN) {
                    // Field contains field - need to create a custom condition
                    int rightField = resolveColumnIndex(rightOperand.value);
                    return new CsvConditionContainsField(leftField, rightField, csvParser);
                } else if (rightOperand.type == TokenType.STRING_LITERAL) {
                    return new CsvConditionStringContains(leftField, rightOperand.value, csvParser);
                } else {
                    throw new ParseException("'contains' operator requires string value or column reference");
                }
                
            case "equals":
            case "=":
                if (rightOperand.type == TokenType.COLUMN) {
                    int rightField = resolveColumnIndex(rightOperand.value);
                    return new CsvConditionEqualsField(leftField, rightField, csvParser);
                } else if (rightOperand.type == TokenType.STRING_LITERAL) {
                    return new CsvConditionStringEqual(leftField, rightOperand.value, csvParser);
                } else if (rightOperand.type == TokenType.NUMERIC_LITERAL) {
                    try {
                        double value = Double.parseDouble(rightOperand.value);
                        return new CsvConditionDoubleEqual(leftField, value, csvParser);
                    } catch (NumberFormatException e) {
                        throw new ParseException("Invalid numeric value: " + rightOperand.value);
                    }
                } else if (rightOperand.type == TokenType.MISSING) {
                    return new CsvConditionMissing(leftField, csvParser);
                } else {
                    throw new ParseException("Unsupported operand type for equals: " + rightOperand.type);
                }
                
            case ">=":
                if (rightOperand.type == TokenType.NUMERIC_LITERAL) {
                    try {
                        double value = Double.parseDouble(rightOperand.value);
                        return new CsvConditionDoubleGreaterEqual(leftField, value, csvParser);
                    } catch (NumberFormatException e) {
                        throw new ParseException("Invalid numeric value: " + rightOperand.value);
                    }
                } else if (rightOperand.type == TokenType.COLUMN) {
                    int rightField = resolveColumnIndex(rightOperand.value);
                    return new CsvConditionGreaterOrEqualsField(leftField, rightField, csvParser);
                } else if (rightOperand.type == TokenType.STRING_LITERAL) {
                    return new CsvConditionStringGreaterEqual(leftField, rightOperand.value, csvParser);
                } else {
                    throw new ParseException("'>=' operator requires numeric value, string value, or column reference");
                }
                
            case ">":
                if (rightOperand.type == TokenType.NUMERIC_LITERAL) {
                    try {
                        double value = Double.parseDouble(rightOperand.value);
                        return new CsvConditionDoubleGreaterThen(leftField, value, csvParser);
                    } catch (NumberFormatException e) {
                        throw new ParseException("Invalid numeric value: " + rightOperand.value);
                    }
                } else if (rightOperand.type == TokenType.COLUMN) {
                    int rightField = resolveColumnIndex(rightOperand.value);
                    return new CsvConditionGreaterField(leftField, rightField, csvParser);
                } else if (rightOperand.type == TokenType.STRING_LITERAL) {
                    return new CsvConditionStringGreaterThen(leftField, rightOperand.value, csvParser);
                } else {
                    throw new ParseException("'>' operator requires numeric value, string value, or column reference");
                }
                
            case "<=":
                if (rightOperand.type == TokenType.NUMERIC_LITERAL) {
                    try {
                        double value = Double.parseDouble(rightOperand.value);
                        return new CsvConditionDoubleLessEqual(leftField, value, csvParser);
                    } catch (NumberFormatException e) {
                        throw new ParseException("Invalid numeric value: " + rightOperand.value);
                    }
                } else if (rightOperand.type == TokenType.COLUMN) {
                    int rightField = resolveColumnIndex(rightOperand.value);
                    return new CsvConditionLessOrEqualsField(leftField, rightField, csvParser);
                } else if (rightOperand.type == TokenType.STRING_LITERAL) {
                    return new CsvConditionStringLessEqual(leftField, rightOperand.value, csvParser);
                } else {
                    throw new ParseException("'<=' operator requires numeric value, string value, or column reference");
                }
                
            case "<":
                if (rightOperand.type == TokenType.NUMERIC_LITERAL) {
                    try {
                        double value = Double.parseDouble(rightOperand.value);
                        return new CsvConditionDoubleLessThen(leftField, value, csvParser);
                    } catch (NumberFormatException e) {
                        throw new ParseException("Invalid numeric value: " + rightOperand.value);
                    }
                } else if (rightOperand.type == TokenType.COLUMN) {
                    int rightField = resolveColumnIndex(rightOperand.value);
                    return new CsvConditionLessField(leftField, rightField, csvParser);
                } else if (rightOperand.type == TokenType.STRING_LITERAL) {
                    return new CsvConditionStringLessThen(leftField, rightOperand.value, csvParser);
                } else {
                    throw new ParseException("'<' operator requires numeric value, string value, or column reference");
                }
                
            case "~":
                if (rightOperand.type == TokenType.STRING_LITERAL) {
                    return new CsvConditionStringMatches(leftField, rightOperand.value, csvParser);
                } else {
                    throw new ParseException("'matches' operator requires string pattern");
                }
                
            default:
                throw new ParseException("Unsupported operator: " + operator.value);
        }
    }
    
    /**
     * Resolves a column name or index to a column index
     */
    private int resolveColumnIndex(String columnRef) throws ParseException {
        // Try to parse as numeric index first
        try {
            return Integer.parseInt(columnRef);
        } catch (NumberFormatException e) {
            // Not a number, try to resolve as column name
            if (csvParser != null) {
                Integer index = csvParser.getColumn(columnRef);
                if (index != null) {
                    return index;
                }
            }
            throw new ParseException("Unknown column: " + columnRef);
        }
    }
}