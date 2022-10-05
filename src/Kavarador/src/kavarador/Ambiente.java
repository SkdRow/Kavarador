/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kavarador;

import java.util.HashMap;
import java.util.Map;
import model.Token;

/**
 *
 * @author guilherme.tonetti
 */
public class Ambiente {
    private final Map<String, Object> valores = new HashMap<>();
    
    Object get(Token name) {
        if (valores.containsKey(name.getLexeme())) {
            return valores.get(name.getLexeme());
        }
        
        throw new RuntimeError(name, "Variável não definida '" + name.getLexeme() + "'.");
    }
    
    void definir(String name, Object valor) {
        valores.put(name, valor);
    }
}
