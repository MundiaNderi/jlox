## Scanner Challenges

### The lexical grammars of Python and Haskell are not regular. What does that mean, and why aren’t they?

[Lexical analysis in Python](https://docs.python.org/3/reference/lexical_analysis.html). Also [here](https://docs.python.org/3/reference/grammar.html)
One of the reasons this grammar is not 'regular' is because each lexical element can contain multiple derivations. Choosing which derivation to follow is part of the lexing algorithm, and that algorithm uses a memory construct called a 'stack' (or something that serves the purpose of a 'stack') to remember certain things about where it is inside of the parsing process. These are called "Context-Free Grammars", to distinguish them from grammars that have even stronger memory requirements to build and maintain a 'context' as they are parsed.

Regular grammars, in contrast, only ever have one derivation\*, and that derivation can be followed without any additional memory structure beyond a notion of the previously parsed lexical element.

In essence, regular grammars can be parsed with no 'memory', but most languages need at least a stack-like memory to be parsed.

### Aside from separating tokens—distinguishing print foo from printfoo—spaces aren’t used for much in most languages. However, in a couple of dark corners, a space does affect how code is parsed in CoffeeScript, Ruby, and the C preprocessor. Where and what effect does it have in each of those languages?

In C, a single space can change what kind of macro you define

```
#define FOO(x) x + 1 // function-like macro
@define FOO (x) x + 1 // object-like macro named FOO
```

In Ruby, spaces affect how Ruby decides between method calls, variables, and operators

```
foor bar # method call: foo(bar)
foobar # local variable or method name

a = -1 # unary minus
a = - 1 # also unary minus
a-1 #subtraction
```

In CoffeeScript, whitespace can change the structure of the AST, not just Token separation. Like Python, identation(spaces) defines blocks. Space help determine whether something is a call, an argument list or a nested expression not just where tokens split.

```
new Foor bar # new Foo(bar)
new Foo(bar) # same

f x, y # function call -> same as f(x, y)
f x:y # object literal passed to f, same as f(x:y)

f x.y # f(x.y)
f(x).y  # property access after call
```

### Our scanner here, like most, discards comments and whitespace since those aren’t needed by the parser. Why might you want to write a scanner that does not discard those? What would it be useful for?

- For tools that need to understand the code structure beyond just execution. Like code formatters, linters, static analyzers or for surce-to-source transformations
- Think ESLint, prettier or Black
- Static analysis like Javadocs
- Source-to-Source transformation - Transpilation - When inserting code eg logging into codebases, you must preserve comments or whitespaces to avoid breaking the code or confusing developers

### Add support to Lox’s scanner for C-style /_ ... _/ block comments. Make sure to handle newlines in them. Consider allowing them to nest. Is adding support for nesting more work than you expected? Why?

#### Our interpreter carefully checks that the number of arguments passed to a function matches the number of parameters it expects. Since this check is done at runtime on every call, it has a performance cost. Smalltalk implementations don’t have that problem. Why not?

When a message is sent to an object, a method will be selected and executed. Since we cannot know, in general, the class of the object until run-time, the method cannot be selected until the message is actually sent. This is called "dynamic binding", and Java, C++, and Smalltalk all have it. With straight functions, the compiler can look at a "call" statement and figure out at compile-time (i.e., "statically") which body of code to branch to. C++ (which always prefers efficiency over clarity) encourages static binding and refers to dynamically bound methods as "virtual" methods, and refers to the virtual table.
[See](https://docs.huihoo.com/smalltalk/Smalltalk-Overview.html#:~:text=When%20a%20message%20is%20sent,of%20code%20to%20branch%20to)

In Smalltalk, the number of arguments a method accepts is encoded directly in the message name (selector) itself. For example:

add → 0 arguments (unary message)
add: → 1 argument (the colon is part of the name)
add:to: → 2 arguments (two colons = two arguments)

So when a message is sent, the selector already unambiguously encodes the arity. There's no way to send add:to: with 3 arguments — it's a contradiction in terms. The argument count mismatch cannot happen by construction, so there's nothing to check at runtime.
Compare this to a language like Lox (or C), where a function named add gives you no information about how many arguments it takes — you have to look up the definition and verify the count separately, which is what the runtime check is doing.
In other words:
Language Arity check needed? Why Lox/CYes, at runtimeFunction name carries no arity info
Smalltalk - No - Selector syntax encodes arity structurally

## Why is it safe to eagerly define the variable bound to a function’s name when other variables must wait until after they are initialized before they can be used?

Function definitions are immutable, known at compile/parse time, and do not dpend on runtime execution order, unlike variables

- Functions generally represent a static block of code, whereas variables hold state that changes
- In languages like C or iterpreters like JavaScript, the compiler / interpreter performms a pass to find function declaratios and map their memory addresses before running the actual code logic
- Hoisting behaviour - In many languages, functions are hoisted at the top of their scope, allowing them to be invoked earlier in the file than their declaration.
-

The above us a throwback from when compilers needed to know the size of the stack for the function before they instantiated the function's code.

### How do other languages you know handle local variables that refer to the same name in their initializer, like:

```
var a = "outer";
{
var a = a;
}
```

### Is it a runtime error? Compile error? Allowed? Do they treat global variables differently? Do you agree with their choices? Justify your answer.

JavaScript:

- var keyword - is function scoped and hoisted. In the above snippet, the inner var a refers to the same variable as the outer a because var ignores block scope. Value of a remains outer.
- Let and const - are block scoped and introduce a Temporal Dead Zone (TDZ) - which is the period between the entering of a scope and the actual declartion of a variable using let or const
