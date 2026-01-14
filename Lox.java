package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Lox {
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

}

    // static - belongs to the class itself, not an instance like C++ or JS class
    // methods
    // stattic methods can only directly call other static methods - since main is
    // static
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code
        if (hadError)
            System.exit(65);
    }

    // throws IOException - declares that this method might throw and exception.
    // Unlike C++, Java has checked exceptions that mus be declared
    private static void runPrompt() throws IOException {
        InputSreameReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(inpput);

        for (;;) {
            System.out.print("> ");
            // Reads a line of input from the user on the command line and returns the
            // result
            String line = reader.readLine();
            if (line == null)
                break;
            run(line);
            hadError = false; // reset error flag for REPL
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        // For now, just print the tokens
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    // report - helper method for error
    private static void report(int line, String where, String message) {
        System.err.println("[line" + line  + "] Error" + where + ": " + message);
        hadError = true;
    }