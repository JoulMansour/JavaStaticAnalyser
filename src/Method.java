package ex5.main;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a method definition in s-Java.
 * Stores the method signature: name and parameters.
 */
public class Method {

    // Member variables.
    private final String name;
    private final List<Variable> parameters;

    /**
     * Constructor.
     * @param name the name.
     * @param parameters the parameters.
     */
    public Method(String name, List<Variable> parameters) {
        this.name = name;
        // If parameters is null (no params), store an empty list to avoid NullPointerExceptions
        this.parameters = parameters != null ? parameters : new ArrayList<>();
    }

    /**
     * Method to get the name.
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return A list of variables representing the method parameters.
     * Note: Parameters are treated as local variables initialized
     * upon method entry.
     */
    public List<Variable> getParameters() {
        return parameters;
    }

    /**
     * Helper to get a parameter by index (useful for validating method calls).
     * @param index the index of the parameter.
     * @return null.
     */
    public Variable getParameter(int index) {
        if (index >= 0 && index < parameters.size()) {
            return parameters.get(index);
        }
        return null;
    }
}