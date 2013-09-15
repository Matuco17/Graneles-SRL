/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.salario.ReciboManual;
import com.orco.graneles.model.AbstractFacade;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author orco
 */
@Stateless
public class ReciboManualFacade extends AbstractFacade<ReciboManual> {
    
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    
    @EJB
    private SueldoFacade sueldoF;
    

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public ReciboManualFacade(){
        super(ReciboManual.class);
    }
    
}
