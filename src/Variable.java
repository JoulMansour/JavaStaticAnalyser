package ex5.main;

/**
 * Represents a variable in the s-Java language.
 * Holds the properties defined in the assignment: name, type,
 * modifier, and state.
 */
public class Variable {

    // Member variables.
    private final String name;
    private final String type;
    private final boolean isFinal;
    private boolean isInitialized;
    private boolean isGlobal;

    /**
     * Constructor
     * @param name The name of the variable (e.g., "counter")
     * @param type The type of the variable (e.g., "int")
     * @param isFinal True if the variable is constant
     * @param isInitialized True if assigned a value at declaration
     */
    public Variable(String name, String type, boolean isFinal, boolean isInitialized) {
        this.name = name;
        this.type = type;
        this.isFinal = isFinal;
        this.isInitialized = isInitialized;
        this.isGlobal = false;
    }

    /**
     * Method to get the name.
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Method to get the type.
     * @return the type.
     */
    public String getType() {
        return type;
    }

    /**
     * Method to know if final.
     * @return true if final otherwise false.
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * Method to know if initialized.
     * @return true if initialized otherwise false.
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Marks the variable as initialized.
     * Used when a variable is assigned a value after declaration.
     */
    public void setInitialized(boolean initialized) {
        this.isInitialized = initialized;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public boolean isGlobal() {
        return isGlobal;
    }
}