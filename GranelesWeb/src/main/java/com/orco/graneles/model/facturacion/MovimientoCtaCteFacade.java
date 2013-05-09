/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.facturacion;

import com.orco.graneles.domain.facturacion.Empresa;
import com.orco.graneles.domain.facturacion.MovimientoCtaCte;
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
public class MovimientoCtaCteFacade extends AbstractFacade<MovimientoCtaCte> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public MovimientoCtaCteFacade() {
        super(MovimientoCtaCte.class);
    }
    
    public List<MovimientoCtaCte> findByEmpresa(Empresa empresa){
        return getEntityManager().createNamedQuery("MovimientoCtaCte.findByEmpresa", MovimientoCtaCte.class)
                .setParameter("empresa", empresa)
                .getResultList();
    }
    
}
