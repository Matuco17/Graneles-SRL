/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.seguridad;

import com.orco.graneles.domain.seguridad.Usuario;
import com.orco.graneles.model.AbstractFacade;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author orco
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {
    
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }
    
    
    /**
     * Método que obtienen el usuario con el correspondiente Username pero que además que no esté bloqueado
     * @param username
     * @return usuario no bloqueado, sino devuelve Null
     */
    public Usuario getUsuarioValido(String username){
        Usuario us = find(username);
        return us;        
    }

    @Override
    public void create(Usuario entity) {
        //Seteo algunos valores por defecto
        
        super.create(entity);
    }
    

    
    
    
}
