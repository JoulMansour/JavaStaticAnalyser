package ex5.main;

import java.util.regex.Pattern;

/**
 * A utility class containing all the Regular Expressions used to parse s-Java.
 * Defined as constants to optimize performance (compiling Patterns once).
 */
public class RegexLibrary {

    // Class constants.
    static final String PRIMITIVE_TYPE = "(int|double|String|boolean|char)";
    private static final String FINAL = "final";
    private static final String VOID = "void";
    /**
     * Regex for a valid variable name.
     */
    public static final String VAR_NAME = "([a-zA-Z][\\w]*|_[\\w]+)";
    /**
     * Regex for a valid method name.
     */
    public static final String METHOD_NAME = "[a-zA-Z][\\w]*";

    /**
     * Integers: optional +/-, digits.
     */
    public static final String INT_VAL = "[+-]?\\d+";

    /**
     * Doubles: various formats like .5, 5., 5.0.
     */
    public static final String DOUBLE_VAL = "[+-]?(\\d+\\.\\d*|\\.\\d+|\\d+)";

    /**
     * String: anything inside double quotes.
     */
    public static final String STRING_VAL = "\"[^\"]*\"";

    /**
     * Char: single character inside single quotes.
     */
    public static final String CHAR_VAL = "'[^']'";

    /**
     * Boolean: true/false (numbers are also valid booleans,
     * but handled by type checker).
     */
    public static final String BOOL_VAL = "(true|false|" + DOUBLE_VAL + ")";

    /**
     * Any valid literal value (for basic syntax checking).
     */
    public static final String ANY_VALUE =
            String.format("(%s|%s|%s|%s|%s|%s)",
                    STRING_VAL, CHAR_VAL, BOOL_VAL, DOUBLE_VAL,
                    INT_VAL, VAR_NAME);
    /**
     * Variable Declaration.
     */
    public static final Pattern VAR_DECLARATION = Pattern.compile(
            "^\\s*(?<final>" + FINAL + "\\s+)?(?<type>" +
                    PRIMITIVE_TYPE + ")\\s+.*"
    );
    /**
     * Variable Assignment.
     */
    public static final Pattern ASSIGNMENT = Pattern.compile(
            "^\\s*(?<name>" + VAR_NAME + ")" +
                    "\\s*=\\s*(?<value>.+?)\\s*;\\s*$"

    );
    /**
     * Method Declaration.
     */
    public static final Pattern METHOD_DECLARATION = Pattern.compile(
            "^\\s*" + VOID + "\\s+(?<name>" + METHOD_NAME + ")" +
                    "\\s*\\((?<params>.*)\\)\\s*\\{\\s*$"
    );
    /**
     * Method Declaration.
     */
    public static final Pattern METHOD_CALL = Pattern.compile(
            "^\\s*(?<name>" + METHOD_NAME + ")\\s*\\((?<args>.*)" +
                    "\\)\\s*;\\s*$"
    );
    /**
     * Return Statement.
     */
    public static final Pattern RETURN = Pattern.compile(
            "^\\s*return\\s*;\\s*$");
    /**
     * If / While Blocks.
     */
    public static final Pattern IF_WHILE = Pattern.compile(
            "^\\s*(if|while)\\s*\\((?<condition>.*)\\)\\s*\\{\\s*$"
    );
    /**
     * Closing Block.
     */
    public static final Pattern CLOSING_BLOCK = Pattern.compile(
            "^\\s*\\}\\s*$");
    /**
     * Comment.
     */
    public static final Pattern COMMENT = Pattern.compile("^\\s*//.*$");
    /**
     * Empty Line.
     */
    public static final Pattern EMPTY_LINE = Pattern.compile("^\\s*$");
}