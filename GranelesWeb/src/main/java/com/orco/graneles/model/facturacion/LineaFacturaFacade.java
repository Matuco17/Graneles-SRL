/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.facturacion;

import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.facturacion.LineaFactura;
import com.orco.graneles.domain.facturacion.Tarifa;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
/**
 *
 * @author orco
 */
@Stateless
public class LineaFacturaFacade extends AbstractFacade<LineaFactura> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @EJB
    TarifaFacade tarifaF;
    
    protected EntityManager getEntityManager() {
        return em;
    }

    public LineaFacturaFacade() {
        super(LineaFactura.class);
    }
    
    /**
     * Metodo que compelta las linea de factura para los turnos correspondientes
     * El metodo hace calculos para los sueldos de administracion, tarifa, costo, etc
     * @param cargaTurnos
     * @return 
     */
    public List<LineaFactura> crearLineas(List<CargaTurno> cargaTurnos){
        List<LineaFactura> lineas = new ArrayList<LineaFactura>();
        
        for (CargaTurno ct : cargaTurnos){
            LineaFactura lf = new LineaFactura();
            lf.setCargaTurno(ct);
            lf.setValor(BigDecimal.ZERO);
            
            lf.setTotalBruto(ct.getTurnoEmbarque().getTotalBruto());
            lf.setCosto(calcularCosto(ct));
            lf.setTarifa(calcularTarifa(ct));
            
            lf.setPorcentajeAdministracion(BigDecimal.ZERO);
            lf.setAdministracion(ct.getTurnoEmbarque().getTotalBruto());
        }
        
        
        return lineas;
    }
    
    public BigDecimal calcularCosto(CargaTurno ct){
        //TODO: Modificar este metodo para que tome el valor de la configuracion
        BigDecimal multiplicador = new BigDecimal(0.9);
        return ct.getTurnoEmbarque().getTotalBruto().multiply(multiplicador);
    }
    
    
    public BigDecimal calcularTarifa(CargaTurno ct){
        Tarifa tActiva = tarifaF.obtenerTarifaActiva(ct.getTurnoEmbarque().getTipo(), ct.getTurnoEmbarque().getFecha());
        
        if (tActiva != null){
            return tActiva.getValor().multiply(ct.getTotalCargado());
        } else {
            return BigDecimal.ZERO;
        }
    }
    
    
    
    
    
}
