package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0; // points to the first character in the lexeme being scanned
    private int current = 0; // points at the character currently being considered
    private int line = 1; // tracks what source line current is on

    // constructor
    Scanner(String source) {
        this.source = source;
    }

}

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme
            start = current;
            scanTokens();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private boolean match(char expected) {
        if (isAtEnd)
            return false;

        if (source.charAt(current) != expected)
            return false;

        current++;
        return true;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;

            default:
                Lox.error(line, "Unexppected charcater.");
                break;
        }
    }

    // Helper methods
    // consumes the next character in the source dile and returns it
    // for input
    private char advance() {
        return source.charAt(current++);
    }

    // for output
    // grabs the text of the current lexeme and creates a new token for it
    private void addToken(TokenType type) {
        addToken(null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

private boolean isAtEnd (){
    return current >= source.length();
}