/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.miscelaneos;

import com.orco.graneles.domain.miscelaneos.FixedList;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import java.util.List;
/**
 *
 * @author orco
 */
@Stateless
public class FixedListFacade extends AbstractFacade<FixedList> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public FixedListFacade() {
        super(FixedList.class);
    }
    
    public List<FixedList> findByLista(int idLista){
        return em.createNamedQuery("FixedList.findByIdLista", FixedList.class).setParameter("idLista", idLista).getResultList();
    }
    
}
