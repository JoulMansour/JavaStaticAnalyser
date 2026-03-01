package ex5.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Responsible for the main verification logic.
 * Orchestrates the Two-Pass algorithm.
 */
public class Verifier {

    // Member variables.
    private final String filePath;
    private final GlobalScope globalScope;

    /**
     * Constructor.
     * @param filePath the file.
     */
    public Verifier(String filePath) {
        this.filePath = filePath;
        this.globalScope = new GlobalScope();
    }

    /**
     * The main entry point for verification.
     * Runs Pass 1 (Globals/Methods) then Pass 2 (Inner Logic).
     */
    public void verify() throws IOException, SjavacException {
        List<String> lines = readFile(filePath);
        runFirstPass(lines);
        runSecondPass(lines);
    }

    /**
     * Reads the file and removes empty lines/comments immediately
     * to make processing easier.
     */
    private List<String> readFile(String path) throws IOException {
        List<String> cleanLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!RegexLibrary.EMPTY_LINE.matcher(line).matches() &&
                        !RegexLibrary.COMMENT.matcher(line).matches()) {
                    cleanLines.add(line);
                }
            }
        }
        return cleanLines;
    }

    /**
     * PASS 1: Iterate through the file.
     * 1. If it's a variable declaration -> Add to GlobalScope.
     * 2. If it's a method declaration -> Add to GlobalScope.
     * 3. If it's inside a method -> Skip until the method ends.
     */
    private void runFirstPass(List<String> lines) throws SjavacException {
        boolean insideMethod = false;
        int braceCount = 0;

        for (String line : lines) {
            Matcher methodMatcher = RegexLibrary.
                    METHOD_DECLARATION.matcher(line);
            Matcher varMatcher = RegexLibrary.VAR_DECLARATION.matcher(line);

            if (insideMethod) {
                if (line.contains("{")) braceCount++;
                if (line.contains("}")) braceCount--;
                if (braceCount == 0) {
                    insideMethod = false;
                }
                continue;
            }

            if (varMatcher.matches()) {
                parseGlobalVariable(line);
            }
            else if (methodMatcher.matches()) {
                parseMethodDeclaration(methodMatcher);
                insideMethod = true;
                braceCount = 1;
            }
            else if (RegexLibrary.ASSIGNMENT.matcher(line).matches()) {
                validateAssignmentLine(line, globalScope);
            }
            else {
                throw new SyntaxException("Illegal line in global scope: "
                        + line);
            }
        }
    }

    private void parseGlobalVariable(String line) throws SjavacException {
        parseVariableLine(line, globalScope, true);
    }

    private void parseVariableLine(String line, Scope scope, boolean isGlobalContext)
            throws SjavacException {
        Matcher matcher = RegexLibrary.VAR_DECLARATION.matcher(line);
        if (!matcher.matches()) {
            throw new SyntaxException("Invalid variable declaration syntax: "
                    + line);
        }

        boolean isFinal = matcher.group("final") != null;
        String type = matcher.group("type");

        String varsPart = line.substring(matcher.end("type")).trim();
        if (varsPart.endsWith(";")) {
            varsPart = varsPart.substring(0, varsPart.length() - 1);
        }

        String[] parts = varsPart.split(
                ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        for (String part : parts) {
            parseSingleVariable(part.trim(), type, isFinal, scope,  isGlobalContext);
        }
    }

    private void parseSingleVariable(String part, String type,
                                     boolean isFinal, Scope scope, boolean isGlobalContext)
            throws SjavacException {
        String name;
        String value = null;
        boolean isInitialized = false;

        int equalsIndex = part.indexOf('=');
        if (equalsIndex != -1) {
            name = part.substring(0, equalsIndex).trim();
            value = part.substring(equalsIndex + 1).trim();
            isInitialized = true;
        } else {
            name = part.trim();
        }

        if (!name.matches(RegexLibrary.VAR_NAME)) {
            throw new SyntaxException("Invalid variable name: " + name);
        }

        if (isFinal && !isInitialized) {
            throw new SyntaxException("Final variable '" + name +
                    "' must be initialized.");
        }

        if (scope.isVariableDefinedInScope(name)) {
            throw new ScopeException("Variable '" + name +
                    "' already defined in this scope.");
        }

        if (isInitialized) {
            validateValueType(value, type, scope);
        }

        Variable var = new Variable(name, type, isFinal, isInitialized);
        var.setGlobal(isGlobalContext);
        scope.addVariable(var);

        if (isInitialized && isGlobalContext) {
            scope.markGlobalAsInitialized(var);
        }
    }

    /**
     * Checks if a value string matches the expected type.
     * @param value The value string (e.g. "5", "true", "varName")
     * @param type The expected type (e.g. "int")
     * @param scope The current scope (to look up variables used as values)
     */
    private void validateValueType(String value, String type, Scope scope) throws SjavacException {
        if (type.equals("int")) {
            if (value.matches(RegexLibrary.INT_VAL)) return;
        } else if (type.equals("double")) {
            if (value.matches(RegexLibrary.DOUBLE_VAL) || value.matches(RegexLibrary.INT_VAL)) return;
        } else if (type.equals("String")) {
            if (value.matches(RegexLibrary.STRING_VAL)) return;
        } else if (type.equals("boolean")) {
            if (value.matches(RegexLibrary.BOOL_VAL) || value.matches(RegexLibrary.DOUBLE_VAL)
                    || value.matches(RegexLibrary.INT_VAL)) return;
        } else if (type.equals("char")) {
            if (value.matches(RegexLibrary.CHAR_VAL)) return;
        }

        Variable referencedVar = scope.getVariable(value);

        if (referencedVar == null) {
            throw new ScopeException("Variable '" + value + "' is not defined.");
        }

        boolean isInit = referencedVar.isInitialized();

        if (referencedVar.isGlobal()) {
            if (!isInit && !scope.isGlobalInitialized(referencedVar)) {
                throw new ScopeException("Global Variable '" + value + "' is not initialized.");
            }
        } else {
            if (!isInit) {
                throw new ScopeException("Local Variable '" + value + "' is not initialized.");
            }
        }

        String refType = referencedVar.getType();
        if (type.equals(refType)) return;
        if (type.equals("double") && refType.equals("int")) return;
        if (type.equals("boolean") && (refType.equals("int") || refType.equals("double"))) return;

        throw new TypeException("Type mismatch: cannot assign " + refType + " to " + type);
    }

    private void parseMethodDeclaration(Matcher matcher)
            throws SjavacException {
        String methodName = matcher.group("name");
        String paramsString = matcher.group("params");

        if (!methodName.matches(RegexLibrary.METHOD_NAME)) {
            throw new SyntaxException("Invalid method name: " + methodName);
        }

        if (globalScope.getMethod(methodName) != null) {
            throw new SyntaxException("Duplicate method definition: "
                    + methodName);
        }

        List<Variable> params = new ArrayList<>();
        if (!paramsString.trim().isEmpty()) {
            String[] paramParts = paramsString.split(",");
            for (String paramPart : paramParts) {
                params.add(parseParameter(paramPart.trim()));
            }
        }

        globalScope.addMethod(new Method(methodName, params));
    }

    private Variable parseParameter(String paramPart) throws SjavacException {

        Pattern paramPattern = Pattern.compile(
                "\\s*(?<final>final\\s+)?(?<type>" +
                        RegexLibrary.PRIMITIVE_TYPE+ ")\\s+(?<name>" +
                        RegexLibrary.VAR_NAME + ")\\s*");

        Matcher m = paramPattern.matcher(paramPart);
        if (!m.matches()) {
            throw new SyntaxException("Invalid parameter syntax: " + paramPart);
        }

        boolean isFinal = m.group("final") != null;
        String type = m.group("type");
        String name = m.group("name");

        return new Variable(name, type, isFinal, true);
    }

    /**
     * Validates an assignment line like "a = 5;" or "x = y;"
     * @param line The line to validate.
     * @param scope The current scope where the assignment happens.
     */
    private void validateAssignmentLine(String line, Scope scope)
            throws SjavacException {
        Matcher matcher = RegexLibrary.ASSIGNMENT.matcher(line);
        if (!matcher.matches()) {
            throw new SyntaxException("Invalid assignment syntax: " + line);
        }

        String varName = matcher.group("name");
        String valueExp = matcher.group("value");

        Variable var = scope.getVariable(varName);
        if (var == null) {
            throw new ScopeException("Cannot assign to undefined variable: "
                    + varName);
        }

        if (var.isFinal()) {
            throw new SyntaxException("Cannot re-assign final variable: "
                    + varName);
        }

        validateValueType(valueExp, var.getType(), scope);

        if (var.isGlobal()) {
            scope.markGlobalAsInitialized(var);
        } else {
            var.setInitialized(true);
        }
    }

    /**
     * PASS 2: Deep verification.
     * Iterates through the file again, but this time enters
     * methods and checks logic.
     */
    private void runSecondPass(List<String> lines) throws SjavacException {
        Scope currentScope = globalScope;

        for (String line : lines) {

            Matcher methodMatcher = RegexLibrary.METHOD_DECLARATION.
                    matcher(line);
            if (methodMatcher.matches()) {
                if (currentScope != globalScope) {
                    throw new SyntaxException("Method defined inside" +
                            " another method/block: " + line);
                }

                MethodScope methodScope = new MethodScope(globalScope);

                String methodName = methodMatcher.group("name");
                Method method = globalScope.getMethod(methodName);
                for (Variable param : method.getParameters()) {
                    methodScope.addVariable(param);
                }

                currentScope = methodScope;
                continue;
            }

            if (currentScope == globalScope) {
                continue;
            }

            if (RegexLibrary.CLOSING_BLOCK.matcher(line).matches()) {
                currentScope = currentScope.getParentScope();
                continue;
            }

            if (RegexLibrary.VAR_DECLARATION.matcher(line).matches()) {
                parseVariableLine(line, currentScope, false);
                continue;
            }

            if (RegexLibrary.ASSIGNMENT.matcher(line).matches()) {
                validateAssignmentLine(line, currentScope);
                continue;
            }

            Matcher callMatcher = RegexLibrary.METHOD_CALL.matcher(line);
            if (callMatcher.matches()) {
                validateMethodCall(callMatcher, currentScope);
                continue;
            }

            Matcher ifWhileMatcher = RegexLibrary.IF_WHILE.matcher(line);
            if (ifWhileMatcher.matches()) {
                validateIfWhileCondition(ifWhileMatcher.group(
                        "condition"), currentScope);

                BlockScope blockScope = new BlockScope(currentScope);
                currentScope = blockScope;
                continue;
            }

            if (RegexLibrary.RETURN.matcher(line).matches()) {
                continue;
            }

            if (RegexLibrary.COMMENT.matcher(line).matches() ||
                    RegexLibrary.EMPTY_LINE.matcher(line).matches()) {
                continue;
            }

            throw new SyntaxException("Unknown syntax or illegal line: "
                    + line);
        }

        if (currentScope != globalScope) {
            throw new SyntaxException("File ended with unclosed " +
                    "blocks/methods.");
        }
    }

    private void validateMethodCall(Matcher matcher, Scope scope)
            throws SjavacException {
        String methodName = matcher.group("name");
        String argsString = matcher.group("args");

        Method method = globalScope.getMethod(methodName);
        if (method == null) {
            throw new ScopeException("Method not found: " + methodName);
        }

        String[] args = argsString.trim().isEmpty() ? new String[0] :
                argsString.split(",");
        List<Variable> params = method.getParameters();

        if (args.length != params.size()) {
            throw new TypeException("Method " + methodName + " expects " +
                    params.size() + " arguments, got " + args.length);
        }

        for (int i = 0; i < args.length; i++) {
            String argValue = args[i].trim();
            Variable param = params.get(i);
            validateValueType(argValue, param.getType(), scope);
        }
    }

    private void validateIfWhileCondition(String condition, Scope scope)
            throws SjavacException {
        String[] parts = condition.split("(\\|\\|)|(&&)");
        for (String part : parts) {
            String val = part.trim();
            validateValueType(val, "boolean", scope);
        }
    }
}