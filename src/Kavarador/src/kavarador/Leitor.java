/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kavarador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Token;
import model.TokenType;

import static model.TokenType.*; 

/**
 *
 * @author guilherme.tonetti
 */
public class Leitor {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int inicioToken = 0; // Aponta para o primeiro caractere do lexeme.
    private int caractereAtual = 0; // Aponta para o caractere sendo lido no momento.
    private int linha = 1;
    private static final Map<String, TokenType> palavras_chave;
    
    static {
        palavras_chave = new HashMap<>();
        palavras_chave.put("false",   FALSE);
        palavras_chave.put("true",    TRUE);
        palavras_chave.put("nil",     NIL);
        palavras_chave.put("if",      IF);
        palavras_chave.put("else",    ELSE);
        palavras_chave.put("for",     FOR);
        palavras_chave.put("fun",     FUN);
        palavras_chave.put("while",   WHILE);
        palavras_chave.put("string",  STRING_VAR);
        palavras_chave.put("number",  NUMBER_VAR);
        palavras_chave.put("boolean", BOOLEAN);
        palavras_chave.put("return",  RETURN);
        palavras_chave.put("write",   WRITE);
    };
    
    public Leitor(String source) {
        this.source = source;
    }
    
    /**
     *
     * @return 
     */
    public List<Token> lerTokens() {
        while (!this.estaNoFim()) {
            // A cada execução do loop, executará o próximo token individualmente.
            this.inicioToken = this.caractereAtual;
            lerProximoToken();
        }
        
        this.tokens.add(new Token(linha, EOF, null, ""));
        
        return this.tokens;
    }
    
    /**
     * Responsável por tratar os próximos caracteres do script e adequá-los ao
     * token correto.
     * 
     * Estratégia geral para identificar lexemas grandes: após detecar o começo
     * de um, percorre o restante dos caracteres até identificar seu fim.
     * 
     * Sempre que houver um erro o programa não interromperá a aplicação e
     * continuará executando as demais, pois o objetivo é demonstrar a maior
     * quantidade de erros possível em uma única compilação.
     */
    private void lerProximoToken() {
        char token = avancar();
        switch (token) {
            // Caracteres que não precisam de tratamento para serem identificados.
            case '(': adicionarToken(PARENTESES_ESQ); break;
            case ')': adicionarToken(PARENTESES_DIR); break;
            case '{': adicionarToken(CHAVES_ESQ); break;
            case '}': adicionarToken(CHAVES_DIR); break;
            case ',': adicionarToken(VIRGULA); break;
            case '-': adicionarToken(MENOS); break;
            case '+': adicionarToken(MAIS); break;
            case ';': adicionarToken(PONTO_VIRGULA); break;
            case '*': adicionarToken(ESTRELA); break;
            case '!': adicionarToken(NEGACAO); break;
            // Para os próximos caracteres, há a possiblidade do seguinte pertencer
            // a outra palavra-chave, portanto precisa identificá-lo.
            case '>': 
                adicionarToken(verificarProximoToken('=')
                    ? MAIOR_IGUAL
                    : MAIOR);
                break;
            case '<':
                adicionarToken(verificarProximoToken('=')
                    ? MENOR_IGUAL
                    : verificarProximoToken('>') ? DIFERENTE : MENOR);
                 break;
            case '=':
                if (verificarProximoToken('=')) {
                    adicionarToken(IGUAL);
                } else {
                    Kavarador.reportarErro(this.linha, "", "O operador de comparação deve possuir dois =.");
                }
                break;
            case '/':
                // Pode ser que seja um comentário ou divisão.
                if (verificarProximoToken('/')) {
                    // É um comentário, portanto somente deve finalizar no término
                    // da respectiva linha.
                    while (olhar() != '\n' && !estaNoFim()) avancar();
                } else {
                    adicionarToken(BARRA);
                }
                break;
            // Espaços em branco, tabs e carriage return.
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n': this.linha++; break;
            case '"': adicionarString(); break;
            
            // Palavras-chave
            case ':':
                if (verificarProximoToken('=')) {
                    adicionarToken(ATRIBUICAO);
                } else {
                    Kavarador.reportarErro(this.linha,"", "Os dois pontos da atribuição deve ser sucedido por =.");
                }                 
                break;
            case '|':
                if (verificarProximoToken('|')) {
                    adicionarToken(OU);
                } else {
                    Kavarador.reportarErro(this.linha, "", "O operador OU deve ser sucedido por outro |");
                }
                break;
            case '&':
                if (verificarProximoToken('&')) {
                    adicionarToken(E);
                } else {
                    Kavarador.reportarErro(this.linha, "", "O operador E deve ser sucedido por outro &");
                }
                break;
            default:
                if (Character.isDigit(token)) {
                    adicionarNumero();
                } else if (alfanumerico(token)) {
                    adicionarIdentificador();
                } else {
                    // Algum caractere não reconhecido foi encontrado no script
                    Kavarador.reportarErro(this.linha, "", "Caracter inesperado");
                }
                break;
        }
    }
    
