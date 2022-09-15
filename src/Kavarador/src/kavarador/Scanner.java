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
    private int start = 0; // Aponta para o primeiro caractere do lexeme.
    private int current = 0; // Aponta para o caractere sendo lido no momento.
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
            this.start = this.current;
            lerProximoToken();
        }
        
        return new ArrayList<>();
    }
    
    /**
     * 
     */
    private void lerProximoToken() {
        switch (source.charAt(current++)) {
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
            default: Kavarador.reportarErro(linha, "", "Caractere inesperado"); break;
        }
    }
    
    private void adicionarToken(TokenType tipoToken) {
        adicionarToken(tipoToken, null);
    }
    
    private void adicionarToken(TokenType tipoToken, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(linha, tipoToken, literal, text));
    }
    
    /**
     *
     */
    private boolean estaNoFim() {
        return this.current >= this.source.length();
    }
}
