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
abstract class Expressao {
    abstract <R> R accept(Visitor<R> visitor);
    
    interface Visitor<R> {
        R visitAssignExpressao(Atribuicao expr);
        R visitBinaryExpressao(Binaria expr);
        R visitGroupingExpressao(Agrupamento expr);
        R visitLiteralExpressao(Literal expr);
        R visitUnaryExpressao(Unaria expr);
    }
    
    static class Atribuicao extends Expressao {
        final Token name;
        final Expressao value;
        
        Atribuicao(Token name, Expressao value) {
            this.name = name;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpressao(this);
        }
    }
    
    static class Binaria extends Expressao {
        final Expressao left;
        final Token operator;
        final Expressao right;
        
        Binaria(Expressao left, Token operator, Expressao right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpressao(this);
        }
    }
    
    static class Agrupamento extends Expressao {
        final Expressao expression;
        
        Agrupamento(Expressao expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpressao(this);
        }
    }
    
    static class Literal extends Expressao {
        final Object value;
        
        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpressao(this);
        }
    }
    
    static class Unaria extends Expressao {
        final Token operator;
        final Expressao right;
        
        Unaria(Token operator, Expressao right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpressao(this);
        }
    }
}
