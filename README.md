This repository contains my personal implementation and notes while working through Crafting Interpreters by Robert Nystrom.

The goal is to deepen my understanding of parsing, AST construction, interpretation, and language design. My notes reflect design decisions, questions, and insights encountered during implementation. Challenges.md contains my solutions and reflections on the end-of-chapter exercises.

# jlox interpreter

## A Tree Walk Interpreter

- Based on crafting interpreters

A Tree walk interpreter executes code by directly traversing an AST (abstract syntax tree).

Each node represents a language construct, like an expression, statetement or an operator.

Scanner / Lexer - Breaks source code as a string into tokens => Parser - Builds the AST from tokens => Interpreter - Walks the AST and interprets it

=> Error handling - It's good engineering practice to separate the code that generates the errors from the code that reports them.

## Lexemes and Tokens

- Scan through a list of characters and group them together into the smallest sequences that still represent something. Each of these blobs of characters is called a lexeme.

- When we take the lexeme and bundle it together with other data = token

## Tokens

- Which reserved word (while, do etc) and which keyword.
- At the point we recognize a lexeme, we also remember which lexeme it represents. We have a different type for each keyword, operator, bit of punctuation and literal type
- When we create a token, we also produce the actual string value that will be used later by the interpreter

- Lox supports multistrings

## Numbers

- All numbers in Lox are floating point at runtime
- Both integer and decimal literals are supported.
- Leading / trailing decimal point are unsuported

## Maximal Munch

When two lexical grammar rules can both match a chunk of code that the scanner is looking at, whichever one matches the most characters wins. Which means we can't easily detect a reserved word until we've reached the end of what might instead be an identifier.

## compiling

javac com/craftinginterpreters/lox/\*.java

## Running the shell

java com.craftinginterpreters.lox.Lox

### To delete the classes and recompile

find com/craftinginterpreters/lox -name "\*.class" -delete

# Representing Code

- Formal grammars
- Feel the difference between functional and object-oriented programming
- Go over a couple of design patterns
- Metaprogramming

A represetation for code - It should be easy for the parser to produce and easy for the interpreter to consume.

1 + 2 \* 3 - 4

- Order of operations - One way to visualize that precedence is using a tree. Leaf nodes are numbers, and interior nodes are operators with brances for eeach of their operands
- In order to evaluate an arithmetic node, you need to know the numeric values of it's subtrees, so you have to evaluate those first. That means working your way up from the leaves up to the root. Following BODMAS, 2 \* 3 are the lowest leaves. This is called a post order traversal

### Context Free Grammars (CFG)

Our scanner emits a flat sequence of tokens
But now we we need to handle expressions that can nest arbitrarily deeply. Which is where the CFG comes in.

### Rules for Grammars

Strings => Derivations - derived from rules of grammar
Rules => Productions - Produce strings in the gramar
Each production in a context free grammar has a head, it's name and a body, whch describes what it generates
The body is a list of symbols and come in two delectable flavours:

- A terminal - a letter from the grammar's alphabet -> they don't lead to any futher moves in the game
- A nonterminal - a named reference to another rule in the grammar -> 'play the rule and insert whatever it produces here'

You may have multiple rules with the same name. When you reach a non-terminal with that name, you are allowed to pick any of the rules for it, whichever floats your boat.

### Recursion

Recursion in the grammar is a goo sgn that the language being defined is context-free instead of regular. In particular, recursion where the recursive non-terminal has productions on both sides implies that the language is not regular.
Regular grammars can express repetition but they can;t keep count of how many repetitions there are, which is necessary to ensure that the string has the same number of width and on the side parts.

### Metalanguages

- Created by Robert Milnner
- Here, you don't have classes with methods. Types and functions are totally distinct.
- To implement an operation for a number of different types, you define a single function, in the body of that function, you use pattern matching to implement the operation for each type all in one place.

### The Visitor Pattern

- Many think the pattern has to do with traversing trees, which isn’t the case at all.
- We are going to use it on a set of classes that are tree-like, but that’s a coincidence
- It's about approximating the functional style within an OOP language

# Parsing Expressions

Lox's current expression grammar

