/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.salario.ItemsSueldo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
/**
 *
 * @author orco
 */
@Stateless
public class ItemsSueldoFacade extends AbstractFacade<ItemsSueldo> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemsSueldoFacade() {
        super(ItemsSueldo.class);
    }
    
}
