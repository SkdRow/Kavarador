/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kavarador;

import java.util.ArrayList;
import java.util.List;
import model.Token;
import model.TokenType;

import static model.TokenType.*; 

/**
 *
 * @author android
 */
public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int tokenStart = 0; // Aponta para o primeiro caractere do lexeme.
    private int currentCaracter = 0; // Aponta para o caractere sendo lido no momento.
    private int linha = 1;
    
    public Scanner(String source) {
        this.source = source;
    }
    
    /**
     *
     * @return 
     */
    public List<Token> lerTokens() {
        while (!this.estaNoFim()) {
            // A cada execução do loop, executará o próximo token individualmente.
            this.tokenStart = this.currentCaracter;
            lerProximoToken();
        }
        
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
        switch (avancar()) {
            // Caracteres que não precisam de tratamento para serem identificados.
            case '(': adicionarToken(PARENTESES_ESQ); break;
            case ')': adicionarToken(PARENTESES_DIR); break;
            case '{': adicionarToken(CHAVES_ESQ); break;
            case '}': adicionarToken(CHAVES_DIR); break;
            case ',': adicionarToken(VIRGULA); break;
            case '.': adicionarToken(PONTO); break;
            case '-': adicionarToken(MENOS); break;
            case '+': adicionarToken(MAIS); break;
            case ';': adicionarToken(PONTO_VIRGULA); break;
            case '*': adicionarToken(ESTRELA); break;
            // Para os próximos caracteres, há a possiblidade do seguinte pertencer
            // a outra palavra-chave, portanto precisa identificá-lo.
            case '!': adicionarToken(NEGACAO); break;
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
                    while (olharParaFrente() != '\n' && !estaNoFim()) avancar();
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
                // Algum caractere não reconhecido foi encontrado no script
                Kavarador.reportarErro(this.linha, "", "Caractere inesperado");
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
        String text = source.substring(tokenStart, currentCaracter);
        tokens.add(new Token(linha, tipoToken, literal, text));
    }
    
    /**
     * 
     */
    private void adicionarString() {
        while (olharParaFrente() != '"' && !estaNoFim()) {
            if (olharParaFrente() == '\n') this.linha++;
            avancar();
        }
        
        if (estaNoFim()) {
            Kavarador.reportarErro(this.linha, "", "String não terminada.");
        }
    }
    
    /**
     * Verifica o próximo caractere e retorna verdadeiro caso seja igual ao
     * esperado, falso caso contrário.
     * 
     * @param caracterEsperado O próximo caractere a ser verificado.
     */
    private boolean verificarProximoToken(char caracterEsperado) {
        if (estaNoFim()) return false;
        if (source.charAt(currentCaracter) != caracterEsperado) return false;
        
        currentCaracter++;
        return true;
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
    private char olharParaFrente() {
        if (estaNoFim()) return '\0';
        return source.charAt(currentCaracter);
    }
    
    /**
     * Função que identifica se o caractere sendo lido está ou não no fim do
     * script.
     * 
     * Retorna falso caso não esteja, verdadeiro caso contrário.
     */
    private boolean estaNoFim() {
        return this.currentCaracter >= this.source.length();
    }
    
    /**
     * 
     * @return 
     */
    private char avancar() {
        return this.source.charAt(this.currentCaracter++);
    }
}
