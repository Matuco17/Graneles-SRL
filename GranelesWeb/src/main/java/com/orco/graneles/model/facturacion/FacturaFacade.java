/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.facturacion;

import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.carga.Embarque;
import com.orco.graneles.domain.carga.TurnoEmbarque;
import com.orco.graneles.domain.facturacion.Factura;
import com.orco.graneles.domain.facturacion.MovimientoCtaCte;
import com.orco.graneles.domain.facturacion.TurnoFacturado;
import com.orco.graneles.domain.miscelaneos.TipoMovimientoCtaCte;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.carga.CargaTurnoFacade;
import com.orco.graneles.model.carga.EmbarqueFacade;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import org.joda.time.DateTime;
/**
 *
 * @author orco
 */
@Stateless
public class FacturaFacade extends AbstractFacade<Factura> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @EJB
    private CargaTurnoFacade cargaTurnoF; 
    @EJB
    private EmbarqueFacade embarqueF;
    @EJB
    private FixedListFacade fixedListF;
    
    protected EntityManager getEntityManager() {
        return em;
    }

    public FacturaFacade() {
        super(Factura.class);
    }

    @Override
    public void create(Factura entity) {
        //Creo el movimiento en cta cte para la factura
        MovimientoCtaCte movCtaCteFact = new MovimientoCtaCte();
        movCtaCteFact.setEmpresa(entity.getExportador());
        movCtaCteFact.setFactura(entity);
        movCtaCteFact.setFecha(entity.getFecha());
        movCtaCteFact.setObservaciones("Factura: " + entity.getComprobante());
        movCtaCteFact.setTipoMovimiento(fixedListF.find(TipoMovimientoCtaCte.FACTURA));
        movCtaCteFact.setValor(entity.getTotalConIVA());
        movCtaCteFact.setManual(Boolean.FALSE);
        
        entity.setMovimientoCtaCtesCollection(new ArrayList<MovimientoCtaCte>());
        entity.getMovimientoCtaCtesCollection().add(movCtaCteFact);
        
        super.create(entity);
        actualizarTurnos(entity);
        actualizarEmbarque(entity);
    }

    @Override
    public void edit(Factura entity) {
        super.edit(entity);
        actualizarTurnos(entity);
        actualizarEmbarque(entity);
    }

    private void actualizarTurnos(Factura entity) {
        for (TurnoFacturado tf : entity.getTurnosFacturadosCollection()){
            CargaTurno ct = tf.getCargaTurno();
            ct.setTurnoFacturado(tf);
            cargaTurnoF.edit(ct);
        }
    }

    private void actualizarEmbarque(Factura f){
        Embarque e = f.getEmbarque();
        boolean todoFacturado = true;
        for (TurnoEmbarque te : e.getTurnoEmbarqueCollection()){
            for (CargaTurno ct : te.getCargaTurnoCollection()){
                if (ct.getTurnoFacturado() == null){
                    boolean facturadoAhora = false;
                    for (TurnoFacturado tf : f.getTurnosFacturadosCollection()){
                        if (tf.getCargaTurno().getId() == ct.getId()){
                            facturadoAhora = true;
                        }
                    }
                    
                    if (!facturadoAhora){
                        return;
                    }
                }
            }
        }
        
        if (todoFacturado){
            e.setFacturado(true);
            embarqueF.edit(e);
        }
    }
    
    @Override
    public void remove(Factura entity) {
        //Actualizo las carga turnos para que se vacia
        for (TurnoFacturado tf : entity.getTurnosFacturadosCollection()){
            CargaTurno ct = tf.getCargaTurno();
            ct.setTurnoFacturado(null);
            cargaTurnoF.edit(ct);
        }
        //actualizo el flag de embarque completamente facturado
        Embarque e = entity.getEmbarque();
        e.setFacturado(false);
        embarqueF.edit(e);
        
        super.remove(entity);
    }
    
    public List<Factura> findByPagada(Boolean pagada) {
        List<Factura> result = getEntityManager().createNamedQuery("Factura.findByPagada", Factura.class)
                .setParameter("pagada", pagada)
                .getResultList();
        
        Collections.sort(result);
        return result;
    }
    
    /**
     * Obtiene todas las facturas de un periodo ordenadas por fecha y nro de comprobante
     * @param mes
     * @param anio
     * @return 
     */
    public List<Factura> getFacturasPeriodo(int mes, int anio) {
        DateTime fechaBase = new DateTime(anio, mes, 1, 0, 0);
        List<Factura> facturas = this.getEntityManager().createNamedQuery("Factura.findByMesAnio", Factura.class)
                .setParameter("desde", fechaBase.toDate())
                .setParameter("hasta", fechaBase.plusMonths(1).minusDays(1).toDate())
                .getResultList();
        
        Collections.sort(facturas);
        return facturas;
    }
    
    
}
