/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.facturacion;

import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.facturacion.Factura;
import com.orco.graneles.domain.facturacion.LineaFactura;
import com.orco.graneles.domain.facturacion.Tarifa;
import com.orco.graneles.domain.miscelaneos.TipoLineaFactura;
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
    public List<LineaFactura> crearLineas(List<CargaTurno> cargaTurnos, Factura factura){
        List<LineaFactura> lineas = new ArrayList<LineaFactura>();
        
        for (CargaTurno ct : cargaTurnos){
            LineaFactura lf = new LineaFactura();
            lf.setFactura(factura);
            lf.setCargaTurno(ct);
            lf.setValor(BigDecimal.ZERO);
            
            lf.setTotalBruto(ct.getTurnoEmbarque().getTotalBruto());
            lf.setCosto(calcularCosto(ct));
            lf.setTarifa(calcularTarifa(ct));
            
            lf.setPorcentajeAdministracion(BigDecimal.ZERO);
            lf.setAdministracion(calcularAdministracion(ct, lf.getPorcentajeAdministracion()));
            
            lineas.add(lf);
        }
        
        factura.setLineaFacturaCollection(lineas);
        return lineas;
    }
    
    public void actualizarLineas(List<LineaFactura> lineas){
        for(LineaFactura lf : lineas){
            if (lf.getTipoLinea() != null){
                switch (lf.getTipoLinea().getId()){
                    case TipoLineaFactura.ADMINISTRACION :
                        lf.setAdministracion(calcularAdministracion(lf.getCargaTurno(), lf.getPorcentajeAdministracion()));
                        lf.setValor(lf.getAdministracion());
                        break;
                    case TipoLineaFactura.TARIFA :
                        lf.setValor(lf.getTarifa());
                        break;
                }
            }
        }
    }
    
    public BigDecimal calcularCosto(CargaTurno ct){
        //TODO: Modificar este metodo para que tome el valor de la configuracion
        BigDecimal multiplicador = new BigDecimal(1.9);
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
    
    public BigDecimal calcularAdministracion(CargaTurno ct, BigDecimal porcentaje){
        return ct.getTurnoEmbarque().getTotalBruto()
                .add(ct.getTurnoEmbarque().getTotalBruto()
                    .multiply(porcentaje)
                    .divide(new BigDecimal(100)
               ));
    }
    
    
    
}
