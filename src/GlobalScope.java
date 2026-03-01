package ex5.main;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the top level global scope.
 * Stores global variables and the method registry.
 */
public class GlobalScope extends Scope {

    // Member variables.
    private final List<Method> methods;

    /**
     * Constructor.
     */
    public GlobalScope() {
        super(null); // Global scope has no parent
        this.methods = new ArrayList<>();
    }

    /**
     * A function to add a method.
     * @param method the method to be added.
     */
    public void addMethod(Method method) {
        methods.add(method);
    }

    /**
     * valid method search logic.
     * Note: Methods can be called from anywhere, so we store them globally.
     */
    public Method getMethod(String name) {
        for (Method m : methods) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }
}