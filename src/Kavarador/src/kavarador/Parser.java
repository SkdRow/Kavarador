package kavarador;

import java.util.List;
import model.Token;
import model.TokenType;

import static model.TokenType.*;

/**
 *
 * @author guilherme.tonetti
 */
public class Parser {
    private static class ParseError extends RuntimeException {}
    
    private final List<Token> tokens;
    private int tokenAtual = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
    
    /**
     *
     * @return
     */
    public Expressao parse() {
        try {
            return expr();
        } catch (ParseError error) {
            sincronizar();
            return null;
        }
    }
    
    private Expressao expr() {
        return igualdade();
    }
    
    private Expressao igualdade() {
        Expressao expressao = comparacao();
        
        while (igual(DIFERENTE, IGUAL)) {
            Token operador = anterior();
            Expressao direita = comparacao();
            expressao = new Expressao.Binaria(expressao, operador, direita);
        }
        
        return expressao;
    }

    private Expressao comparacao() {
        Expressao expr = termo();
        
        while (igual(MAIOR, MAIOR_IGUAL, MENOR, MENOR_IGUAL)) {
            Token operador = anterior();
            Expressao direita = termo();
            expr = new Expressao.Binaria(expr, operador, direita);
        }
        
        return expr;
    }
    
    private Expressao termo() {
        Expressao expr = fator();
        
        while (igual(MAIS, MENOS)) {
            Token operador = anterior();
            Expressao direita = fator();
            expr = new Expressao.Binaria(expr, operador, direita);
        }
        
        return expr;
    }
    
    private Expressao fator() {
        Expressao expr = unaria();
        
        while (igual(ESTRELA, BARRA)) {
            Token operador = anterior();
            Expressao direita = unaria();
            expr = new Expressao.Binaria(expr, operador, direita);
        }
        
        return expr;
    }
    
    private Expressao unaria() {
        while (igual(NEGACAO, MENOS)) {
            Token operador = anterior();
            Expressao direita = unaria();
            return new Expressao.Unaria(operador, direita);
        }
        
        return primaria();
    }
    
    private Expressao primaria() {
        if (igual(FALSE)) return new Expressao.Literal(false);
        if (igual(TRUE)) return new Expressao.Literal(true);
        if (igual(NIL)) return new Expressao.Literal(null);
        
        if (igual(NUMBER_LITERAL, STRING_LITERAL)) {
            return new Expressao.Literal(anterior().getLiteral());
        }
        
        if (igual(PARENTESES_ESQ)) {
            Expressao expr = expr();
            consumir(PARENTESES_DIR, "Espera-se ) após expressão.");
            return new Expressao.Agrupamento(expr);
        }
        
        throw error(olhar(), "Expressão esperada");
    }

    private boolean igual(TokenType... types) {
        for (TokenType type : types) {
            if (checar(type)) {
                avancar();
                return true;
            }
        }
        
        return false;
    }
    
    private Token consumir(TokenType tokenType, String message) {
        if (igual(tokenType)) return avancar();
        
        throw error(olhar(), message);
    }
    
    private boolean checar(TokenType type) {
        if (estaNoFim()) return false;
        
        return olhar().getTipoToken() == type;
    }
    
    private Token avancar() {
        if (!estaNoFim()) tokenAtual++;
        return anterior();
    }
    
    private boolean estaNoFim() {
        return olhar().getTipoToken() == EOF;
    }

    private Token olhar() {
        return tokens.get(tokenAtual);
    }

    private Token anterior() {
        return tokens.get(tokenAtual - 1);
    }
    
    private ParseError error(Token token, String mensagem) {
        Kavarador.reportarErro(token, mensagem);
        return new ParseError();
    }
    
    private void sincronizar() {
        avancar();
        
        while (!estaNoFim()) {
            if (anterior().getTipoToken() == PONTO_VIRGULA) return;
            
            switch (olhar().getTipoToken()) {
                case FUN:
                case STRING_VAR:
                case NUMBER_VAR:
                case BOOLEAN:
                case FOR:
                case IF:
                case WHILE:
                case WRITE:
                case RETURN:
                    return;
            }
            
            avancar();
        }
    }
}
