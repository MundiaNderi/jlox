# jlox interpreter

## A Tree Walk Interpreter

- Based on crafting interpreters

A Tree walk interpreter executes code by directly traversing an AST (abstract syntax tree). +
/ \
 2 \*
/ \00
3 4

Each node represents a language construct, like an expression, statetement or an operator.

Scanner / Lexer - Breaks source code into tokens
↓
Parser - Builds the AST from tokens
↓
Interpreter - Walks the AST and interprets it

=> Error handling - It's good engineering practice to separate the code that generates the errors from the code that reports them.

## Lexemes and Tokens

- Scan through a list of characters and group them together into the smallest sequences that still represent something. Each of thes blobs of characters is called a lexeme.

- When we take the lexeme and bundle it together with other data = token

## Tokens

- Which reserved word (while, do etc) and which keyword.
- At the point we recognize a lexeme, we also remember which lexeme it represents. We have a different type for each keyword, operator, bit of punctuation and literal type
