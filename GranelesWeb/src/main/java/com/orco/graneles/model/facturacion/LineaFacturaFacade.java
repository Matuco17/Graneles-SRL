/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.facturacion;

import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.carga.CargaTurnoCargas;
import com.orco.graneles.domain.carga.Mercaderia;
import com.orco.graneles.domain.facturacion.Factura;
import com.orco.graneles.domain.facturacion.LineaFactura;
import com.orco.graneles.domain.facturacion.Tarifa;
import com.orco.graneles.domain.facturacion.TurnoFacturado;
import com.orco.graneles.domain.miscelaneos.ConceptoFacturado;
import com.orco.graneles.domain.miscelaneos.FixedList;
import com.orco.graneles.domain.miscelaneos.GrupoFacturacion;
import com.orco.graneles.domain.miscelaneos.TipoTurnoFactura;
import com.orco.graneles.domain.salario.TipoJornal;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.carga.CargaTurnoFacade;
import com.orco.graneles.model.carga.MercaderiaFacade;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import com.orco.graneles.model.salario.TipoJornalFacade;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private TarifaFacade tarifaF;
    @EJB
    private FixedListFacade fixedListF;
    @EJB
    private MercaderiaFacade mercaderiaF;
    @EJB
    private TipoJornalFacade tipoJornalF;
    
    protected EntityManager getEntityManager() {
        return em;
    }

    public LineaFacturaFacade() {
        super(LineaFactura.class);
    }
 
    
     /**
     * Metodo que crea la lista de lineas de facturas de acuerdo a los turnos facturados,
     * crea lineas para cada mercaderia con diferente tasa y luego
     * @param turnosFacturados
     * @return 
     */
    public List<LineaFactura> crearLineasTarifa(Factura factura){
        List<LineaFactura> lineas = new ArrayList<LineaFactura>();
        
        //Genero las lineas de acuerdo a la Mercaderia y el Tipo de Jornal
        for (Mercaderia m : mercaderiaF.findAll()){
            for (TipoJornal tj : tipoJornalF.findAll()){
                BigDecimal totalTarifa = BigDecimal.ZERO;
                BigDecimal totalCargado = BigDecimal.ZERO;
                
                Tarifa tarifaActiva = null;
                
                for (TurnoFacturado tf : factura.getTurnosFacturadosCollection()){
                    if (tf.getTipoTurnoFacturado() != null
                        && (tf.getTipoTurnoFacturado().getId().equals(TipoTurnoFactura.TARIFA)
                            || tf.getTipoTurnoFacturado().getId().equals(TipoTurnoFactura.MIXTO))
                        && tf.getCargaTurno().getTurnoEmbarque().getTipo().equals(tj)) {
                        
                        for (CargaTurnoCargas ctc : tf.getCargaTurno().getCargasCollection()){
                            if (ctc.getMercaderiaBodega().equals(m) && ctc.getCarga().compareTo(BigDecimal.ZERO) > 0){
                                if (tarifaActiva == null){
                                    tarifaActiva = tarifaF.obtenerTarifaActiva(tf.getCargaTurno().getTurnoEmbarque().getTipo(), ctc.getMercaderiaBodega().getGrupoFacturacion(), tf.getCargaTurno().getTurnoEmbarque().getFecha());
                                }
                                
                                if (tarifaActiva != null){
                                    totalTarifa = totalTarifa.add(tarifaActiva.getValor().multiply(ctc.getCarga()));
                                }
                                
                                totalCargado = totalCargado.add(ctc.getCarga());
                            }
                        }
                    }
                }
                                
                if (totalCargado.compareTo(BigDecimal.ZERO) > 0){
                    LineaFactura lf = new LineaFactura();
                    lf.setDescripcion("Carga de " + m.getDescripcion() + " x " + totalCargado.toBigInteger().toString() + " Tons.");
                    if (tarifaActiva != null) {
                        lf.setPrecioUnitario(tarifaActiva.getValor());
                    }
                    lf.setImporte(totalTarifa);
                    lf.setFactura(factura);
                    lf.setTipoLinea(null);
                    lineas.add(lf);
                }                
            }
        }
        
        return lineas;
    }
     
    /**
     * Crea la linea de adminstracion para la factura, esta tiene en cuenta tanto a la adminstracion como mixto tambien
     * @param factura
     * @return 
     */
    public LineaFactura crearLineaAdministracion(Factura factura) {
        LineaFactura lf = null;
        BigDecimal totalAdminstracion = BigDecimal.ZERO;
        for (TurnoFacturado tf : factura.getTurnosFacturadosCollection()){
            if (tf.getTipoTurnoFacturado().getId().equals(TipoTurnoFactura.ADMINISTRACION)) {
                totalAdminstracion = totalAdminstracion.add(tf.getValor());
            } else if (tf.getTipoTurnoFacturado().getId().equals(TipoTurnoFactura.MIXTO)) {
                totalAdminstracion = totalAdminstracion.add(tf.getValor().subtract(tf.getTarifa()));
            }
        }
        if (totalAdminstracion.compareTo(BigDecimal.ZERO) > 0){
            lf = new LineaFactura();
            lf.setDescripcion("Detalle de Administración...");
            lf.setPrecioUnitario(null);
            lf.setImporte(totalAdminstracion);
            lf.setFactura(factura);
            lf.setTipoLinea(null);
        }
        
        return lf;
    }
   
    
}
