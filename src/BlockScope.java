package ex5.main;

/**
 * Represents the scope inside an if statement or while loop.
 */
public class BlockScope extends Scope {
    /**
     * Constructor.
     * @param parentScope the parent scope.
     */
    public BlockScope(Scope parentScope) {
        super(parentScope);
    }
}