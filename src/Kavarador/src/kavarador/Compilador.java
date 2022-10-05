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
public class Compilador implements Expressao.Visitor<Object>, Declaracao.Visitor<Void> {
    private Ambiente ambiente = new Ambiente();
    
    void compilar(List<Declaracao> declaracoes) {
        try {
            for (Declaracao declaracao : declaracoes) {
                executar(declaracao);
            }
        } catch (RuntimeError error) {
            Kavarador.reportarErroEmExcecucao(error);
        }
    }
    
    private void executar(Declaracao declaracao) {
        declaracao.accept(this);
    }
    
    @Override
    public Object visitBinaryExpressao(Expressao.Binaria expr) {
        Object esquerda = avaliar(expr.left);
        Object direita = avaliar(expr.right);
        
        switch (expr.operator.getTipoToken()) {
            case MAIOR:
                checarOperandos(expr.operator, esquerda, direita);
                return (double)esquerda > (double)direita;
            case MAIOR_IGUAL:
                checarOperandos(expr.operator, esquerda, direita);
                return (double)esquerda >= (double)direita;
            case MENOR:
                checarOperandos(expr.operator, esquerda, direita);
                return (double)esquerda < (double)direita;
            case MENOR_IGUAL:
                checarOperandos(expr.operator, esquerda, direita);
                return (double)esquerda <= (double)direita;
            case MENOS:
                checarOperandos(expr.operator, esquerda, direita);
                return (double)esquerda - (double)direita;
            case MAIS:
                if (esquerda instanceof Double && direita instanceof Double) {
                    return (double)esquerda + (double)direita;
                }
                
                if (esquerda instanceof String && direita instanceof String) {
                    return (String)esquerda + (String)direita;
                }
                
                if (esquerda instanceof String && direita instanceof Double) {
                    return (String) esquerda + ((Double)direita).toString();
                }
                
                throw new RuntimeError(expr.operator, "Operandos devem ser números ou strings.");
            case BARRA:
                checarOperandos(expr.operator, esquerda, direita);
                return (double)esquerda / (double)direita;
            case ESTRELA:
                checarOperandos(expr.operator, esquerda, direita);
                return (double)esquerda * (double)direita;
            case IGUAL:
                return ehIgual(esquerda, direita);
            case DIFERENTE:
                return !ehIgual(esquerda, direita);
        }
        
        return null;
    }

    @Override
    public Object visitGroupingExpressao(Expressao.Agrupamento expr) {
        return this.avaliar(expr.expression);
    }

    @Override
    public Object visitLiteralExpressao(Expressao.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpressao(Expressao.Unaria expr) {
        Object direita = avaliar(expr.right);
        
        switch (expr.operator.getTipoToken()) {
            case NEGACAO:
                return ehVerdadeiro(direita);
            case MENOS:
                checarOperando(expr.operator, direita);
                return -(double)direita;
        }
        
        return null;
    }
    
    private void checarOperando(Token operador, Object operando) {
        if (operando instanceof Double) return;
        
        throw new RuntimeError(operador, "Operando deve ser um número.");
    }   
    
    private void checarOperandos(Token operador, Object esquerda, Object direita) {
        if (esquerda instanceof Double && direita instanceof Double) return;
        
        throw new RuntimeError(operador, "Operandos devem ser números.");
    }   
    
    @Override
    public Object visitAssignExpressao(Expressao.Atribuicao expr) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    public Object visitVariableExpresssao(Expressao.Variavel expr) {
        return ambiente.get(expr.name);
    }
    
    /**
     * 
     * 
     * @param object
     * @return 
     */
    private boolean ehVerdadeiro(Object object) {
       if (object == null) return false;
       if (object instanceof Boolean) return (boolean)object;
       return true;
    }
    
    /**
     * 
     * @param expr
     * @return 
     */
    private Object avaliar(Expressao expr) {
        return expr.accept(this);
    }
    
    @Override
    public Void visitExpressaoDecl(Declaracao.Expr expr) {
        avaliar(expr.expression);
        return null;
    }

    @Override
    public Void visitWriteDecl(Declaracao.WriteExpr expr) {
        Object valor = avaliar(expr.expression);
        System.out.println(transformarEmTexto(valor));
        return null;
    }
    
    @Override
    public Void visitVarDecl(Declaracao.Var expr) {
        Object valor = null;
        if (expr.inicializador != null) {
            valor = avaliar(expr.inicializador);
        }
        
        ambiente.definir(expr.name.getLexeme(), valor);
        return null;
    }

    private boolean ehIgual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        
        return a.equals(b);
    }
    
    private String transformarEmTexto(Object valor) {
        if (valor == null) return "nil";
        
        return valor.toString();
    }
}
