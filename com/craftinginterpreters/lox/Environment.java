package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    // get method to retrieve variable values
    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null)
            return enclosing.get(name);

        throw new RuntimeError(name, "Undefined vaiable '" + name.lexeme + "'.");
    }

    // assign method to update variable values. Not allowed to create a new variable
    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }
        throw new RuntimeError(name, "Undefined variable'" + name.lexeme + "'.");
    }

    void define(String name, Object value) {
        values.put(name, value);
    }

    // constructor for global environment
    Environment() {
        enclosing = null;
    }

    // constructr for nested / local enviroments
    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }
}