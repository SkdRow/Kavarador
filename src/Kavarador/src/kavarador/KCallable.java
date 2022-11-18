/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kavarador;

import java.util.List;

/**
 *
 * @author lasts
 */
public interface KCallable {
    int arity();
    Object chamar(Compilador compilador, List<Object> argumentos);
}
