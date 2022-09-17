/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author guilherme.tonetti
 */
public class Token {
    private final int linha; // Será usado ao reportar o erro.
    private final TokenType tipoToken;
    private final Object literal; // Identificador
    private final String lexeme; // 

    public Token(int linha, TokenType tipoToken, Object literal, String lexeme) {
        this.linha = linha;
        this.tipoToken = tipoToken;
        this.literal = literal;
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        StringBuilder token = new StringBuilder();
        
        token.append(this.tipoToken);
        token.append(" ");
        token.append(this.lexeme);
        token.append(" ");
        token.append(this.literal);
        
        return token.toString();
    }
}
