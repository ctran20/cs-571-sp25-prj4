package simplf;

import java.util.List;

class SimplfFunction implements SimplfCallable {

    private final Stmt.Function declaration;
    private Environment closure;

    SimplfFunction(Stmt.Function declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    public void setClosure(Environment environment) {
        this.closure = environment;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        // Create a new environment for the call, enclosed in the captured closure
        Environment functionEnv = new Environment(closure);

        // Bind parameters in-place
        for (int i = 0; i < declaration.params.size(); i++) {
            functionEnv.define(declaration.params.get(i),
                               declaration.params.get(i).lexeme,
                               args.get(i));
        }

        // Temporarily swap interpreter environment
        Environment previous = interpreter.environment;
        interpreter.environment = functionEnv;

        Object result = null;
        try {
            for (Stmt stmt : declaration.body) {
                result = interpreter.execute(stmt);
            }
        } finally {
            interpreter.environment = previous;
        }

        return result;
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }
}
