/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.facturacion;

import com.orco.graneles.domain.facturacion.Empresa;
import com.orco.graneles.domain.facturacion.MovimientoCtaCteTons;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
/**
 *
 * @author orco
 */
@Stateless
public class MovimientoCtaCteTonsFacade extends AbstractFacade<MovimientoCtaCteTons> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public MovimientoCtaCteTonsFacade() {
        super(MovimientoCtaCteTons.class);
    }
    
    public List<MovimientoCtaCteTons> findByEmpresa(Empresa empresa){
        List<MovimientoCtaCteTons> movimientos = getEntityManager().createNamedQuery("MovimientoCtaCteTons.findByEmpresa", MovimientoCtaCteTons.class)
                .setParameter("empresa", empresa)
                .getResultList();
        
        setearSaldo(movimientos);
        
        return movimientos;
    }

    private void setearSaldo(List<MovimientoCtaCteTons> movimientos) {
        Collections.sort(movimientos);
        
        BigDecimal saldo = BigDecimal.ZERO;
        for (MovimientoCtaCteTons movCtaCte : movimientos){
            movCtaCte.setSaldo(saldo.add(movCtaCte.getValor()));
            saldo = saldo.add(movCtaCte.getValor());
        }
    }
    
    
    public List<MovimientoCtaCteTons> findByEmpresaYFecha(Empresa empresa, Date desde, Date hasta){
        List<MovimientoCtaCteTons> movimientos = getEntityManager().createNamedQuery("MovimientoCtaCteTons.findByEmpresaYFechas", MovimientoCtaCteTons.class)
                .setParameter("empresa", empresa)
                .setParameter("desde", null)
                .setParameter("hasta", hasta)
                .getResultList();
        
        setearSaldo(movimientos);
        
        
        //Filtro los movs desde a mano asi ya los tengo con saldo
        if (desde != null) {
            List<MovimientoCtaCteTons> movsFiltrados = new ArrayList<MovimientoCtaCteTons>();
            for (MovimientoCtaCteTons m : movimientos) {
                if (!m.getFecha().before(desde)) {
                    movsFiltrados.add(m);
                }
            }
            return movsFiltrados;
        } else {
            return movimientos;
        }
    }
    
    /**
     * Metodo que de vuelve los ultimos X movimientos (utilizando fecha de modificación)
     * @param cantidad
     * @return 
     */
    public List<MovimientoCtaCteTons> findUltimos(Integer cantidad){
        return null; //TODO: implementar
    }
    
}
