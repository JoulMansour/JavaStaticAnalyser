package ex5.main;

/**
 * Base exception for all verification errors.
 */
public abstract class SjavacException extends Exception {
    /**
     * Constructor.
     * @param message the message to be printed.
     */
    public SjavacException(String message) {
        super(message);
    }
}