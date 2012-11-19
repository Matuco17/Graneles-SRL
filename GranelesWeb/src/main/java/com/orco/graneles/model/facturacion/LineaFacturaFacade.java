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
    TarifaFacade tarifaF;
    @EJB
    FixedListFacade fixedListF;
    @EJB
    MercaderiaFacade mercaderiaF;
    @EJB
    TipoJornalFacade tipoJornalF;
    
    
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
    public List<LineaFactura> crearLineas(List<TurnoFacturado> turnosFacturados){
        List<LineaFactura> lineas = new ArrayList<LineaFactura>();
        
        Map<Integer, FixedList> gruposFacturacion = fixedListF.findByListaMap(GrupoFacturacion.ID_LISTA);
        
        //Genero las lineas de acuerdo a la Mercaderia y el Tipo de Jornal
        for (Mercaderia m : mercaderiaF.findAll()){
            for (TipoJornal tj : tipoJornalF.findAll()){
                BigDecimal totalTarifa = BigDecimal.ZERO;
                BigDecimal totalCargado = BigDecimal.ZERO;
                
                for (TurnoFacturado tf : turnosFacturados){
                    if (tf.getTipoTurnoFacturado().getId().equals(TipoTurnoFactura.TARIFA)
                        && tf.getCargaTurno().getTurnoEmbarque().getTipo().equals(tj)) {
                        
                        for (CargaTurnoCargas ctc : tf.getCargaTurno().getCargasCollection()){
                            if (ctc.getMercaderiaBodega().equals(m)){
                                //TODO: CREAR EN TARIFA EL CALCULO CORRESPONDIENTE
                            }
                        }
                    }
                }
                
                
                if (totalCargado.compareTo(BigDecimal.ZERO) > 0){
                    //TODO: CREAR LA LINEA
                }                
            }
        }
        
        
        return lineas;
    }
    
    
    
}
