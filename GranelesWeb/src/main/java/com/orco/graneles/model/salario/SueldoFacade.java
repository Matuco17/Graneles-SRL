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
import com.orco.graneles.model.personal.AccidentadoFacade;
import com.orco.graneles.vo.CargaRegVO;
import com.orco.graneles.vo.TurnoEmbarqueExcelVO;
import java.io.InputStream;
import java.math.BigDecimal;
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
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
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
    ConceptoRecibo conceptoReciboFeriadoCache;
    
    
    @EJB
    private ItemsSueldoFacade itemSueldoF;
    @EJB
    private ConceptoReciboFacade conceptoReciboF;
    @EJB
    private AdelantoFacade adelantoF;
    @EJB
    private FixedListFacade fixedListF;
    @EJB
    private PeriodoFacade periodoF;
    @EJB
    private AccidentadoFacade accidentadoF;

    
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
    public Sueldo calcularSueldoAccidentado(Periodo periodo, Accidentado accidentado, Map<Integer, List<ConceptoRecibo>> conceptos, boolean incluirSACyVac, boolean incluirAdelantos){
        
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
        
        
        //Realizo el calculo si tienen que existir SAC y Vacacioens para este periodo
        //Esto es, es fin de periodo por lo q tengo q calcular el aguinaldo de accidente 
        if (incluirSACyVac && periodoF.calcularSacIndividual(accidentado.getPersonal(), periodo, accidentado)){
            //Se tiene que tener en cuenta el semestre con respecto a los limites
            DateTime desdePeriodo = new DateTime(periodo.getDesde());
            DateTime desdeSAC;
            DateTime hastaSAC;
            
            //Evaluo el semestre para poner los limites de fechas maximos de semestre
            if (desdePeriodo.getMonthOfYear() <= DateTimeConstants.JUNE){
                desdeSAC = new DateTime(desdePeriodo.getYear(), DateTimeConstants.JANUARY, 1, 0, 0);
                hastaSAC = new DateTime(desdePeriodo.getYear(), DateTimeConstants.JUNE, 30, 23, 59);
            } else {
                desdeSAC = new DateTime(desdePeriodo.getYear(), DateTimeConstants.JULY, 1, 0, 0);
                hastaSAC = new DateTime(desdePeriodo.getYear(), DateTimeConstants.DECEMBER, 31, 23, 59);
            }
            
            //pongo los nuevos limites de acuerdo a los del accidente
            if (accidentado.getHasta() != null && accidentado.getHasta().before(hastaSAC.toDate())){
                hastaSAC = new DateTime(accidentado.getHasta());
            }
            if (accidentado.getDesde().after(desdeSAC.toDate())){
                desdeSAC = new DateTime(accidentado.getDesde());
            }
            
            Sueldo sueldoSAC = sueldoSAC(periodo, desdeSAC.toDate(), hastaSAC.toDate(), accidentado.getPersonal(), conceptos, false, true, false, false);
            Sueldo sueldoVac = sueldoVacaciones(periodo, desdeSAC.toDate(), hastaSAC.toDate(), accidentado.getPersonal(), conceptos, false, true, false, false);
            
            sueldoAcc = mergeSueldos(sueldoAcc, sueldoSAC);
            sueldoAcc = mergeSueldos(sueldoAcc, sueldoVac);
        }
        
        //Agrego los adelantos correspondientes a todo el periodo
        if (incluirAdelantos) {
            agregarAdelanto(sueldoAcc, false, true);
        }
        
        return sueldoAcc;
    }
    
    public Sueldo calcularSueldoFeriado(TrabajadoresTurnoEmbarque tte, Periodo periodo, Map<Integer, List<ConceptoRecibo>> conceptos){
        
        if (conceptoReciboFeriadoCache == null || !conceptoReciboFeriadoCache.getTipoRecibo().equals(tte.getPersonal().getTipoRecibo())){
            conceptoReciboFeriadoCache = conceptoReciboF.obtenerConcepto(
                                                tte.getPersonal().getTipoRecibo(), 
                                                fixedListF.find(TipoValorConcepto.HORAS_HABILES));
        }
        
        double brutoCalculado = conceptoReciboF.calcularDiaBrutoTTE(tte, true);
        
        return crearSueldoXItemBruto(conceptoReciboFeriadoCache, 
                        new BigDecimal(6),
                        new Moneda(brutoCalculado),
                        periodo, conceptos, tte.getPersonal());
     }
    
    public void agregarAdelanto(Sueldo s, boolean suprimirAccidentado, boolean suprimirHoras){
        
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
            
            //tomo en cuenta si piden o no las supresiones
            if (suprimirAccidentado || suprimirHoras){
        
                BigDecimal totalNetoSacYVacAccidentado = new BigDecimal(
                        conceptoReciboF.calcularNeto(s.getPersonal(), 
                            totalSacYVacAccidentado(s.getPersonal(), s.getPeriodo()).doubleValue())
                );
                
                if (suprimirAccidentado) {
                    if (totalAdelantos.compareTo(totalNetoSacYVacAccidentado) > 0) {
                        totalAdelantos = totalAdelantos.subtract(totalNetoSacYVacAccidentado);
                    } else {
                        totalAdelantos = BigDecimal.ZERO;
                    }
                } else if (suprimirHoras){
                    if (totalAdelantos.compareTo(totalNetoSacYVacAccidentado) > 0) {
                        totalAdelantos = totalNetoSacYVacAccidentado;
                    } 
                }
                
            }
            
            if (totalAdelantos.doubleValue() >= 0.01) {
                //Agrego el item al sueldo;
                itemSueldoF.crearItemSueldo(conceptoReciboAdelantoAguinaldoCache, null, totalAdelantos, s);
            }
        }
        
    }
    
    /**
     * Metodo que devuelve el total bruto de Sac Y Vacaciones para un trabajador en concepto de accidentado
     * @param personal
     * @param periodo
     * @return 
     */
    public BigDecimal totalSacYVacAccidentado(Personal personal, Periodo periodo){
        BigDecimal totalSacYVac = BigDecimal.ZERO;
        
        List<Accidentado> listaAcc = accidentadoF.getAccidentadosPeriodoYPersonal(periodo.getDesde(), periodo.getHasta(), personal);
        
        Map<Integer, List<ConceptoRecibo>> conceptosHoras = conceptoReciboF.obtenerConceptosXTipoRecibo(fixedListF.find(TipoRecibo.HORAS));
        
        for (Accidentado acc : listaAcc){
            Sueldo sueldoAcc = calcularSueldoAccidentado(periodo, acc, conceptosHoras, true, false);
            
            for (ItemsSueldo is : sueldoAcc.getItemsSueldoCollection()){
                if (is.getConceptoRecibo().getTipoValor().getId().equals(TipoValorConcepto.SAC)
                    || is.getConceptoRecibo().getTipoValor().getId().equals(TipoValorConcepto.VACACIONES))
                {
                    totalSacYVac = totalSacYVac.add(is.getValorCalculado());
                }
            }
        }
       
        return totalSacYVac;
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
    
    
    public Sueldo sueldoSAC(Periodo periodo, Date desde, Date hasta, Personal personal, Map<Integer, List<ConceptoRecibo>> conceptos, boolean incluirHoras, boolean incluirAccidente, boolean incluirFeriado, boolean incluirManual){
        
        if (conceptoReciboSACCache == null 
            || !conceptoReciboSACCache.getTipoRecibo().getId().equals(personal.getTipoRecibo().getId())){
            
        conceptoReciboSACCache = conceptoReciboF.obtenerConcepto(
                                                                personal.getTipoRecibo(),
                                                                fixedListF.find(TipoValorConcepto.SAC));
        }
        
        double calculadoSAC = conceptoReciboF.calcularValorSAC(personal, desde, hasta, conceptoReciboSACCache, incluirHoras, incluirAccidente, incluirFeriado, incluirManual);
        
        return crearSueldoXItemBruto(conceptoReciboSACCache, null, 
                                new Moneda(calculadoSAC),
                                periodo, conceptos, personal);
        
    }
    
    public Sueldo sueldoManual(ReciboManual recibo, Map<Integer, List<ConceptoRecibo>> conceptosHoras){
        Sueldo sueldoManual = new Sueldo();
        sueldoManual.setPeriodo(recibo.getPeriodo());
        sueldoManual.setPersonal(recibo.getPersonal());
        
        if (conceptosHoras == null){
            conceptosHoras = conceptoReciboF.obtenerConceptosXTipoRecibo(fixedListF.find(TipoRecibo.HORAS));
        }
        
        for (ItemsReciboManual iRM : recibo.getItemsReciboManualCollection()){
            Sueldo sueldoIRM = crearSueldoXItemBruto(iRM.getConceptoRecibo(), iRM.getCantidad(), iRM.getValor(), recibo.getPeriodo(), conceptosHoras, recibo.getPersonal());
            sueldoManual = mergeSueldos(sueldoManual, sueldoIRM);
        }
        
        return sueldoManual;
    }
    
    public Sueldo sueldoVacaciones(Periodo periodo, Date desde, Date hasta, Personal personal, Map<Integer, List<ConceptoRecibo>> conceptos, boolean incluirHoras, boolean incluirAccidente, boolean incluirFeriado, boolean incluirManual){
        
        if (conceptoReciboVacacionesCache == null 
            || !conceptoReciboVacacionesCache.getTipoRecibo().getId().equals(personal.getTipoRecibo().getId())){
        
            conceptoReciboVacacionesCache = conceptoReciboF.obtenerConcepto(
                                                                    personal.getTipoRecibo(),
                                                                    fixedListF.find(TipoValorConcepto.VACACIONES));
        }
        
        double calculadoVacaciones = conceptoReciboF.calcularValorVacaciones(personal, desde, hasta, conceptoReciboVacacionesCache, incluirHoras, incluirAccidente, incluirFeriado, incluirManual);
        
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
                if (isS1.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.REMUNERATIVO)
                        && isS1.getCantidad() != null)
                {
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


    /**
     * Devuelve todos los sueldos para los parametros especificados (no mandatorios)
     * @param persona
     * @param desde
     * @param hasta
     * @return 
     */
    public List<Sueldo> obtenerSueldos(Personal personal, Periodo desde, Periodo hasta){
        return getEntityManager().createNamedQuery("Sueldo.findByPersonalYPeriodos", Sueldo.class)
                .setParameter("personal", personal)
                .setParameter("periodoDesdeDescripcion", (desde != null ? desde.getDescripcion() : null))
                .setParameter("periodoHastaDescripcion", (hasta != null ? hasta.getDescripcion() : null))
                .getResultList();
    }
    
    
}
