/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.facturacion;

import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.facturacion.Factura;
import com.orco.graneles.domain.facturacion.LineaFactura;
import com.orco.graneles.domain.facturacion.TurnoFacturado;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.carga.CargaTurnoFacade;
import javax.ejb.EJB;
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
    
    protected EntityManager getEntityManager() {
        return em;
    }

    public FacturaFacade() {
        super(Factura.class);
    }

    @Override
    public void create(Factura entity) {
        super.create(entity);
        actualizarTurnos(entity);
        
    }

    @Override
    public void edit(Factura entity) {
        super.edit(entity);
        actualizarTurnos(entity);
    }

    private void actualizarTurnos(Factura entity) {
        for (TurnoFacturado tf : entity.getTurnosFacturadosCollection()){
            CargaTurno ct = tf.getCargaTurno();
            ct.setTurnoFacturado(tf);
            cargaTurnoF.edit(ct);
        }
    }
    
    
    
}
