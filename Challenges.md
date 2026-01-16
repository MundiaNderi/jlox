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
