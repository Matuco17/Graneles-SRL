/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.facturacion;

import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.carga.CargaTurnoCargas;
import com.orco.graneles.domain.carga.Mercaderia;
import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.facturacion.Factura;
import com.orco.graneles.domain.facturacion.FacturaCalculadora;
import com.orco.graneles.domain.facturacion.LineaFactura;
import com.orco.graneles.domain.facturacion.Tarifa;
import com.orco.graneles.domain.facturacion.TurnoFacturado;
import com.orco.graneles.domain.miscelaneos.TipoTurnoFactura;
import com.orco.graneles.domain.personal.Tarea;
import com.orco.graneles.domain.salario.SalarioBasico;
import com.orco.graneles.domain.salario.TipoJornal;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.personal.TareaFacade;
import com.orco.graneles.model.salario.ConceptoReciboFacade;
import com.orco.graneles.model.salario.SalarioBasicoFacade;
import com.orco.graneles.model.salario.TipoJornalFacade;
import com.orco.graneles.vo.Calculadora;
import com.orco.graneles.vo.FilaCalculadora;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
/**
 *
 * @author orco
 */
@Stateless
public class FacturaCalculadoraFacade extends AbstractFacade<FacturaCalculadora> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @EJB
    SalarioBasicoFacade salarioBasicoF;
    @EJB
    TareaFacade tareaF;
    @EJB
    TipoJornalFacade tipoJornalF;
    @EJB
    ConceptoReciboFacade conceptoReciboF;
    
    protected EntityManager getEntityManager() {
        return em;
    }

    public FacturaCalculadoraFacade() {
        super(FacturaCalculadora.class);
    }
    
    private Collection<SalarioBasico> salariosCache;
    
    private SalarioBasico obtenerSalarioBasico(Tarea tarea, Date fecha){
        if (salariosCache == null){
            salariosCache = salarioBasicoF.findAll();
        }
        
        for (SalarioBasico sb : salariosCache){
            if (sb.getTarea().getId() == tarea.getId()){
                if ((sb.getDesde().equals(fecha) || sb.getDesde().before(fecha))
                   && (sb.getHasta() == null || sb.getHasta().equals(fecha) || sb.getHasta().after(fecha)))
                {
                    return sb;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Genera las filas de la calculadora con las cantidades en cero,
     * @param fecha
     * @return 
     */
    public Calculadora generarCalculadora(Factura factura){
        Calculadora calculadora = new Calculadora();
        calculadora.setFilas(new ArrayList<FilaCalculadora>());
        List<TipoJornal> tiposJornales = new ArrayList<TipoJornal>(tipoJornalF.findAll());
        Collections.sort(tiposJornales, new ComparadorTipoJornal());
        calculadora.setTiposJornales(tiposJornales);
        
        for (Tarea tarea : tareaF.findAll()){
            FilaCalculadora fila = new FilaCalculadora(tarea);
            fila.setFacturasCalculadoras(new ArrayList<FacturaCalculadora>());
            
            for (TipoJornal tipoJornal : tiposJornales){
                FacturaCalculadora fCalculadora = new FacturaCalculadora();
                fCalculadora.setCantidad(BigDecimal.ZERO);
                fCalculadora.setFactura(factura);
                fCalculadora.setTarea(tarea);
                fCalculadora.setSalarioBasico(obtenerSalarioBasico(tarea, factura.getFecha()));
                
                fCalculadora.setValorTurno(conceptoReciboF.calculaDiaTTE(null, 
                        fCalculadora.getSalarioBasico(), 
                        6, 
                        true, 
                        tarea, 
                        tipoJornal).getValorBruto());
                
                fila.getFacturasCalculadoras().add(fCalculadora);                
            }
            calculadora.getFilas().add(fila);
        }
        
        return calculadora;
    }
    
    /**
     * Agrega las cantidades del turno facturado en la calculadora
     * @param calculadora
     * @param turno 
     */
    public void agregarTurno(Calculadora calculadora, TurnoFacturado turno){
        for (TrabajadoresTurnoEmbarque tte : turno.getCargaTurno().getTurnoEmbarque().getTrabajadoresTurnoEmbarqueCollection()){
            for (FilaCalculadora fila : calculadora.getFilas()){
                if (fila.getTarea().getId() == tte.getTarea().getId()){
                    for (FacturaCalculadora fCalculadora : fila.getFacturasCalculadoras()){
                        if (fCalculadora.getTipoJornal().getId() == turno.getCargaTurno().getTurnoEmbarque().getTipo().getId()){
                            fCalculadora.setCantidad(fCalculadora.getCantidad().add(new BigDecimal(tte.getHoras().doubleValue() / 6)));
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }
    
    private class ComparadorTipoJornal implements Comparator<TipoJornal>{

        @Override
        public int compare(TipoJornal o1, TipoJornal o2) {
            return (o1.getPorcExtraBasico().add(o1.getPorcExtraBruto())).compareTo(
                    o2.getPorcExtraBasico().add(o2.getPorcExtraBruto()));
        }
        
    }
}
