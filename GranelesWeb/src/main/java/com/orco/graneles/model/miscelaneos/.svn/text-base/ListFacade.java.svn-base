/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.miscelaneos;

import com.orco.graneles.domain.miscelaneos.List;
import com.orco.graneles.model.AbstractFacade;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author orco
 */
@Stateless
public class ListFacade extends AbstractFacade<List> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public ListFacade() {
        super(List.class);
    }
    
}
