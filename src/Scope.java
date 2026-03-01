package ex5.main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract base class representing a scope in the s-Java code.
 * Implements the logic for storing variables and searching the scope chain.
 */
public abstract class Scope {

    // Member variables.
    private final Scope parentScope;
    private final List<Variable> variables;

    protected final Set<Variable> initializedGlobals = new HashSet<>();

    /**
     * Constructor.
     * @param parentScope The outer scope (null for GlobalScope).
     */
    public Scope(Scope parentScope) {
        this.parentScope = parentScope;
        this.variables = new ArrayList<>();
    }

    /**
     * Method to return the parent scope.
     * @return the parent scope.
     */
    public Scope getParentScope() {
        return parentScope;
    }

    /**
     * Adds a variable to this specific scope.
     * @param variable The variable to add.
     */
    public void addVariable(Variable variable) {
        variables.add(variable);
    }

    /**
     * Searches for a variable by name.
     * Starts in the current scope; if not found, bubbles up
     * to the parent scope.
     * @param name The name of the variable to find.
     * @return The Variable object if found, or null if not found.
     */
    public Variable getVariable(String name) {

        for (Variable var : variables) {
            if (var.getName().equals(name)) {
                return var;
            }
        }

        if (parentScope != null) {
            return parentScope.getVariable(name);
        }

        return null;
    }

    /**
     * Checks if a variable name is already defined *in this specific scope*.
     * Used to prevent declaring the same variable twice in the same block.
     */
    public boolean isVariableDefinedInScope(String name) {
        for (Variable var : variables) {
            if (var.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Marks a global variable as initialized in the current scope chain.
     * Bubbles the status up to the method level to ensure visibility across the entire execution flow.
     * * @param var The global variable to mark as initialized.
     */
    public void markGlobalAsInitialized(Variable var) {
        if (this.getParentScope() != null && !(this.getParentScope() instanceof GlobalScope)) {
            this.getParentScope().markGlobalAsInitialized(var);
        } else {
            initializedGlobals.add(var);
        }
    }

    /**
     * Recursively checks if a global variable is initialized in the current or any parent scope.
     * * @param var The global variable to check.
     * @return true if the variable is initialized in the scope chain; false otherwise.
     */
    public boolean isGlobalInitialized(Variable var) {
        if (initializedGlobals.contains(var)) return true;
        if (parentScope != null) return parentScope.isGlobalInitialized(var);
        return false;
    }
}