package com.craftinginterpreters.lox;

// implements the Visitor interface
class AstPrinter implements Expr.Visitor<String> {
    String print(Expr expr) {
        return expr.accept(this);
    }

    // visit methods for each expression types
    // expressions with sub-expressions use the parenthesize helper methd
    @Override
    public String visitBinaryExpr(Expr.Binary Expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupigExr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null)
            return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    // takes a name and a list of expressions and wraps them in parantheses,
    // yielding a string like (+ 1 2)
    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            // calls accept on each sub-expression and passes in itself letting us print an
            // entire tree
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    // manualy instantiates a tree and prints it
    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1), new Expr.Literal(123)),
                new Token(TokenType.STAR, "*", null, 1), new Expr.Grouping(new Expr.Literal(45.67)));
        System.out.println(new AstPrinter().print(expression));
    }
}