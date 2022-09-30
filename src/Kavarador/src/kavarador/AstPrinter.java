package kavarador;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author guilherme.tonetti
 */
public class AstPrinter implements Expressao.Visitor<String> {
  String print(Expressao expr) {
    return expr.accept(this);
  }
  
    @Override
    public String visitBinaryExpressao(Expressao.Binaria expr) {
        return parenthesize(expr.operator.getLexeme(),
                          expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpressao(Expressao.Agrupamento expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpressao(Expressao.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpressao(Expressao.Unaria expr) {
        return parenthesize(expr.operator.getLexeme(), expr.right);
    }
    
    private String parenthesize(String name, Expressao... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expressao expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
  }

    @Override
    public String visitAssignExpressao(Expressao.Atribuicao expr) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
