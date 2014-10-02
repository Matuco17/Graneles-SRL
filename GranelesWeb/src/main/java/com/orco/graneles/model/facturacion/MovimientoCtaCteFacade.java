/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.facturacion;

import com.orco.graneles.domain.facturacion.Empresa;
import com.orco.graneles.domain.facturacion.Factura;
import com.orco.graneles.domain.facturacion.MovimientoCtaCte;
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
        
        setearSaldo(movimientos);
        
        return movimientos;
    }

    private void setearSaldo(List<MovimientoCtaCte> movimientos) {
        Collections.sort(movimientos);
        
        BigDecimal saldo = BigDecimal.ZERO;
        for (MovimientoCtaCte movCtaCte : movimientos){
            movCtaCte.setSaldo(saldo.add(movCtaCte.getValor()));
            saldo = saldo.add(movCtaCte.getValor());
        }
    }
    
    
    public List<MovimientoCtaCte> findByEmpresaYFecha(Empresa empresa, Date desde, Date hasta){
        List<MovimientoCtaCte> movimientos = getEntityManager().createNamedQuery("MovimientoCtaCte.findByEmpresaYFechas", MovimientoCtaCte.class)
                .setParameter("empresa", empresa)
                .setParameter("desde", null)
                .setParameter("hasta", hasta)
                .getResultList();
        
        setearSaldo(movimientos);
        
        
        //Filtro los movs desde a mano asi ya los tengo con saldo
        if (desde != null) {
            List<MovimientoCtaCte> movsFiltrados = new ArrayList<MovimientoCtaCte>();
            for (MovimientoCtaCte m : movimientos) {
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
    public List<MovimientoCtaCte> findUltimos(Integer cantidad){
        return null; //TODO: implementar
    }

    @Override
    public void create(MovimientoCtaCte entity) {
        //Pago las facturas involucradas en caso de completar su saldo
        //En este caso las facturas son siempre debitos asi q tengo q aplicarlo a los creditos solamente
        if (entity.getValor().compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal saldoMovimiento = entity.getValor().abs();
            List<Factura> facturas = new ArrayList<Factura>();
            Collections.sort(facturas);

            for (Factura f : facturas) {
                saldoMovimiento = saldoMovimiento.subtract(saldoPendienteFactura(f, entity));
                if (saldoMovimiento.compareTo(saldoMovimiento) >= 0) {
                    f.setPagada(Boolean.TRUE);
                    em.merge(f);
                }
            }
        }
        
        super.create(entity);
    }

    @Override
    public void edit(MovimientoCtaCte entity) {
        //En este caso para tener todo bien actualizado elimino el movimiento anterior y agrego uno nuevo con todas las referencias
        MovimientoCtaCte movimientoAnterior = em.find(MovimientoCtaCte.class, entity.getId());
        remove(movimientoAnterior);
        entity.setId(null);
        create(entity);
    }

    @Override
    public void remove(MovimientoCtaCte entity) {
        
        if (entity.getValor().compareTo(BigDecimal.ZERO) < 0) {
            for (Factura f : entity.getFacturaCollection()) {
                f.setPagada(Boolean.FALSE);
                em.merge(f);
            }
        }
        
        entity.getFacturaCollection().clear();
        em.createNativeQuery("DELETE FROM movctacte_factura WHERE movimiento=" + entity.getId()).executeUpdate();
        em.createNativeQuery("DELETE FROM mov_cta_cte WHERE id=" + entity.getId()).executeUpdate();
        em.flush();
        
        //super.remove(entity); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Saldo pendiente de la factura a cancelar.
     * @param f
     * @return 
     */
    private BigDecimal saldoPendienteFactura(Factura f, MovimientoCtaCte movimientoExcluido) {
        BigDecimal saldoFactura = f.getTotalConIVA().abs();
        List<MovimientoCtaCte> movCtasCtes = new ArrayList<MovimientoCtaCte>(f.getMovimientoCtaCtesCollection());
        Collections.sort(movCtasCtes);
        
        for(MovimientoCtaCte mCC : movCtasCtes) {
            if (movimientoExcluido.getId() == null ||
                    !movimientoExcluido.getId().equals(mCC.getId())) {
                saldoFactura = saldoFactura.subtract(saldoPendienteMovimiento(mCC, f));
            }
        }
        
        return saldoFactura;
    }
    
    /**
     * Saldo pendiente del movimiento relacionado con la factura
     * @param mCC
     * @return 
     */
    private BigDecimal saldoPendienteMovimiento(MovimientoCtaCte mCC, Factura facturaAExcluir) {
        BigDecimal saldoMovimiento = mCC.getValor().abs();
        List<Factura> facturas = new ArrayList<Factura>();
        Collections.sort(facturas);

        for (Factura f : facturas) {
            if (!facturaAExcluir.getId().equals(f.getId())) {
                saldoMovimiento = saldoMovimiento.subtract(saldoPendienteFactura(f, mCC));
            }
        }
        return saldoMovimiento;
    }
    
}
