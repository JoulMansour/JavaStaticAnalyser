package ex5.main;

/**
 * Syntax exception class.
 */
public class SyntaxException extends SjavacException {
    /**
     * Constructor.
     * @param message the message to be printed.
     */
    public SyntaxException(String message) {
        super(message);
    }
}