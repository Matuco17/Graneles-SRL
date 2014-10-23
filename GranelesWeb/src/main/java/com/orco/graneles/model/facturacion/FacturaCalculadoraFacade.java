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
import com.orco.graneles.vo.CalculadoraTurno;
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
     * @param factura
     * @return 
     */
    public Calculadora generarCalculadoraNueva(Factura factura){
        Calculadora calculadora = new Calculadora();
        calculadora.setFactura(factura);
        
        for (Tarea t : tareaF.findAll()){
            calculadora.getFilas().add(new FilaCalculadora(t));
        }
        
        for (TurnoFacturado tf : factura.getTurnosFacturadosCollection()){
            if (tf.getTipoTurnoFacturado().getId().equals(TipoTurnoFactura.ADMINISTRACION) ||
                   tf.getTipoTurnoFacturado().getId().equals(TipoTurnoFactura.MIXTO))
            {
                CalculadoraTurno ct = new CalculadoraTurno(tf);

                Map<Integer, FacturaCalculadora> fcMap = new HashMap<Integer, FacturaCalculadora>();

                for (TrabajadoresTurnoEmbarque tte : tf.getCargaTurno().getTurnoEmbarque().getTrabajadoresTurnoEmbarqueCollection()){
                    FacturaCalculadora currentFC = fcMap.get(tte.getTarea().getId());

                    if (tte.getDelegado() || currentFC == null){
                        currentFC = new FacturaCalculadora();
                        currentFC.setCantidad(BigDecimal.ZERO);
                        currentFC.setTurnoFacturado(tf);
                        currentFC.setTarea((tte.getDelegado()) ? tareaF.find(Tarea.DELEGADO_ID) : tte.getTarea());
                        currentFC.setSalarioBasico(obtenerSalarioBasico(tte.getTarea(), tte.getPlanilla().getFecha()));
                        currentFC.setTipoJornal(tte.getPlanilla().getTipo());

                        currentFC.setValorTurno(conceptoReciboF.calculaDiaTTE(null, 
                              currentFC.getSalarioBasico(), 
                              6, 
                              true, 
                              currentFC.getTarea(), 
                              currentFC.getTipoJornal()).getValorBruto());
                    }

                    currentFC.setCantidad(currentFC.getCantidad().add(new BigDecimal(tte.getHoras().doubleValue() / 6)));

                    if (tte.getDelegado()){
                        fcMap.put(Tarea.DELEGADO_ID, currentFC);
                    } else {
                        fcMap.put(tte.getTarea().getId(), currentFC);
                    }
                }

                List<FacturaCalculadora> fcList = new ArrayList<FacturaCalculadora>(fcMap.values());
                Collections.sort(fcList);
                ct.setFcs(fcList);
                calculadora.getCalculadorasTurno().add(ct);

                for (FilaCalculadora filaC : calculadora.getFilas()){
                    FacturaCalculadora currentFC = fcMap.get(filaC.getTarea().getId());
                    if (currentFC == null){
                        currentFC = new FacturaCalculadora();
                        currentFC.setCantidad(BigDecimal.ZERO);
                        currentFC.setTurnoFacturado(tf);
                        currentFC.setTarea(filaC.getTarea());
                        currentFC.setSalarioBasico(obtenerSalarioBasico(filaC.getTarea(), tf.getCargaTurno().getTurnoEmbarque().getFecha()));
                        currentFC.setTipoJornal(tf.getCargaTurno().getTurnoEmbarque().getTipo());

                        currentFC.setValorTurno(conceptoReciboF.calculaDiaTTE(null, 
                              currentFC.getSalarioBasico(), 
                              6, 
                              true, 
                              currentFC.getTarea(), 
                              currentFC.getTipoJornal()).getValorBruto());
                    }

                    filaC.getFacturasCalculadoras().add(currentFC);
                }
            }
        }
        
        return calculadora;
    }
    
    /**
     * Metodo que aplica la calculadora a la factura (turnos facturados y facturas calculadoras
     * @param factura
     * @param calculadora 
     */
    public void aplicarCalculadora(Factura factura, Calculadora calculadora){
        //Creo un mapeo desde el turno embarque con los elementos de la calculadora para agruparlos
        Map<Long, Collection<FacturaCalculadora>> fcMap = new HashMap<Long, Collection<FacturaCalculadora>>();
        for (FilaCalculadora filaC : calculadora.getFilas()){
            for (FacturaCalculadora fc : filaC.getFacturasCalculadoras()){
                Collection<FacturaCalculadora> fcColl = fcMap.get(fc.getTurnoFacturado().getCargaTurno().getTurnoEmbarque().getId());
                if (fcColl == null){
                    fcColl = new ArrayList<FacturaCalculadora>();
                }
                if (fc.getValorTotal().doubleValue() > 0.0){
                    fcColl.add(fc);
                }
                fcMap.put(fc.getTurnoFacturado().getCargaTurno().getTurnoEmbarque().getId(), fcColl);
            }
        }
        
        for (TurnoFacturado tf : factura.getTurnosFacturadosCollection()){
            if (tf.getTipoTurnoFacturado().getId().equals(TipoTurnoFactura.ADMINISTRACION)
                            || tf.getTipoTurnoFacturado().getId().equals(TipoTurnoFactura.MIXTO))
            {
                tf.setFacturaCalculadoraCollection(fcMap.get(tf.getCargaTurno().getTurnoEmbarque().getId()));
                
                BigDecimal totalAdmin = BigDecimal.ZERO;
                if (tf.getFacturaCalculadoraCollection() != null){
                    for (FacturaCalculadora fc : tf.getFacturaCalculadoraCollection()){
                        totalAdmin = totalAdmin.add(fc.getValorTotal());
                    }
                }
                tf.setAdministracion(totalAdmin.add(totalAdmin.multiply(factura.getPorcentajeAdministracion()).divide(new BigDecimal(100.0))));
                
                if (tf.getTipoTurnoFacturado().getId().equals(TipoTurnoFactura.ADMINISTRACION)){
                    tf.setValor(tf.getAdministracion());
                } else {
                    tf.setValor(tf.getAdministracion().add(tf.getTarifa()));
                }                
            }
        }
    }
    
    /**
     * Genera la calculadora con los registros de calculadora existentes en la factura
     * @param factura
     * @return 
     */
    public Calculadora generarCalculadoraDeFactura(Factura factura){
        Calculadora calculadora = new Calculadora();
        calculadora.setFactura(factura);
        
        for (Tarea t : tareaF.findAll()){
            calculadora.getFilas().add(new FilaCalculadora(t));
        }
      
        for (TurnoFacturado tf : factura.getTurnosFacturadosCollection()){
            if (tf.getTipoTurnoFacturado().getId().equals(TipoTurnoFactura.ADMINISTRACION) ||
                   tf.getTipoTurnoFacturado().getId().equals(TipoTurnoFactura.MIXTO))
            {
                CalculadoraTurno ct = new CalculadoraTurno(tf);
                List<FacturaCalculadora> fcsList = new ArrayList<FacturaCalculadora>(tf.getFacturaCalculadoraCollection());
                Collections.sort(fcsList);
                ct.setFcs(fcsList);
                calculadora.getCalculadorasTurno().add(ct);

                for (FilaCalculadora filaC : calculadora.getFilas()){
                    FacturaCalculadora fcFila = null;
                    for (FacturaCalculadora fc : ct.getFcs()){
                        if (fc.getTarea().getId().equals(filaC.getTarea().getId())){
                            fcFila = fc;
                        }
                    }
                    if (fcFila == null){
                        fcFila = new FacturaCalculadora();
                        fcFila.setCantidad(BigDecimal.ZERO);
                        fcFila.setTurnoFacturado(tf);
                        fcFila.setTarea(filaC.getTarea());
                        fcFila.setSalarioBasico(obtenerSalarioBasico(filaC.getTarea(), tf.getCargaTurno().getTurnoEmbarque().getFecha()));
                        fcFila.setTipoJornal(tf.getCargaTurno().getTurnoEmbarque().getTipo());

                        fcFila.setValorTurno(conceptoReciboF.calculaDiaTTE(null, 
                              fcFila.getSalarioBasico(), 
                              6, 
                              true, 
                              fcFila.getTarea(), 
                              fcFila.getTipoJornal()).getValorBruto());
                    }

                    filaC.getFacturasCalculadoras().add(fcFila);
                }
            }
        }
        Collections.sort(calculadora.getCalculadorasTurno());
        
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
        /*
        for (CalculadoraTurno ct : calculadora.getCalculadorasTurno()){
            for (FacturaCalculadora fc : ct.getFcs()){
                 if (fc.getCantidad().doubleValue() > 0.0 && fc.getValorTurno().doubleValue() > 0.0){
                    fcs.add(fc);
                }
            }
        }
        */
        return fcs;
    }
    
    /**
     * Agrega las cantidades del turno facturado en la calculadora
     * @param calculadora
     * @param turno 
     */
    /*
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
                            }
                        }
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
                        }
                    }
                }
            }            
        }
        
        agregarTotales(calculadora);
        
        if (factura.getPorcentajeAdministracion() == null 
           || factura.getPorcentajeAdministracion().doubleValue() < turno.getPorcentajeAdministracion().doubleValue() ){
           factura.setPorcentajeAdministracion(turno.getPorcentajeAdministracion()); 
        }
    }*/
/*
    private void agregarTotales(Calculadora calculadora) {
        //Agrego los valores de totales
        calculadora.setTotalXtipoJornal(new ArrayList<TipoJornalVO>());
        Map<Integer, TipoJornalVO> tjVOMap = new HashMap<Integer, TipoJornalVO> ();
        
        for (TipoJornal tJornal : calculadora.getTiposJornales()){
            TipoJornalVO tipoJornalVO = new TipoJornalVO(tJornal);
            
            for (FilaCalculadora fila : calculadora.getFilas()){
                for (FacturaCalculadora fCalculadora : fila.getFacturasCalculadoras()){
                    if (fCalculadora.getTipoJornal().getId() == tJornal.getId()){
                        tipoJornalVO.setTotal(tipoJornalVO.getTotal().add(fCalculadora.getValorTotal()));
                    }
                }                    
            }
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
  */  
    private class ComparadorTipoJornal implements Comparator<TipoJornal>{

        @Override
        public int compare(TipoJornal o1, TipoJornal o2) {
            return (o1.getPorcExtraBasico().add(o1.getPorcExtraBruto())).compareTo(
                    o2.getPorcExtraBasico().add(o2.getPorcExtraBruto()));
        }
        
    }
}
