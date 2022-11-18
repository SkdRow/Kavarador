package kavarador;


import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author lasts
 */
public class KFunction implements KCallable {
    private Declaracao.Function declaracao;
    
    public KFunction(Declaracao.Function declaracao) {
        this.declaracao = declaracao;
    }

    @Override
    public int arity() {
        return this.declaracao.parameters.size();
    }

    @Override
    public Object chamar(Compilador compilador, List<Object> argumentos) {
        Ambiente ambiente = new Ambiente(compilador.globals);
        
        for (int i = 0; i < declaracao.parameters.size(); i++) {
            ambiente.definir(
                    declaracao.parameters.get(i).getLexeme(),
                    argumentos.get(i));
        }
        
        try {
            compilador.executarBloco(declaracao.corpo, ambiente);
        } catch (Return valor) {
            return valor.value;
        }
        
        return null;
    }
    
    @Override
    public String toString() {
        return "function " + declaracao.name.getLexeme();
    }
}
