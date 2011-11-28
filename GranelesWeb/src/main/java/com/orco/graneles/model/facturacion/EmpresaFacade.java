/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.facturacion;

import com.orco.graneles.domain.facturacion.Empresa;
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
public class EmpresaFacade extends AbstractFacade<Empresa> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public EmpresaFacade() {
        super(Empresa.class);
    }
    
    public List<Empresa> findByTipoEmpresa(FixedList tipoEmpresa){
        return getEntityManager().createNamedQuery("Empresa.findByTipoEmpresa", Empresa.class)
                .setParameter("tipoEmpresa", tipoEmpresa)
                .getResultList();
    }
    
}
