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

    Environment ancestor(int distance) {
        Environment environment = this;

        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }

    // returns the value of the variable in that environment's map
    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    // walks a fixed number of environments and then stuffs the new value in that
    // map
    void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
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