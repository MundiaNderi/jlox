package com.craftinginterpreters.lox;

// wraps the return value with the accoutrements ava requires fr a runtime exception class
class Return extends RuntimeException {
    final Object value;

    Return(Object value) {
        super(null, null, false, false);
        this.value = value;
    }
}