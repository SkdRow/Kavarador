/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kavarador;

import model.Token;

/**
 *
 * @author guilherme.tonetti
 */
public abstract class Declaracao {
    abstract <R> R accept(Visitor<R> visitor);
    
    interface Visitor<R> {
        R visitExpressaoDecl(Expr expr);
        R visitWriteDecl(WriteExpr expr);
        R visitVarDecl(Var expr);
    }
    
    public static class Expr extends Declaracao {
        final Expressao expression;
        
        Expr(Expressao expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressaoDecl(this);
        }
    }
    
    public static class WriteExpr extends Declaracao {
        final Expressao expression;
        
        WriteExpr(Expressao expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWriteDecl(this);
        }
    }
    
    static class Var extends Declaracao {
        final Token name;
        final Expressao inicializador;
        
        Var(Token name, Expressao inicializador) {
            this.name = name;
            this.inicializador = inicializador;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarDecl(this);
        }
    }
}
