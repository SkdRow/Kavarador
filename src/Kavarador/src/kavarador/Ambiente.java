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
    private final Ambiente ambienteInterno;
    private final Map<String, Object> valores = new HashMap<>();
    
    public Ambiente() {
        this.ambienteInterno = null;
    }
    
    public Ambiente(Ambiente ambienteInterno) {
        this.ambienteInterno = ambienteInterno;
    }
    
    Object get(Token name) {
        if (valores.containsKey(name.getLexeme())) {
            return valores.get(name.getLexeme());
        }
        
        if (ambienteInterno != null) {
            return ambienteInterno.get(name);
        }
        
        throw new RuntimeError(name, "Variável não definida '" + name.getLexeme() + "'.");
    }
    
    void definir(String name, Object valor) {
        valores.put(name, valor);
    }
    
    void definir(Token name, Object valor) {
        if (valores.containsKey(name.getLexeme())) {
            valores.put(name.getLexeme(), valor);
            return;
        }
        
        if (ambienteInterno != null) {
            ambienteInterno.definir(name, valor);
            return;
        }
        
        throw new RuntimeError(name, "Variável não definida '" + name.getLexeme() + "'.");
    }
}
