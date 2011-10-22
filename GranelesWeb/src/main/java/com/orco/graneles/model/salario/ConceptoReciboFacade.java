/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.salario.ConceptoRecibo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
/**
 *
 * @author orco
 */
@Stateless
public class ConceptoReciboFacade extends AbstractFacade<ConceptoRecibo> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public ConceptoReciboFacade() {
        super(ConceptoRecibo.class);
    }
    
}
