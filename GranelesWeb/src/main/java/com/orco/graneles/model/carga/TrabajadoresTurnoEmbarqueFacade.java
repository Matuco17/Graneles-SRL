/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.carga;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
/**
 *
 * @author orco
 */
@Stateless
public class TrabajadoresTurnoEmbarqueFacade extends AbstractFacade<TrabajadoresTurnoEmbarque> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public TrabajadoresTurnoEmbarqueFacade() {
        super(TrabajadoresTurnoEmbarque.class);
    }
    
}
