/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.model;

/**
 *
 * @author orco
 */
public class NegocioException extends RuntimeException {
    
    private String mensaje;

    public String getMensaje() {
        return mensaje;
    }

    public NegocioException(String message) {
        super(message);
        mensaje = message;
    }
}
