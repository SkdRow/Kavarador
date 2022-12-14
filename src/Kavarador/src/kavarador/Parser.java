package kavarador;

import java.util.ArrayList;
import java.util.Arrays;
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

    /**
     * 
     * @param tokens 
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
    
    /**
     *
     * @return
     */
    public List<Declaracao> parse() {
        List<Declaracao> declaracoes = new ArrayList<>();

        while (!estaNoFim()) {
            declaracoes.add(declaracao());
        }

        return declaracoes;
    }
    
    private Declaracao declaracao() {
        try {
            if (igual(FUNCTION)) return funcao();
            if (igual(VAR)) return declaracaoVar();
            
            return statement();
        } catch (ParseError error) {
            sincronizar();
            return null;
        }
    }
    
    private Declaracao.Function funcao() {
        Token nome = consumir(IDENTIFICADOR, "Espera-se o nome da função.");
        
        consumir(PARENTESES_ESQ, "Espera-se ( após o nome da função.");
                
        List<Token> parameters = new ArrayList<>();
        if (!checar(PARENTESES_DIR)) {
            do {
                if (parameters.size() >= 255) {
                    error(olhar(), "Não pode ter mais do que 255 parâmetros");
                }
                
                parameters.add(consumir(IDENTIFICADOR, "Espera-se nome do parâmetro"));
            } while (igual(VIRGULA));
        }
        
        consumir(PARENTESES_DIR, "Espera-se ) após os parâmetros");
        consumir(CHAVES_ESQ, "Espera-se { antes do corpo da função");
        
        List<Declaracao> corpo = bloco();
        return new Declaracao.Function(nome, parameters, corpo);
    }
    
    private Declaracao declaracaoVar() {
        Token nome = consumir(IDENTIFICADOR, "Espera-se o nome da variável.");
        
        Expressao inicializador = null;
        if (igual(ATRIBUICAO)) {
            inicializador = expr();
        }
        
        consumir(PONTO_VIRGULA, "Espera-se ';' após a declaração.");
        return new Declaracao.Var(nome, inicializador);
    }
    
    private Declaracao statement() {
        if (igual(WRITE)) return writeStatement();
        if (igual(IF)) return ifStatement();
        if (igual(RETURN)) return returnStatement();
        if (igual(WHILE)) return whileStatement();
        if (igual(FOR)) return forStatement();
        if (igual(CHAVES_ESQ)) return new Declaracao.Bloco(bloco());
        
        return expressaoStatement();
    }
    
    private Declaracao writeStatement() {
        Expressao valor = expr();
        
        consumir(PONTO_VIRGULA, "Espera-se ';' após o valor.");
        return new Declaracao.WriteExpr(valor);
    }
    
    private Declaracao ifStatement() {
        consumir(PARENTESES_ESQ, "Espera-se '(' após o 'if'. ");
        
        Expressao condicao = expr();
        consumir(PARENTESES_DIR, "Espera-se ')' após a condição do if");
        
        Declaracao branchExecucao = statement();
        Declaracao branchElse = null;
        if (igual(ELSE)) {
            branchElse = statement();
        }
        
        return new Declaracao.If(condicao, branchExecucao, branchElse);
    }
    
    private Declaracao returnStatement() {
        Token palavrachave = anterior(); // Obtendo o return.
        Expressao valor = null;
        if (!igual(PONTO_VIRGULA)) {
            valor = expr();
        }
        
        consumir(PONTO_VIRGULA, "Espera-se ; após return.");
        return new Declaracao.Return(palavrachave, valor);
    }
    
    private Declaracao forStatement() {
        consumir(PARENTESES_ESQ, "Espera-se '(' após o 'for'. ");
        
        Declaracao inicializador;
        if (igual(PONTO_VIRGULA)) {
            inicializador = null;
        } else if (igual(VAR)) {
            inicializador = declaracaoVar();
        } else {
            inicializador = expressaoStatement();
        }
        
        Expressao condicao = null;
        if (!checar(PONTO_VIRGULA)) {
            condicao = expr();
        }
        
        consumir(PONTO_VIRGULA, "Espera-se ';' após a condição do for.");
        
        Expressao incremento = null;
        if (!checar(PARENTESES_DIR)) {
            incremento = expr();
        }
        
        System.out.println(incremento);
        
        consumir(PARENTESES_DIR, "Espera-se ')' após as cláusulas do for.");
        
        Declaracao corpo = statement();
        
        if (incremento != null) {
            corpo = new Declaracao.Bloco(
                Arrays.asList(
                    corpo,
                    new Declaracao.Expr(incremento)));
        }
        
        if (condicao == null) condicao = new Expressao.Literal(true);
        corpo = new Declaracao.While(condicao, corpo);
        
        if (inicializador != null) {
            corpo = new Declaracao.Bloco(Arrays.asList(inicializador, corpo));
        }
        
        return corpo;
    }
    
    private Declaracao whileStatement() {
        consumir(PARENTESES_ESQ, "Espera-se '(' após o 'while'.");
        
        Expressao condicao = expr();
        consumir(PARENTESES_DIR, "Espera-se ')' após a condição do while");
        
        Declaracao branchExecucao = statement();
        
        return new Declaracao.While(condicao, branchExecucao);
    }
    
    private Declaracao expressaoStatement() {
        Expressao valor = expr();
        
        consumir(PONTO_VIRGULA, "Espera-se ';' após o valor.");
        return new Declaracao.Expr(valor);
    }
    
    private List<Declaracao> bloco() {
        List<Declaracao> declaracoes = new ArrayList<>();
        
        while (!checar(CHAVES_DIR) && !estaNoFim()) {
            declaracoes.add(declaracao());
        }
        
        consumir(CHAVES_DIR, "Espera-se } após o bloco.");
        return declaracoes;
    }
    
    /**
     * 
     * @return 
     */
    private Expressao expr() {
        return atribuicao();
    }
    
    private Expressao atribuicao() {
        Expressao expressao = ou();
        
        if (igual(ATRIBUICAO)) {
            Token igual = anterior();
            Expressao valor = atribuicao();
            
            if (expressao instanceof Expressao.Variavel) {
                Token variavel = ((Expressao.Variavel) expressao).name;
                return new Expressao.Atribuicao(variavel, valor);
            }
            
            error(igual, "Atribuição inválida");
        }
        
        return expressao;
    }
    
    private Expressao ou() {
        Expressao expressao = e();
        
        while (igual(OU)) {
            Token operador = anterior();
            Expressao direita = e();
            expressao = new Expressao.Logical(expressao, operador, direita);
        }
        
        return expressao;
    }
    
    private Expressao e() {
        Expressao expressao = igualdade();
        
        while (igual(E)) {
            Token operador = anterior();
            Expressao direita = igualdade();
            expressao = new Expressao.Logical(expressao, operador, direita);
        }
        
        return expressao;
    }
    
    /**
     * 
     * @return 
     */
    private Expressao igualdade() {
        Expressao expressao = comparacao();
        
        while (igual(DIFERENTE, IGUAL)) {
            Token operador = anterior();
            Expressao direita = comparacao();
            expressao = new Expressao.Binaria(expressao, operador, direita);
        }
        
        return expressao;
    }

    /**
     * 
     * @return 
     */
    private Expressao comparacao() {
        Expressao expr = termo();
        
        while (igual(MAIOR, MAIOR_IGUAL, MENOR, MENOR_IGUAL)) {
            Token operador = anterior();
            Expressao direita = termo();
            expr = new Expressao.Binaria(expr, operador, direita);
        }
        
        return expr;
    }
    
    /**
     * 
     * @return 
     */
    private Expressao termo() {
        Expressao expr = fator();
        
        while (igual(MAIS, MENOS)) {
            Token operador = anterior();
            Expressao direita = fator();
            expr = new Expressao.Binaria(expr, operador, direita);
        }
        
        return expr;
    }
    
    /**
     * 
     * @return 
     */
    private Expressao fator() {
        Expressao expr = unaria();
        
        while (igual(ESTRELA, BARRA, MODULO)) {
            Token operador = anterior();
            Expressao direita = unaria();
            expr = new Expressao.Binaria(expr, operador, direita);
        }
        
        return expr;
    }
    
    /**
     * 
     * @return 
     */
    private Expressao unaria() {
        while (igual(NEGACAO, MENOS)) {
            Token operador = anterior();
            Expressao direita = unaria();
            return new Expressao.Unaria(operador, direita);
        }
        
        return chamada();
    }
    
    private Expressao chamada() {
        Expressao expr = primaria();
        
        while (true) {
            if (igual(PARENTESES_ESQ)) {
                expr = finalizarChamada(expr);
            } else {
                break;
            }
        }
        
        return expr;
    }
    
    private Expressao finalizarChamada(Expressao calle) {
        List<Expressao> arguments = new ArrayList<>();
        
        if (!checar(PARENTESES_DIR)) {
            do {
                if (arguments.size() >= 255) {
                    error(olhar(), "Não pode ter mais do que 255 parâmetros");
                }
                
                arguments.add(expr());
            } while (igual(VIRGULA));
        }
        
        Token parenteses = consumir(PARENTESES_DIR, "Espera-se ) após a expressão.");
        
        return new Expressao.Funcao(calle, parenteses, arguments);
    }
    
    /**
     * 
     * @return 
     */
    private Expressao primaria() {
        if (igual(FALSE)) return new Expressao.Literal(false);
        if (igual(TRUE)) return new Expressao.Literal(true);
        if (igual(NIL)) return new Expressao.Literal(null);
        
        if (igual(NUMBER_LITERAL, STRING_LITERAL)) {
            return new Expressao.Literal(anterior().getLiteral());
        }
        
        if (igual(IDENTIFICADOR)) {
            return new Expressao.Variavel(anterior());
        }
        
        if (igual(PARENTESES_ESQ)) {
            Expressao expr = expr();
            consumir(PARENTESES_DIR, "Espera-se ) após expressão.");
            return new Expressao.Agrupamento(expr);
        }
        
        throw error(olhar(), "Expressão esperada");
    }

    /**
     * 
     * @param types
     * @return 
     */
    private boolean igual(TokenType... types) {
        for (TokenType type : types) {
            if (checar(type)) {
                avancar();
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 
     * @param tokenType
     * @param message
     * @return 
     */
    private Token consumir(TokenType tokenType, String message) {
        if (checar(tokenType)) return avancar();
        
        throw error(olhar(), message);
    }
    
    /**
     * 
     * @param type
     * @return 
     */
    private boolean checar(TokenType type) {
        if (estaNoFim()) return false;
        
        return olhar().getTipoToken() == type;
    }
    
    /**
     * 
     * @return 
     */
    private Token avancar() {
        if (!estaNoFim()) tokenAtual++;
        return anterior();
    }
    
    /**
     * 
     * @return 
     */
    private boolean estaNoFim() {
        return olhar().getTipoToken() == EOF;
    }

    /**
     * 
     * @return 
     */
    private Token olhar() {
        return tokens.get(tokenAtual);
    }

    /**
     * 
     * @return 
     */
    private Token anterior() {
        return tokens.get(tokenAtual - 1);
    }
    
    /**
     * 
     * @param token
     * @param mensagem
     * @return 
     */
    private ParseError error(Token token, String mensagem) {
        Kavarador.reportarErro(token, mensagem);
        return new ParseError();
    }
    
    /**
     * 
     */
    private void sincronizar() {
        avancar();
        
        while (!estaNoFim()) {
            if (anterior().getTipoToken() == PONTO_VIRGULA) return;
            
            switch (olhar().getTipoToken()) {
                case FUNCTION:
                case VAR:
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
