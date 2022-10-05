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
public enum TokenType {
    // Tokens de apenas um caractere.
    PARENTESES_ESQ, PARENTESES_DIR, CHAVES_ESQ, CHAVES_DIR, VIRGULA, MENOS,
    MAIS, PONTO_VIRGULA, BARRA, ESTRELA, NEGACAO,
    
    // Tokens de dois caracteres.
    IGUAL, DIFERENTE,
    MAIOR, MAIOR_IGUAL,
    MENOR, MENOR_IGUAL,
    OU, E,
    ATRIBUICAO,
    
    // Literais.
    IDENTIFICADOR, STRING_LITERAL, NUMBER_LITERAL,
    
    // Palavras-chave.
    FUN, WHILE, VAR, IF, ELSE, FALSE, TRUE, RETURN,
    NIL, WRITE, FOR,
    
    EOF
}
