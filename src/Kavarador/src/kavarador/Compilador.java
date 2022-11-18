/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kavarador;

import java.util.ArrayList;
import java.util.List;
import model.Token;
import model.TokenType;

/**
 *
 * @author guilherme.tonetti
 */
public class Compilador implements Expressao.Visitor<Object>, Declaracao.Visitor<Void> {
    final Ambiente globals = new Ambiente();
    private Ambiente ambiente = globals;
    
    Compilador() {
        globals.definir("clock", new KCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object chamar(Compilador compilador, List<Object> argumentos) {
                return (double)System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() {
                return "função nativa";
            }
        });
    }
    
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
            case MODULO:
                checarOperandos(expr.operator, esquerda, direita);
                return (double)esquerda % (double)direita;
            case IGUAL:
                return ehIgual(esquerda, direita);
            case DIFERENTE:
                return !ehIgual(esquerda, direita);
        }
        
        return null;
    }
    
    @Override
    public Object visitFuncaoExpressao(Expressao.Funcao expr) {
        Object calle = this.avaliar(expr.calle);
        
        List<Object> arguments = new ArrayList<>();
        for (Expressao argument : expr.argumentos) {
            arguments.add(avaliar(argument));
        }
        
        if (!(calle instanceof KCallable)) {
            throw new RuntimeError(expr.parenteses, "Somente funções suportam chamadas!");
        }
        
        KCallable funcao = (KCallable)calle;
        
        if (arguments.size() != funcao.arity()) {
            throw new RuntimeError(expr.parenteses, "Espera-se " +
                    funcao.arity() + " argumentos, mas obteve " +
                    arguments.size() + ".");
        }
        
        return funcao.chamar(this, arguments);
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
                return !ehVerdadeiro(direita);
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
    public Object visitLogicalExpressao(Expressao.Logical expr) {
        Object esquerda = avaliar(expr.left);
        
        if (expr.operador.getTipoToken() == TokenType.OU) {
            if (ehVerdadeiro(esquerda)) return esquerda;
        } else {
            if (!ehVerdadeiro(esquerda)) return esquerda;
        }
        
        return avaliar(expr.right);
    }
    
    @Override
    public Object visitAtribuicaoExpressao(Expressao.Atribuicao expr) {
        Object valor = avaliar(expr.value);
        ambiente.definir(expr.name, valor);
        return valor;
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
    
    public Void visitBlocoDecl(Declaracao.Bloco expr) {
        executarBloco(expr.declaracoes, new Ambiente(ambiente));
        
        return null;
    }
    
    @Override
    public Void visitExpressaoDecl(Declaracao.Expr expr) {
        avaliar(expr.expression);
        return null;
    }
    
    @Override
    public Void visitFunctionDecl(Declaracao.Function expr) {
        KFunction funcao = new KFunction(expr);
        ambiente.definir(expr.name.getLexeme(), funcao);
        return null;
    }
    
    @Override
    public Void visitIfDecl(Declaracao.If decl) {
        if (ehVerdadeiro(avaliar(decl.condicao))) {
            executar(decl.branchExecucao);
        } else if (decl.branchElse != null) {
            executar(decl.branchElse);
        }
        return null;
    }
    
    @Override
    public Void visitWhileDecl(Declaracao.While declr) {
        while (ehVerdadeiro(avaliar(declr.condicao))) {
            executar(declr.branchExecucao);
        }
        return null;
    }

    @Override
    public Void visitWriteDecl(Declaracao.WriteExpr expr) {
        Object valor = avaliar(expr.expression);
        System.out.println(transformarEmTexto(valor));
        return null;
    }
    
    @Override
    public Void visitReturnDecl(Declaracao.Return expr) {
        Object valor = null;
        if (expr.valor != null) valor = avaliar(expr.valor);
        
        throw new Return(valor);
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
    
    public void executarBloco(List<Declaracao> declaracoes, Ambiente ambiente) {
        Ambiente ambienteAnterior = this.ambiente;
        try {
            this.ambiente = ambiente;
            
            for (Declaracao declaracao : declaracoes) {
                executar(declaracao);
            }
        } finally {
            this.ambiente = ambienteAnterior;
        }
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
