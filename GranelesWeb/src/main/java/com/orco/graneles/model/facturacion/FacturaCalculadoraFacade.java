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
import com.orco.graneles.vo.TipoJornalVO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Calculadora generarCalculadoraNueva(Factura factura){
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
                fCalculadora.setTipoJornal(tipoJornal);
                
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
        
        agregarTotales(calculadora);
        
        return calculadora;
    }
    
    /**
     * Genera la calculadora con los registros de calculadora existentes en la factura
     * @param factura
     * @return 
     */
    public Calculadora generarCalculadoraDeFactura(Factura factura){
        Calculadora calculadora = new Calculadora();
        calculadora.setFilas(new ArrayList<FilaCalculadora>());
        List<TipoJornal> tiposJornales = new ArrayList<TipoJornal>(tipoJornalF.findAll());
        Collections.sort(tiposJornales, new ComparadorTipoJornal());
        calculadora.setTiposJornales(tiposJornales);
        
        Map<String, FacturaCalculadora> fcMap = new HashMap<String, FacturaCalculadora>();
        for (FacturaCalculadora fc : factura.getFacturaCalculadoraCollection()){
            fcMap.put(fc.getTarea().getId() + "_" + fc.getTipoJornal().getId(), fc);
        }
        
        
        for (Tarea tarea : tareaF.findAll()){
            FilaCalculadora fila = new FilaCalculadora(tarea);
            fila.setFacturasCalculadoras(new ArrayList<FacturaCalculadora>());
            
            for (TipoJornal tipoJornal : tiposJornales){
                FacturaCalculadora fCalculadora = fcMap.get(tarea.getId() + "_" + tipoJornal.getId());
                if (fCalculadora == null){
                    fCalculadora = new FacturaCalculadora();
                    fCalculadora.setCantidad(BigDecimal.ZERO);
                    fCalculadora.setFactura(factura);
                    fCalculadora.setTarea(tarea);
                    fCalculadora.setSalarioBasico(obtenerSalarioBasico(tarea, factura.getFecha()));
                    fCalculadora.setTipoJornal(tipoJornal);
                }
                
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
        
        agregarTotales(calculadora);
        
        return calculadora;
    }
    
    /**
     * Devuelve los registros de factura calculadora en limpio
     * @param calculadora
     * @return 
     */
    public Collection<FacturaCalculadora> cleanCalculadora(Calculadora calculadora){
        Collection<FacturaCalculadora> fcs = new ArrayList<FacturaCalculadora>();
        
        for (FilaCalculadora filaC : calculadora.getFilas()){
            for (FacturaCalculadora fc: filaC.getFacturasCalculadoras()){
                if (fc.getCantidad().doubleValue() > 0.0 && fc.getValorTurno().doubleValue() > 0.0){
                    fcs.add(fc);
                }
            }
        }
        
        return fcs;
    }
    
    /**
     * Agrega las cantidades del turno facturado en la calculadora
     * @param calculadora
     * @param turno 
     */
    public void agregarTurno(Calculadora calculadora, TurnoFacturado turno, Factura factura){
        //Extraigo el salario basico, para ver si es mayor o no al que figura
        SalarioBasico salarioBasicoDelegado = null;
        BigDecimal valorTurnoDelegado = null;
                        
        for (TrabajadoresTurnoEmbarque tte : turno.getCargaTurno().getTurnoEmbarque().getTrabajadoresTurnoEmbarqueCollection()){
            if (tte.getDelegado()) {
                for (FilaCalculadora fila : calculadora.getFilas()){
                    if (fila.getTarea().getId() == tte.getTarea().getId()){
                        for (FacturaCalculadora fCalculadora : fila.getFacturasCalculadoras()){
                            if (fCalculadora.getTipoJornal().getId() == turno.getCargaTurno().getTurnoEmbarque().getTipo().getId()){
                                salarioBasicoDelegado = fCalculadora.getSalarioBasico();
                                valorTurnoDelegado = fCalculadora.getValorTurno();
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            for (FilaCalculadora fila : calculadora.getFilas()){
                if ((tte.getDelegado() && fila.getTarea().getId() == Tarea.DELEGADO_ID)
                   || (!tte.getDelegado() && fila.getTarea().getId() == tte.getTarea().getId()))
                {
                    for (FacturaCalculadora fCalculadora : fila.getFacturasCalculadoras()){
                        if (fCalculadora.getTipoJornal().getId() == turno.getCargaTurno().getTurnoEmbarque().getTipo().getId()){
                            fCalculadora.setCantidad(fCalculadora.getCantidad().add(new BigDecimal(tte.getHoras().doubleValue() / 6)));
                            //Elijo el salario basico mas alto para el delegado
                            if (tte.getDelegado() && (valorTurnoDelegado.doubleValue() > fCalculadora.getValorTurno().doubleValue()) ){
                                fCalculadora.setSalarioBasico(salarioBasicoDelegado);
                                fCalculadora.setValorTurno(valorTurnoDelegado);
                            }
                            break;
                        }
                    }
                    break;
                }
            }            
        }
        
        agregarTotales(calculadora);
        
        if (factura.getPorcentajeAdministracion() == null 
           || factura.getPorcentajeAdministracion().doubleValue() < turno.getPorcentajeAdministracion().doubleValue() ){
           factura.setPorcentajeAdministracion(turno.getPorcentajeAdministracion()); 
        }
    }

    private void agregarTotales(Calculadora calculadora) {
        //Agrego los valores de totales
        calculadora.setTotalXtipoJornal(new ArrayList<TipoJornalVO>());
        Map<Integer, TipoJornalVO> tjVOMap = new HashMap<Integer, TipoJornalVO> ();
        
        for (TipoJornal tJornal : calculadora.getTiposJornales()){
            BigDecimal totalTJornal = BigDecimal.ZERO;

            for (FilaCalculadora fila : calculadora.getFilas()){
                for (FacturaCalculadora fCalculadora : fila.getFacturasCalculadoras()){
                    if (fCalculadora.getTipoJornal().getId() == tJornal.getId()){
                        totalTJornal = totalTJornal.add(fCalculadora.getValorTotal());
                        break;
                    }
                }                    
            }
            TipoJornalVO tipoJornalVO = new TipoJornalVO(tJornal, totalTJornal);
            calculadora.getTotalXTipoJornal().add(tipoJornalVO);
            tjVOMap.put(tipoJornalVO.getTipoJornal().getId(), tipoJornalVO);
        }
        
        //Asigno el total por tipo de jornal a cada factura calculadora para tener referencia
        for (FilaCalculadora filaC : calculadora.getFilas()){
            for (FacturaCalculadora fc: filaC.getFacturasCalculadoras()){
                fc.setTotalTipoJornal(tjVOMap.get(fc.getTipoJornal().getId()));
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