    /**
     * Responsável por identificar o literal com base no token.
     * 
     * @param tipoToken
     */
    private void adicionarToken(TokenType tipoToken) {
        adicionarToken(tipoToken, null);
    }
    
    /**
     * Adiciona o token passado como argument na lista de tokens.
     * 
     * @param tipoToken Token que será adicionado.
     * @param literal 
     */
    private void adicionarToken(TokenType tipoToken, Object literal) {
        String text = source.substring(inicioToken, caractereAtual);
        tokens.add(new Token(linha, tipoToken, literal, text));
    }
    
    /**
     * 
     */
    private void adicionarString() {
        while (olhar() != '"' && !estaNoFim()) {
            if (olhar() == '\n') this.linha++;
            avancar();
        }
        
        if (estaNoFim()) {
            Kavarador.reportarErro(this.linha, "", "String não terminada.");
            return;
        }
        
        // Como ele somente olhou o próximo caractere e identificou que é uma aspas
        // fechando, precisa avançar o contador.
        avancar();
        
        // Retira as aspas duplas para adicionar somente o valor da string.
        String literal = this.source.substring(inicioToken + 1, caractereAtual - 1);
        adicionarToken(STRING_LITERAL, literal);
    }
    
    /**
     * O Kavar não permite adicionar pontuação no ínicio ou no fim do número.
     */
    private void adicionarNumero() {
        // Consumir a quantidade que der para os números.
        while (Character.isDigit(olhar())) {
            avancar();
        }
        
        if (olhar() == '.' && !Character.isDigit(olharProximo())) {
            Kavarador.reportarErro(linha, "", "O número não pode ser terminado com ponto.");
            return;
        }
        
        // Identificar se é um número fracional.
        if (olhar() == '.') {
            avancar();
            
            while (Character.isDigit(olhar())) avancar(); 
        }
        
        adicionarToken(
                NUMBER_LITERAL,
                Double.parseDouble(source.substring(inicioToken, caractereAtual)));
    }
    
    /**
     * 
     */
    private void adicionarIdentificador() {
        while (identificadorValido(olhar())) {
            avancar();
        }
        
        String value = source.substring(inicioToken, caractereAtual);
        TokenType tokenType = palavras_chave.get(value);
        
        if (tokenType == null) {
            tokenType =  IDENTIFICADOR;
        }
        
        adicionarToken(tokenType, value);
    }
    
    /**
     * Verifica o próximo caractere e retorna verdadeiro caso seja igual ao
     * esperado, falso caso contrário.
     * 
     * @param caracterEsperado O próximo caractere a ser verificado.
     */
    private boolean verificarProximoToken(char caracterEsperado) {
        if (estaNoFim()) return false;
        if (source.charAt(caractereAtual) != caracterEsperado) return false;
        
        caractereAtual++;
        return true;
    }
    
    /**
     * Verifica se o caracter é uma letra ou underline.
     * 
     * @param c
     * @return verdadeiro caso seja, falso caso não.
     */
    private boolean alfanumerico(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               (c == '_');
    }
    
    private boolean identificadorValido(char c) {
        return alfanumerico(c) || Character.isDigit(c);
    }
    
    /**
     * Esta função utiliza a técnica lookahead, identificando o próximo caractere
     * sem incrementar o contador.
     * 
     * Veja os seguintes links para mais detalhes sobre seu funcionamento:
     * https://www.vivaolinux.com.br/artigo/Expressoes-Regulares-Entenda-o-que-sao-Lookahead-e-Lookbehind
     * https://www.rexegg.com/regex-lookarounds.html
     * 
     * @return O caractere que está logo na frente do próximo token.
     */
    private char olhar() {
        if (estaNoFim()) return '\0';
        return source.charAt(caractereAtual);
    }
    
    /**
     * 
     * @return 
     */
    private char olharProximo() {
        if (caractereAtual + 1 >= this.source.length()) return '\0';
        return source.charAt(caractereAtual + 1);
    }
    
    /**
     * Função que identifica se o caractere sendo lido está ou não no fim do
     * script.
     * 
     * Retorna falso caso não esteja, verdadeiro caso contrário.
     */
    private boolean estaNoFim() {
        return caractereAtual >= source.length();
    }
    
    /**
     * 
     * @return 
     */
    private char avancar() {
        return source.charAt(this.caractereAtual++);
    }
}
