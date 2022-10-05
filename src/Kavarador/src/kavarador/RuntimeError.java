/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kavarador;

import model.Token;

/**
 *
 * @author guilherme.tonetti
 */
public class RuntimeError extends RuntimeException {
    final Token token;
    
    public RuntimeError(Token operador, String mensagem) {
        super(mensagem);
        this.token = operador;
    }
    
    public String getMensagem() {
        return this.getMessage();
    }
}