expression → literal
| unary
| binary
| grouping ;

literal → NUMBER | STRING | "true" | "false" | "nil" ;
grouping → "(" expression ")" ;
unary → ( "-" | "!" ) expression ;
binary → expression operator expression ;
operator → "==" | "!=" | "<" | "<=" | ">" | ">="
| "+" | "-" | "\*" | "/" ;

- Binary rule lets operands nests in any way they want which can affect the result of evaluating the parsed tree
  - Precedence determins which operator is evaluated first in an expression containing a mixture of different operators.
  - Associativity determines which operator is evaluated first in a series of the same operator
  - left associative - MINUS => 5 - 3 - 1 => (5 -3) -1
  - right associative - assignment operator (=), a = b = c => a = (b = c)

Lox's precedence and associativity, similar to C
| Name | Operators | Associates |
| ---------- | ----------------- | ---------- |
| Equality | `==` `!=` | Left |
| Comparison | `>` `>=` `<` `<=` | Left |
| Term | `-` `+` | Left |
| Factor | `/` `*` | Left |
| Unary | `!` `-` | Right |

### Recursive Descent Parsing

- Simplest way to build a parser and doesn't require using complex parser generator tools like Yacc, Bison or ANTLR
- Fast, robust, and can support sophisticated error handling
- GCC, V8 and Roslyn use recursive descent
- Considered a top down parser as it starts from the top or the outermost grammar rule(expression) down to nested subexpressions

### Syntax Errors

A parser has two jobs

- Given a valid sequence of tokens, produce a corresponding syntax tree.
- Given an invalid sequence of tokens, detect any errors and tell the user about their mistakes

When a parser runs into a syntax error, it must:

- Detect and report the error.
- Avoid crashing or hanging

A decent parser should be fast, report as many distinct errors as there are and minimize cascaded errors.

Error recovery - The way a parser responds to an error and keeps going to look for later errors.

#### Panic mode error recovery

Parser enters panic mode as soon as it detects an error.

Then it goes into synchronization - Gets its state and the sequence of forthcoming tokens aligned such that the next token does match the rule being parsed

# Evaluating Expressions

Options:

- compile source code into machine code
- Translate t to another high-level language
- Reduce it to some byte code format for a virtual machine to run

For our first interpreter, we will take the simplest shortest path and execute the syntax tree itself. Evaluate an expression and produce a value

## Representing values

- In Lox, values are created by literals, computed by expressions and stored in variables .
- Given a Java variable with that static type, we must also be able to determine which kind of value it holds at runtime
- - => Is it adding two numbers or concatenating two strings? We use a Java type that can hold numbers, strings, Boolean and more; java.lang.Object
    - We determine if the runtime value is a number or a strng using Java's instanceof operator

## Evaluating literals

- Literals are the leaves of an expression tree
- A literal is a bit of syntax that produces a value
- Comes from the parsers domain
- Values are an interpreter's concept , part of the runtime world

## Evaluating unary expressions

- Lox follows Ruby's simple rule: false and nil are false, and everything else is Truthy

## Evaluating binary operators

- Lox doesn't do implicit conversions in equality and Java does not either
- We do have to handle nil/null specially so that we don't throw a NullPointerException if we try to call equals() on null

# Runtime Errors

- Failures that the language semantics demand we detect and report while the program is running (hence the name)
- The fact that Lox is implemented in Java should be hidden to the user - graceful runtime error handling. Instead, we want them to understand a Lox runtime error occured and give them a message relevant to our language and their program
- While a runtime error needs to stop evaluating the expression, it shouldn't kill the interpreter.

# Statements and State

- To support bindings, our interpreter needs internal state
  - Define a variable at the beginning of the program => Hold on to the value of the aribale => Use the ariable at the end of the program
- State and statements go hand in hand
  - Staemnets - A side effect.

In Lox: - An expression statement lets you place an expression where a statement is expected. - A print statement evaluates an expression and displays the result to the user.
Lox is an imperatie, dynamically typed language

- There is no place in grammar where both an expression and a statement are allowed. Operands of + are always expressions, never statements. The body of a while loop is always a statement
  - Hence Expressions and statements are split into separate classes.
