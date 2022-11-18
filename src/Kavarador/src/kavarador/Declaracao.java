/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kavarador;

import java.util.List;
import model.Token;

/**
 *
 * @author guilherme.tonetti
 */
public abstract class Declaracao {
    abstract <R> R accept(Visitor<R> visitor);
    
    interface Visitor<R> {
        R visitBlocoDecl(Bloco expr);
        R visitExpressaoDecl(Expr expr);
        R visitFunctionDecl(Function expr);
        R visitIfDecl(If expr);
        R visitWhileDecl(While expr);
        R visitWriteDecl(WriteExpr expr);
        R visitReturnDecl(Return expr);
        R visitVarDecl(Var expr);
    }
    
    public static class Bloco extends Declaracao {
        List<Declaracao> declaracoes;
        
        public Bloco(List<Declaracao> declaracoes) {
            this.declaracoes = declaracoes;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlocoDecl(this);
        }
        
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
    
    public static class Function extends Declaracao {
        final Token name;
        final List<Token> parameters;
        final List<Declaracao> corpo;
        
        public Function(Token name, List<Token> parameters, List<Declaracao> corpo) {
            this.name = name;
            this.parameters = parameters;
            this.corpo = corpo;
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionDecl(this);
        }
    }
    
    public static class If extends Declaracao {
        final Expressao condicao;
        final Declaracao branchExecucao;
        final Declaracao branchElse;
        
        public If(Expressao condicao, Declaracao branchExecucao, Declaracao branchElse) {
            this.condicao = condicao;
            this.branchExecucao = branchExecucao;
            this.branchElse = branchElse;
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfDecl(this);
        }
    }
    
    public static class While extends Declaracao {
        final Expressao condicao;
        final Declaracao branchExecucao;
        
        public While(Expressao condicao, Declaracao branchExecucao) {
            this.condicao = condicao;
            this.branchExecucao = branchExecucao;
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileDecl(this);
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
    
    static class Return extends Declaracao {
        Token keyword;
        Expressao valor;
        
        Return(Token keyword, Expressao valor) {
            this.keyword = keyword;
            this.valor = valor;
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnDecl(this);
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
