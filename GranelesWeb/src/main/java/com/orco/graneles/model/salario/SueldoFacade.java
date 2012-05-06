/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.miscelaneos.*;
import com.orco.graneles.domain.personal.Accidentado;
import com.orco.graneles.domain.personal.JornalCaido;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.Moneda;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import com.orco.graneles.vo.CargaRegVO;
import com.orco.graneles.vo.TurnoEmbarqueExcelVO;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
/**
 *
 * @author orco
 */
@Stateless
public class SueldoFacade extends AbstractFacade<Sueldo> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    ConceptoRecibo conceptoReciboSACCache;
    ConceptoRecibo conceptoReciboVacacionesCache;
    ConceptoRecibo conceptoReciboAccidentadoCache;
    ConceptoRecibo conceptoReciboAdelantoAccidentadoCache;
    ConceptoRecibo conceptoReciboAdelantoAguinaldoCache;
    ConceptoRecibo conceptoReciboDtoJudicialCache;
    
    
    @EJB
    private ItemsSueldoFacade itemSueldoF;
    @EJB
    private ConceptoReciboFacade conceptoReciboF;
    @EJB
    private AdelantoFacade adelantoF;
    @EJB
    private FixedListFacade fixedListF;

    
    protected void calcularValorYCrearItemConcepto(ConceptoRecibo cDeductivo, double totalBruto, Personal personal, Sueldo sueldoTTE) {
        double totalConcepto = conceptoReciboF.calcularValorConcepto(cDeductivo, totalBruto, personal);

        BigDecimal cantidadConcepto = conceptoReciboF.calcularCantidadConcepto(cDeductivo, personal);
        
        //Una vez que tengo el valor de esta hora, lo agrego
        if (totalConcepto > 0){
            itemSueldoF.crearItemSueldo(cDeductivo, cantidadConcepto, new BigDecimal(totalConcepto), sueldoTTE);
        }
    }
        
    protected void calcularDeducciones(Map<Integer, List<ConceptoRecibo>> conceptos, double totalBruto, Personal personal, Sueldo sueldoTTE) {
        //Por cada tipo de Concepto Deductivo
        if (conceptos.get(TipoConceptoRecibo.DEDUCTIVO) != null){
            for (ConceptoRecibo cDeductivo : conceptos.get(TipoConceptoRecibo.DEDUCTIVO)){
                calcularValorYCrearItemConcepto(cDeductivo, totalBruto, personal, sueldoTTE);
            }
        }
    }

    protected void calcularNoRemunerativos(Map<Integer, List<ConceptoRecibo>> conceptos, double totalBruto, Personal personal, Sueldo sueldoTTE) {
        //Por cada tipo de Concepto No Remunerativo
        if (conceptos.get(TipoConceptoRecibo.NO_REMUNERATIVO) != null) {
            for (ConceptoRecibo cNoRemunerativo : conceptos.get(TipoConceptoRecibo.NO_REMUNERATIVO)){
                calcularValorYCrearItemConcepto(cNoRemunerativo, totalBruto, personal, sueldoTTE);
            }
        }
    }

    /**
     * Crea un sueldo completo a traves de un Item, esto despúes esta preparado para un merge
     * @param conceptoRecibo
     * @param valorCantidad 
     * @param valorItemBruto
     * @param periodo
     * @param conceptos
     * @param personal
     * @return 
     */
    protected Sueldo crearSueldoXItemBruto(ConceptoRecibo conceptoRecibo, BigDecimal valorCantidad, BigDecimal valorItemBruto, Periodo periodo, Map<Integer, List<ConceptoRecibo>> conceptos, Personal personal) {
        Sueldo sueldo = new Sueldo();
        sueldo.setPeriodo(periodo);
        sueldo.setPersonal(personal);
        sueldo.setItemsSueldoCollection(new ArrayList<ItemsSueldo>());
        
        if (valorItemBruto.doubleValue() > 0){
            itemSueldoF.crearItemSueldo(conceptoRecibo, valorCantidad, valorItemBruto, sueldo);
            calcularDeducciones(conceptos, valorItemBruto.doubleValue(), personal, sueldo);
            calcularNoRemunerativos(conceptos, valorItemBruto.doubleValue(), personal, sueldo);
        }
        
        return sueldo;
    }


    protected EntityManager getEntityManager() {
        return em;
    }

    public SueldoFacade() {
        super(Sueldo.class);
    }
    
    /**
     * Calcula el sueldo sacado desde el turno realizado por el trabajador
     * @param periodo
     * @param tte
     * @param conceptos
     * @return 
     */
    public Sueldo calcularSueldoTTE(Periodo periodo, TrabajadoresTurnoEmbarque tte, Map<Integer, List<ConceptoRecibo>> conceptos) {
        //Concepto remunerativo unico dependiente de la cantidad de horas
        if (tte.getHoras() > 0){
            double totalConcepto = conceptoReciboF.calcularDiaBrutoTTE(tte, true);
            //Agrego el valor del total del concepto al valor del total del bruto
            
            Sueldo s = crearSueldoXItemBruto(tte.getPlanilla().getTipo().getConceptoRecibo(),
                    new BigDecimal(tte.getHoras()), 
                    new Moneda(totalConcepto),
                    periodo, conceptos, tte.getPersonal());
            
            //Agrego ademas el descuento judicial
            agregarDescuentoJudicial(tte, totalConcepto, s);
                    
            return s;
        }
        return null;
    }
    
    /**
     * Calcula el sueldo de un accidentado para el periodo en cuestion
     * @param periodo
     * @param accidentado
     * @param conceptos
     * @return 
     */
    public Sueldo calcularSueldoAccidentado(Periodo periodo, Accidentado accidentado, Map<Integer, List<ConceptoRecibo>> conceptos){
        
        if (conceptoReciboAccidentadoCache == null){
            conceptoReciboAccidentadoCache = conceptoReciboF.obtenerConcepto(
                                                                fixedListF.find(TipoRecibo.HORAS),
                                                                fixedListF.find(TipoValorConcepto.PAGOS_ACCIDENTADO_ART));
        }
        if (conceptoReciboAdelantoAccidentadoCache == null){
            conceptoReciboAdelantoAccidentadoCache = conceptoReciboF.obtenerConcepto(
                                                                fixedListF.find(TipoRecibo.HORAS),
                                                                fixedListF.find(TipoValorConcepto.ADELANTO_ACCIDENTADO));
        }
        
        
        int diasTrabajados = conceptoReciboF.calculoDiasAccidentado(periodo.getDesde(), periodo.getHasta(), accidentado);
        
        double brutoCalculado = conceptoReciboF.calculoSueldoAccidentado(accidentado, periodo.getDesde(), periodo.getHasta());
        
        
        Sueldo sueldoAcc = crearSueldoXItemBruto(conceptoReciboAccidentadoCache, 
                        new BigDecimal(diasTrabajados),
                        new Moneda(brutoCalculado),
                        periodo, conceptos, accidentado.getPersonal());
        
        //Agrego los adelantos del periodo
        if (accidentado.getJornalesCaidosCollection() != null && accidentado.getJornalesCaidosCollection().size() > 0){
            BigDecimal totalAdelanto = BigDecimal.ZERO;
            for (JornalCaido jc : accidentado.getJornalesCaidosCollection()){
                if (jc.getDesde().before(periodo.getHasta()) || jc.getHasta().after(periodo.getDesde())){ //Tiene al menos 1 día en el periodo
                    //De acuerdo a la fecha del jc, si este no esta dentro de los limites tomo al periodo como limite
                    if (jc.getDesde().after(periodo.getDesde()) && jc.getHasta().before(periodo.getHasta())){
                        totalAdelanto = totalAdelanto.add(calcularSueldoAccidentado(jc.getDesde(), jc.getHasta(), accidentado, conceptos));
                    } else if (jc.getDesde().after(periodo.getDesde())){
                        totalAdelanto = totalAdelanto.add(calcularSueldoAccidentado(jc.getDesde(), periodo.getHasta(), accidentado, conceptos));
                    } else if (jc.getHasta().before(periodo.getHasta())){
                        totalAdelanto = totalAdelanto.add(calcularSueldoAccidentado(periodo.getDesde(), jc.getHasta(), accidentado, conceptos));
                    } else { //entra en el periodo
                        totalAdelanto = totalAdelanto.add(calcularSueldoAccidentado(periodo.getDesde(), periodo.getHasta(), accidentado, conceptos));
                    }  
                }
            }
 
            if (!totalAdelanto.equals(BigDecimal.ZERO)){
                //CREO EL ITEM DE SUELDO DE ACUEREDO AL ADELANTO_AGUINALDO 
                itemSueldoF.crearItemSueldo(conceptoReciboAdelantoAccidentadoCache, null, totalAdelanto, sueldoAcc);
            }            
        }
        
        
        return sueldoAcc;
    }
    
    public void agregarAdelanto(Sueldo s){
        
        if (conceptoReciboAdelantoAguinaldoCache == null || !conceptoReciboAdelantoAguinaldoCache.getTipoRecibo().equals(s.getPersonal().getTipoRecibo())){
            conceptoReciboAdelantoAguinaldoCache = conceptoReciboF.obtenerConcepto(
                                                                        s.getPersonal().getTipoRecibo(), 
                                                                        fixedListF.find(TipoValorConcepto.ADELANTO_AGUINALDO));
        }
 
        //Debo buscar los adelantos cedidos en el periodo, y los agrego
        List<Adelanto> adelantos = adelantoF.obtenerAdelantosSueldo(s);
        
        if (adelantos.size() > 0){
            BigDecimal totalAdelantos = BigDecimal.ZERO;
            for (Adelanto a : adelantoF.obtenerAdelantosSueldo(s)){
                totalAdelantos = totalAdelantos.add(a.getValor());
            }
            
            //Agrego el item al sueldo;
            itemSueldoF.crearItemSueldo(conceptoReciboAdelantoAguinaldoCache, null, totalAdelantos, s);
        }
        
    }
    
    
    /**
     * Calcula el Valor neto del sueldo del accidetnado en cuestion
     */
    public BigDecimal calcularSueldoAccidentado(Date desde, Date hasta, Accidentado accidentado, Map<Integer, List<ConceptoRecibo>> conceptos){
        
        if (conceptoReciboAccidentadoCache == null){
            conceptoReciboAccidentadoCache = conceptoReciboF.obtenerConcepto(
                                                                fixedListF.find(TipoRecibo.HORAS),
                                                                fixedListF.find(TipoValorConcepto.HORAS_HABILES));
        }
        
        
        int diasTrabajados = conceptoReciboF.calculoDiasAccidentado(desde, hasta, accidentado);
        
        double brutoCalculado = conceptoReciboF.calculoSueldoAccidentado(accidentado, desde, hasta);
        
        
        Sueldo sueldoCalculado = crearSueldoXItemBruto(conceptoReciboAccidentadoCache, 
                        new BigDecimal(diasTrabajados * 6),
                        new Moneda(brutoCalculado),
                        null, conceptos, accidentado.getPersonal());
        
        return sueldoCalculado.getTotalSueldoNeto(false);
    }
    
    
    public Sueldo sueldoSAC(Periodo periodo, Date desde, Date hasta, Personal personal, Map<Integer, List<ConceptoRecibo>> conceptos){
        
        if (conceptoReciboSACCache == null 
            || !conceptoReciboSACCache.getTipoRecibo().getId().equals(personal.getTipoRecibo().getId())){
            
        conceptoReciboSACCache = conceptoReciboF.obtenerConcepto(
                                                                personal.getTipoRecibo(),
                                                                fixedListF.find(TipoValorConcepto.SAC));
        }
        
       
        double calculadoSAC = conceptoReciboF.calcularValorSAC(personal, desde, hasta, conceptoReciboSACCache);
        
        return crearSueldoXItemBruto(conceptoReciboSACCache, null, 
                                new Moneda(calculadoSAC),
                                periodo, conceptos, personal);
        
    }
    
    public Sueldo sueldoVacaciones(Periodo periodo, Date desde, Date hasta, Personal personal, Map<Integer, List<ConceptoRecibo>> conceptos){
        
        if (conceptoReciboVacacionesCache == null 
            || !conceptoReciboVacacionesCache.getTipoRecibo().getId().equals(personal.getTipoRecibo().getId())){
        
            conceptoReciboVacacionesCache = conceptoReciboF.obtenerConcepto(
                                                                    personal.getTipoRecibo(),
                                                                    fixedListF.find(TipoValorConcepto.VACACIONES));
        }
        
        double calculadoVacaciones = conceptoReciboF.calcularValorVacaciones(personal, desde, hasta, conceptoReciboVacacionesCache);
        
        return crearSueldoXItemBruto(conceptoReciboVacacionesCache, null,
                    new Moneda(calculadoVacaciones),
                    periodo, conceptos, personal);
    
    }
    
    /**
     * Método que agrega el dto judicial si este es mayor a cero en el cálculo
     * @param tte
     * @param totalConcepto
     * @param s 
     */
    private void agregarDescuentoJudicial(TrabajadoresTurnoEmbarque tte, double totalConcepto, Sueldo s) {
        
        if (conceptoReciboDtoJudicialCache == null){
            conceptoReciboDtoJudicialCache = conceptoReciboF.obtenerConcepto(
                                                fixedListF.find(TipoRecibo.HORAS),
                                                fixedListF.find(TipoValorConcepto.DESCUENTO_JUDICIAL));
        }
        
        double valorDescuentoJudicial = conceptoReciboF.calcularDescuentoJudicial(tte, totalConcepto);
        
        if (valorDescuentoJudicial > 0.01){
            itemSueldoF.crearItemSueldo(conceptoReciboDtoJudicialCache, null, 
                                        new Moneda(valorDescuentoJudicial), s);
        }
    }
    
    public List<Sueldo> obtenerSueldos(Personal personal, Date desde, Date hasta){
        //TODO: HACER
        return null;
    }
    
    /**
     * Método que suma de manera correcta los items sueldo de cada uno de los sueldos
     * @param s1
     * @param s2
     * @return 
     */
    public Sueldo mergeSueldos(Sueldo s1, Sueldo s2){
        //cargo un map que va a servir para conseguir el item de acuerdo al concepto y extraerlo una vez que fue usado
        Map<Integer, ItemsSueldo> backIsS2 = new HashMap<Integer, ItemsSueldo>();
        for (ItemsSueldo isS2 : s2.getItemsSueldoCollection()){
            backIsS2.put(isS2.getConceptoRecibo().getId(), isS2);
        }
        
        //Junto cada uno de los items con el mismo concepto y saco de s2 los que uso
        for (ItemsSueldo isS1 : s1.getItemsSueldoCollection()){
            ItemsSueldo isS2 = backIsS2.get(isS1.getConceptoRecibo().getId());
            if (isS2 != null){
                //Si es remunerativo, sumo todo, sino solo sumo el valor
                if (isS1.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.REMUNERATIVO)){
                    isS1.setCantidad(isS1.getCantidad().add(isS2.getCantidad()));
                }

                isS1.setValorCalculado(isS1.getValorCalculado().add(isS2.getValorCalculado()));
                isS1.setValorIngresado(isS1.getValorIngresado().add(isS2.getValorIngresado()));

                //Saco el valor de S2
                backIsS2.remove(isS1.getConceptoRecibo().getId());
            }            
        }
        
        //Agrego a s1 todos los items de s2 que no compartian concepto
        for (ItemsSueldo isS2 : backIsS2.values()){
            isS2.setSueldo(s1);
            s1.getItemsSueldoCollection().add(isS2);
        }
        
        return s1;
    }
   
   
    /*
     * Funcionales o Propiedades Extendidas del Sueldo 
     */
    
    /**
     * Obtiene el monto sobre el tipo de valor de/los concepto buscado
     * @param s
     * @param tipoValorConcepto
     * @return 
     */
    public static BigDecimal obtenerMontoXConcepto(Sueldo s, int tipoValorConcepto){
        BigDecimal total = BigDecimal.ZERO;
        
        for (ItemsSueldo is : s.getItemsSueldoCollection()){
            if (is.getConceptoRecibo().getTipoValor().getId().intValue() == tipoValorConcepto){
                total = total.add(is.getValorCalculado());
            }
        }
        
        return total;
    }
    
    /**
     * Obtiene la cantidad sobre el tipo de valor de/los concepto buscado
     * @param s
     * @param tipoValorConcepto
     * @return 
     */
    public static BigDecimal obtenerCantidadXConcepto(Sueldo s, int tipoValorConcepto){
        BigDecimal total = BigDecimal.ZERO;
        
        for (ItemsSueldo is : s.getItemsSueldoCollection()){
            if (is.getConceptoRecibo().getTipoValor().getId().intValue() == tipoValorConcepto){
                total = total.add(is.getCantidad());
            }
        }
        
        return total;
    }


    
    
}
