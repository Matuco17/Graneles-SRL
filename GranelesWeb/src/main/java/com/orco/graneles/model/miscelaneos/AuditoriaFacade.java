/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.miscelaneos;

import com.orco.graneles.domain.EntidadAuditable;
import com.orco.graneles.domain.miscelaneos.Auditoria;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
/**
 *
 * @author orco
 */
@Stateless
public class AuditoriaFacade extends AbstractFacade<Auditoria> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public AuditoriaFacade() {
        super(Auditoria.class);
    }

    
    
    
}
