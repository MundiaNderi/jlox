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
