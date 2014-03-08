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
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
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
        List<MovimientoCtaCte> movimientos = getEntityManager().createNamedQuery("MovimientoCtaCte.findByEmpresa", MovimientoCtaCte.class)
                .setParameter("empresa", empresa)
                .getResultList();
        Collections.sort(movimientos);
        return movimientos;
    }
    
    
    public List<MovimientoCtaCte> findByEmpresaYFechaYTipoValor(Empresa empresa, Date desde, Date hasta, FixedList tipoValor){
        List<MovimientoCtaCte> movimientos = getEntityManager().createNamedQuery("MovimientoCtaCte.findByEmpresaYFechas", MovimientoCtaCte.class)
                .setParameter("empresa", empresa)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .setParameter("tipoValor", tipoValor)
                .getResultList();
        Collections.sort(movimientos);
        return movimientos;
    }
        
    public List<MovimientoCtaCte> findByEmpresaYTipoValor(Empresa empresa, FixedList tipoValor){
        List<MovimientoCtaCte> movimientos = getEntityManager().createNamedQuery("MovimientoCtaCte.findByEmpresaYValor", MovimientoCtaCte.class)
                .setParameter("empresa", empresa)
                .setParameter("tipoValor", tipoValor)
                .getResultList();
        
        Collections.sort(movimientos);
        
        BigDecimal saldo = BigDecimal.ZERO;
        for (MovimientoCtaCte movCtaCte : movimientos){
            movCtaCte.setSaldo(saldo.add(movCtaCte.getValor()));
            saldo = saldo.add(movCtaCte.getValor());
        } 
        
        return movimientos;
    }
    
    /**
     * Metodo que de vuelve los ultimos X movimientos (utilizando fecha de modificación)
     * @param cantidad
     * @return 
     */
    public List<MovimientoCtaCte> findUltimos(Integer cantidad){
        return null; //TODO: implementar
    }
    
}
