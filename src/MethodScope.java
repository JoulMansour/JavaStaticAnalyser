package ex5.main;

/**
 * Represents a local scope (inside a method, if loop, or while loop).
 */
public class MethodScope extends Scope {
    /**
     * Constructor.
     * @param parentScope the parent scope.
     */
    public MethodScope(Scope parentScope) {
        super(parentScope);
    }
}