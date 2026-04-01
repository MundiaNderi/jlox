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
  - Statements - A side effect.
  - Assignment is an expression and not a statement

In Lox: - An expression statement lets you place an expression where a statement is expected. - A print statement evaluates an expression and displays the result to the user.
Lox is an imperatie, dynamically typed language

- There is no place in grammar where both an expression and a statement are allowed. Operands of + are always expressions, never statements. The body of a while loop is always a statement
  - Hence Expressions and statements are split into separate classes.

- Statement produce no values, so the return type of the visit is void, not object

## Global Variables

- Variable declaration - brings a new variable into the world. Binds a name to a value
- Variable expression - accesses that binding

=> Declaration statements

# Enviroments

Where variables live in memory

- A variable statement does not just define a new variable, it can be used to redefine an existing variable.
- Currently allowed for global variables
- It's okay to refer to a variable before its defined as log as you don't evaluate the reference.
- Lox sets a variable to nil if it's not explicitly defined

### Assignment Syntax

- Assignment is right associative
- An L value evaluates to a storage location that you can assign into.
- We want the syntax tree to reflect that an l-value evaluates to a storage location that you can assign into.

### Scopes

- A scope defines a region where a name maps to a certain entity
- Lexical scope - The text of the program itself shows where a scope begins and ends.
- Dynamic scope - you need to execute the code to know what the variable refers to

# Control Flow

## Conditional Execution

- Conditional or branching control flow - used to not execute some piece of code
- Looping control flow - executes a chunk of code more than once

- Lox doesn't have a conditional operator

### Logical Operators

- For an AND expression to evealuate to something truthy, both operands must be truthy.

### Desugaring

- A process where the front end takes code using syntax sugar and translates it to a more primitive form that the back end already knows how to execute.
- Lox desugars for loops, while loops and other statements that the interpreter already handles

# Functions

- It is the parentheses followiing an expression that indicate a function call.

# Function's Arity

Arity is the fancy term for the nuber of arguments a function or operation accepts.
3 arguments = arity 3

- Statically typed languages check this at compile time and refuse to compile the code if the argument count does not match the function's arity
- Python raises a runtime eeror if the argument list is too short or too long which is the approach we'll take with Lox.

### Native Functions

These are functions that the interpreter exposes to user code but are implemented in the host language(in our case Java), not the language being implemnted (lox)

- Also sometimes called Primitives / External functions / Foreign functions
- Form part of implementation's runtime

Foreign function interface (FFI) - Mechanism for languages to provide their own native functions

#### Telling Time

- Benchmarks - programs that measure the time it takes to exercise some corner of the interpreter.
- Nice solution - have the benchmark script itself measure the time elapsed between two points in the code

#### Return Statements

- Expression oriented languages - Ruby , Scheme
- A return statement is the return keyword followed by an optional expresion and terminated by a semicolon.
- In statically typed lanaguages, void functions don't return a value and non-void ones do.
- Since Lox is dynamically typed, there are no true void functions

#### Local Functions and Closures

- Lox supports local functions that are defined inside another function, or nested inside a block.
  Clousures
- A data structure - closes over and holds on the surrounding variables where the function is declared.

## Smalltalk Language

A white paper overview: https://docs.huihoo.com/smalltalk/Smalltalk-Overview.html#:~:text=When%20a%20message%20is%20sent,of%20code%20to%20branch%20to.

- Started the Object Oriented Programming revolution
- Introduced the basic ideas of object, class, message, methd and inheritance
- The original Just in time compiling researc was part of the Smalltalk project

The basic concepts of Smalltalk are: Objects, Fields, OOPs, Classes, Methods, Messages, Inheritance, Receiver, Dynamic Binding

# Resolving and Binding

- A variable usage refers to the preceding declaration with the same name in the innermost scope that encloses the expression where the variable is used.

## Scopes and mutable enviroments

- We create a new environment when we enter a new scope, and discard it when we leave the scope.
- Consider all of the code within a block as being within the same scope, so our interpreter usues a single environment to represent that. Each environment is a mutable hash table

### Persistent environments and persistent data structures

- A persistent data structure can never be directly modified.
- Any modification to an existing structure produces a new object that contains all of the original data and the new modification
- A closure retains a reference to the Environment instance in play when the function was declared.

### Semantic Analysis

- Static scope - a variable usage always resolves to the same declatation

Solution: Resolve each vatiable once ie, Semantic Analysis -> write a chunk of code that inspects the user's program, finds every variable mentioned and figures out which declaration it refers to.

Our resolver:

- Each time it visits a variable, it tells the interpreter how many scopes thereare between the current scope and the scope where the variable is defined
- At runtime, this corresponds to the number of enviroments between the current one and the enclosing one where the interpreter can find the variable's value
- We always have to keep the resolver's scope chains and the interpreters linked environments in sync with each other.
- At runtime, we create the environment after we find the method on the instance

# Classes

There are three broad paths to Object oriented programming:

- Classes - came first and are the most popular
- Prototypes - the key idea is that an object can spawn other objects similar to itself
- Multimethods

The main goal is to bundle data with the code that acts on it. Users do by that by declaring a class that:

- Exposes a constructor to create and initialize new instances of the class
- Provides a way to store and access fields on instances
- Defines a set of methods shared by all instances of the class that operate on each instances' state.

### Properties on Instances

Lox follows JavaScript and Python in how it handles state. Every instance is an open collection of named values. Methods on the instance’s class can access and modify properties, but so can outside code.

- Properties are accessed using a . syntax.

```
someObject.someProperty
```

- In Lox, only instances of classes have properties. If the object uses some other type like a number, invoking a getter on it is a runtime error.
- Fields are named bits of state stored directly in an instance
- Properties are the named things that a get expression may return

### Set Expressions

- Setters use the same syntax as getters, except they appear on the left side of an assignment
- Unlike getters, setters don't chain. However, the reference to call allows any high-precedence expression before the last dot, including any number of getters.

#### Methods on Classes

- Instances are just maps and all instances are more or less the same.
- To make them feel like instances of classes, we need behaviour methods
- A method call chains getters and function calls together

```
object.method(argument)
```

- Like Python and C# we will have methods "bind" this to the original instance when the method is first grabbed. Python calls these bound methods
- Where an instance stores state, class stores behaviour
- When accessing a property, you might get a field - a bit of state stored on the instace - or you could hit a method defined on the instance's class.

## This

- Inside a method body a this expression ealuates to the instance that the method was called on.
  - Since methods are accessed and then invoked as two steps, it will refer to the object that the method was accessed from.
  - Whenever a this expression is encountered, (at least inside a method) it will resolve to a local variable defined in an implicit scope just oustdoe of the block for the method body
  -

## Constructors and Initializers

- Methods and fields let us encapsulate state and behaviour together so that an object always stays in valid configuration.
- To ensure a brand new object starts in a good state, we need constructors.

Constructing an object is a actually a pair of operations:

- The runtime allocates the memory required for a fresh instance
  - In most languages, this operation is at a fundamental level beneath what user code is able to access
- Then, a user provided chunkc of code is called which initializes the unformed object.
- In Lox, init() methids always return this, even when directly called.
  Python -> init()

# Prototypes

- LoxClass - where behaviour for the objects lies
- LoxInstance - where we define state

- What if you could define methods right on a single object, inside LoxInstance? LoxClass would not be needed at all
  - Prototypes are simpler than classes
    - Reuse behaviour accross multiple instances without classes
    - An instance delegates directly to another instance to reuse its fields and methods, like inheritance

With prototypes, objects inherit directly from other objects.
With classes, classes inherit directly from other classes.
