/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.miscelaneos.*;
import com.orco.graneles.domain.personal.Accidentado;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.Moneda;
import com.orco.graneles.model.carga.TrabajadoresTurnoEmbarqueFacade;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import com.orco.graneles.model.personal.AccidentadoFacade;
import com.orco.graneles.vo.AccidentadoVO;
import com.orco.graneles.vo.SueldoAccidentadoVO;
import com.orco.graneles.vo.TrabajadorTurnoEmbarqueVO;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.management.monitor.MonitorSettingException;
import org.jfree.data.time.Month;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
/**
 *
 * @author orco
 */
@Stateless
public class ConceptoReciboFacade extends AbstractFacade<ConceptoRecibo> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    
    
    @EJB
    private SalarioBasicoFacade salarioBasicoF;
    @EJB
    private FixedListFacade fixedListF;
    @EJB
    private TrabajadoresTurnoEmbarqueFacade tteF;
    @EJB
    private AccidentadoFacade accidentadoF;
    @EJB
    private MinimoVitalMovilHoraFacade minVitalMovilF;
    
        
    //Singletos que no se tienen que cargar de nuevo, solamente con una vez me alcanzan
    private Map<Integer, FixedList> mapAdicTarea = null;

    private List<ConceptoRecibo> conceptosDeductivosHorasCache;
    
    Long personalCacheId;
    Double totalAcumuladoCache; //ultimo total acumulado, dependie del personalCache
    
    /**
     * Metodo que levanta el acumulado por las horas del trabajador en el periodo seleccionado
     * @return 
     */
    public double acumuladoBrutoTrabajadores(Personal personal, Date desde, Date hasta) {
        if (personalCacheId == personal.getId()){
            return totalAcumuladoCache;
        } else {
            double totalAcumulado = 0;
            SalarioBasico salarioActivo = null; //Variable del salario activo que me sirve de cache para no tener que hacer la busqueda por cada jornal trabajado
            List<TrabajadoresTurnoEmbarque> ttes = tteF.getTTEPeriodo(personal, desde, hasta);
            for (TrabajadoresTurnoEmbarque tte : ttes){
                //Verifico que tengo un salario basico para ese periodo
                if (salarioActivo == null 
                    || (salarioActivo.getHasta() != null && salarioActivo.getHasta().before(tte.getPlanilla().getFecha()))){
                    salarioActivo = salarioBasicoF.obtenerSalarioActivo(tte.getTarea(), tte.getCategoria(), tte.getPlanilla().getFecha());
                }

                totalAcumulado += calcularDiaBrutoTTE(salarioActivo, tte, true);
            }

            List<Accidentado> accs = accidentadoF.getAccidentadosPeriodoYPersonal(desde, hasta, personal);
            for (Accidentado acc : accs){
                totalAcumulado += calculoSueldoAccidentado(acc, desde, hasta); 
            }

            //Completo los cache para aminorar los calculos
            personalCacheId = personal.getId();
            totalAcumuladoCache = totalAcumulado;
            
            return totalAcumulado;
        }
    }

    /**
     * Calcula el sueldo del accidentado de acuerdo a su valor desde una fecha hasta la otra
     * @param accidentado
     * @param desde fecha inicio del cálculo
     * @param hasta fecha final del calculo
     * @return 
     */
    public double calculoSueldoAccidentado(Accidentado accidentado, Date desde, Date hasta){
        AccidentadoVO accVO = accidentadoF.completarAccidentado(accidentado);
        
        double acumulado = 0.0;
        
        //Por cada uno de los salarios aplicados, veo cuales tienen que ser aplicados en el periodo de tiempo
        if (accVO.getSueldos() != null){
            for (SueldoAccidentadoVO saVO : accVO.getSueldos()){
                if (saVO.getDesde().before(hasta) || saVO.getDesde().equals(hasta)){
                    if (saVO.getHasta() == null || saVO.getHasta().after(desde) || saVO.getHasta().equals(desde)){

                        //Calculo las fechas que se aplica sobre el periodo en cuestion el salario
                        Date saDesde = (saVO.getDesde().before(desde)) ? desde : saVO.getDesde();
                        Date saHasta = (saVO.getHasta() == null || saVO.getHasta().after(hasta)) ? hasta : saVO.getHasta();

                        int diasTrabajados = calculoDiasAccidentado(saDesde, saHasta, accidentado);

                        if (accidentado.getIncluirAdicionales() != null && accidentado.getIncluirAdicionales()){
                            acumulado += saVO.getBrutoConAdicionales().doubleValue() * diasTrabajados;
                        } else {
                            acumulado += saVO.getBrutoSinAdicionales().doubleValue() * diasTrabajados;
                        }
                    }
                }
            }
        }
        
        return acumulado;
    }
    
    /**
     * Calcula los dias teoricos que trabajo el accidentado sobre un periodo seleccionado
     * @param desde
     * @param hasta
     * @param accidentado
     * @return 
     */
    public int calculoDiasAccidentado(Date desde, Date hasta,  Accidentado accidentado) {
        DateTime desdeCalculado;
        DateTime hastaCalculado;        
        int diasTrabajados = 0;
       
        //Se debe calcular las fechas limites a tener en cuenta, de ahi contar todos lso dias menos el domingo
        if (accidentado.getDesde().before(desde)) {
            desdeCalculado = new DateTime(desde);
        } else {
            desdeCalculado = (new DateTime(accidentado.getDesde())).plusDays(1); //Ya que se calcula con un día despues del día de accidente
        }
        
        if (accidentado.getHasta() != null && accidentado.getHasta().before(hasta)){
            hastaCalculado = new DateTime(accidentado.getHasta()).plusDays(1);
        } else {
            hastaCalculado = new DateTime(hasta).plusDays(1);
        }
        //OBS: le sumo un dia ya que la comparacion es solamente isBefore y no tengo el equal en la fecha.
                
        //calculo los dias de acuerdo al periodo limite impuesto 
        DateTime currentFecha = desdeCalculado;
        while (currentFecha.isBefore(hastaCalculado)){
            if (currentFecha.getDayOfWeek() != DateTimeConstants.SUNDAY){
                diasTrabajados++;
            }
            currentFecha = currentFecha.plusDays(1);
        }
        
        return diasTrabajados;
    }
    
    protected double calcularDiaBrutoTTE(SalarioBasico salario, TrabajadoresTurnoEmbarque tte, boolean incluirAdicionales) {
        if (salario == null){
            return 0.0;
        } else {
            return calcularDiaCompletoTTTE(salario, tte, incluirAdicionales).getValorBruto().doubleValue();
        }
    }
    
        /**
     * Metodo que calcula el valor total del dia trabajado para el Trabajador
     * @param tte
     * @param mapAdicTarea
     * @return 
     */
    public double calcularDiaBrutoTTE(TrabajadoresTurnoEmbarque tte, boolean incluirAdicionales) {
        //Esto significa que debo realizar el calculo del total bruto con el salario basico
        //Obtengo el salario correspondiente al tte
        SalarioBasico salario = salarioBasicoF.obtenerSalarioActivo(tte.getTarea(), tte.getCategoria(), tte.getPlanilla().getFecha());
        return calcularDiaBrutoTTE(salario, tte, incluirAdicionales);
    }
    
    public TrabajadorTurnoEmbarqueVO calcularDiaTTE(TrabajadoresTurnoEmbarque tte, boolean incluirAdicionales) {
        SalarioBasico salario = salarioBasicoF.obtenerSalarioActivo(tte.getTarea(), tte.getCategoria(), tte.getPlanilla().getFecha());
        return calcularDiaCompletoTTTE(salario, tte, incluirAdicionales);
    }
     
    
    public TrabajadorTurnoEmbarqueVO calcularDiaCompletoTTTE(SalarioBasico salario, TrabajadoresTurnoEmbarque tte, boolean incluirAdicionales) {
        //Obtengo el valor del bruto ya que depende si trabajo 6 o 3 horas (y el salario está en valor de horas
        
        double basicoBruto = 0.0;
        double totalConcepto = 0.0; //resultado de la suma del concepto

        TrabajadorTurnoEmbarqueVO tteVO = new TrabajadorTurnoEmbarqueVO(tte);
        
        if (salario != null){
            
            basicoBruto = salario.getBasico().doubleValue() / 6 * tte.getHoras().doubleValue();
            totalConcepto = basicoBruto; //resultado de la suma del concepto

            //Realizo el agregado de los modificadores de tarea
            if (incluirAdicionales){
                if (tte.getTarea().getInsalubre()){
                    double conceptoInsalubre = basicoBruto * (getMapAdicTarea().get(AdicionalTarea.INSALUBRE).getValorDefecto().doubleValue() / 100);
                    conceptoInsalubre += tte.getPlanilla().getTipo().getPorcExtraBruto().doubleValue() / 100;
                    tteVO.setInsalubre(new Moneda(conceptoInsalubre));
                    totalConcepto += conceptoInsalubre ;
                }
                if (tte.getTarea().getPeligrosa()){
                    double conceptoPeligrosa = basicoBruto * (getMapAdicTarea().get(AdicionalTarea.PELIGROSA).getValorDefecto().doubleValue() / 100);
                    conceptoPeligrosa += tte.getPlanilla().getTipo().getPorcExtraBruto().doubleValue() / 100;
                    tteVO.setPeligrosa(new Moneda(conceptoPeligrosa));
                    totalConcepto += conceptoPeligrosa ;
                }
                if (tte.getTarea().getPeligrosa2()){
                    double conceptoPeligrosa2 = basicoBruto * (getMapAdicTarea().get(AdicionalTarea.PELIGROSA2).getValorDefecto().doubleValue() / 100);
                    conceptoPeligrosa2 += tte.getPlanilla().getTipo().getPorcExtraBruto().doubleValue() / 100;
                    tteVO.setPeligrosa2(new Moneda(conceptoPeligrosa2));
                    totalConcepto += conceptoPeligrosa2;
                }
                if (tte.getTarea().getProductiva()){
                    double conceptoProductiva = basicoBruto * (getMapAdicTarea().get(AdicionalTarea.PRODUCTIVA).getValorDefecto().doubleValue() / 100);
                    conceptoProductiva += tte.getPlanilla().getTipo().getPorcExtraBruto().doubleValue() / 100;
                    tteVO.setProductiva(new Moneda(conceptoProductiva));
                    totalConcepto += conceptoProductiva ;
                }
                if (tte.getTarea().getEspecidalidad() != null && (tte.getTarea().getEspecidalidad().compareTo(BigDecimal.ZERO) > 0)){
                    double conceptoEspecialidad = basicoBruto * (tte.getTarea().getEspecidalidad().doubleValue() / 100);
                    conceptoEspecialidad += tte.getPlanilla().getTipo().getPorcExtraBruto().doubleValue() / 100;
                    tteVO.setEspecialidad(new Moneda(conceptoEspecialidad));
                    totalConcepto += conceptoEspecialidad ;
                }
                
                //Ahora aplico el valor del modificador del tipo de jornal
                totalConcepto += basicoBruto * tte.getPlanilla().getTipo().getPorcExtraBasico().doubleValue() / 100;
            }
        }
        
        tteVO.setValorBruto(new Moneda(totalConcepto));
        tteVO.setJornalBasico(new Moneda(basicoBruto));
        tteVO.setDescuentoJudicial(new Moneda(calcularDescuentoJudicial(tte, totalConcepto)));
        
        Moneda neto = (new Moneda(calcularNeto(tte.getPersonal(), totalConcepto) - tteVO.getDescuentoJudicial().doubleValue()));
        tteVO.setValorTurno(neto);
        
        return tteVO;
    }
    
    /**
     * Agrega los valores salariales al actual TteVO de acuerdo al metodo calcularDiaTTE
     * @param tteVO 
     */
    public void agregarValoresSalariales(TrabajadorTurnoEmbarqueVO tteVO){
        TrabajadorTurnoEmbarqueVO tteVOconValores = calcularDiaTTE(tteVO.getTte(), true);
        
        tteVO.setJornalBasico(tteVOconValores.getJornalBasico());
        tteVO.setValorBruto(tteVOconValores.getValorBruto());
        tteVO.setValorTurno(tteVOconValores.getValorTurno());
    }
    
    
   /**
     * Metodo que calcula el descuento judicial del TTE
     * @param tte entrada de trabajador en la planilla
     * @param bruto valor bruto calculado con anterioridad 
     * @return el monto del descuento judicial
     */
    public double calcularDescuentoJudicial(TrabajadoresTurnoEmbarque tte, double bruto){
        if (tte.getPersonal().getDescuentoJudicial() != null 
                && tte.getPersonal().getDescuentoJudicial().doubleValue() >= 0.01){
            
            //Primero obtengo el minimo vital y movil correspondiente
            MinimoVitalMovilHora mvm = minVitalMovilF.obtenerMinimoVitalMovilHoraActivo(tte.getPlanilla().getFecha());
            
            if (mvm != null){
                double totalMVM = mvm.getValor().doubleValue() * tte.getHoras().doubleValue();
                        
                double netoAAplicarDesc = bruto - totalMVM;
                
                return netoAAplicarDesc * tte.getPersonal().getDescuentoJudicial().doubleValue() / 100;
            }
        }
        return 0.0;
    }
    
    protected Map<Integer, FixedList> getMapAdicTarea(){
        if (mapAdicTarea == null){
            mapAdicTarea = fixedListF.findByListaMap(AdicionalTarea.ID_LISTA);
        }
        return mapAdicTarea;
    }
    
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConceptoReciboFacade() {
        super(ConceptoRecibo.class);
    }
    
    /**
     * Map distribuido desde el tipo de concepto del recibo con las lista de los conceptos activos del tipo de recibo
     * @param tipoRecibo
     * @return 
     */
    public Map<Integer, List<ConceptoRecibo>> obtenerConceptosXTipoRecibo(FixedList tipoRecibo){
        List<ConceptoRecibo> conceptosEncontrados = getEntityManager().createNamedQuery("ConceptoRecibo.findByTipoRecibo", ConceptoRecibo.class)
                                                        .setParameter("tipoRecibo", tipoRecibo)
                                                        .setParameter("versionActiva", true)
                                                        .getResultList();
        Map<Integer, List<ConceptoRecibo>> result = new HashMap<Integer, List<ConceptoRecibo>>();
        
        for(ConceptoRecibo c : conceptosEncontrados){
            if (result.get(c.getTipo().getId()) == null)
                result.put(c.getTipo().getId(), new ArrayList<ConceptoRecibo>());
            
            result.get(c.getTipo().getId()).add(c);
        }
        
        return result;
    }
    
    /**
     * Obtiene los conceptos de tal tipo de Concepto y tal tipo de Recibo de sueldo
     * @param tipoRecibo
     * @param tipoConcepto
     * @return 
     */
    public List<ConceptoRecibo> obtenerConceptos(FixedList tipoRecibo, FixedList tipoConcepto){
        return getEntityManager().createNamedQuery("ConceptoRecibo.findByTipoReciboYTipoConcepto", ConceptoRecibo.class)
                                   .setParameter("tipoRecibo", tipoRecibo)
                                   .setParameter("tipo", tipoConcepto)
                                   .setParameter("versionActiva", true)
                                   .getResultList();
        
    }
    
    /**
     * Obtiene los conceptos de tal tipo de Concepto y tal tipo de Recibo de sueldo
     * @param tipoRecibo
     * @param tipoValor
     * @return 
     */
    public ConceptoRecibo obtenerConcepto(FixedList tipoRecibo, FixedList tipoValor){
        return getEntityManager().createNamedQuery("ConceptoRecibo.findByTipoReciboYTipoValor", ConceptoRecibo.class)
                                   .setParameter("tipoRecibo", tipoRecibo)
                                   .setParameter("tipoValor", tipoValor)
                                   .setParameter("versionActiva", true)
                                   .getResultList().get(0);
    }
    

    
    /**
     * Metodo que devuelve el valor calculado de acuerdo al concepto, puede ser un simple porcentaje o algo + complejo
     * @param concepto
     * @param totalBruto
     * @return 
     */
    public double calcularValorConcepto(ConceptoRecibo concepto, double totalBruto, Personal personal){
        
        switch (concepto.getTipoValor().getId()){
        
            case TipoValorConcepto.FIJO:
                return concepto.getValor().doubleValue();
            case TipoValorConcepto.PORCENTUAL:
                return totalBruto * concepto.getValor().doubleValue() / 100; 
            case TipoValorConcepto.JUBILACION:
                return totalBruto * concepto.getValor().doubleValue() / 100; 
            case TipoValorConcepto.OBRA_SOCIAL:
                if (personal.getObraSocial() != null){
                    return totalBruto * personal.getObraSocial().getAportes().doubleValue() / 100;
                } else {
                    return totalBruto * concepto.getValor().doubleValue() / 100;
                }
            case TipoValorConcepto.SINDICATO:
                if (personal.getSindicato()){
                    double porcSindicato = concepto.getValor().doubleValue();
                    if (personal.getCategoriaPrincipal().getSindicato() != null){
                        porcSindicato = personal.getCategoriaPrincipal().getSindicato().getPorcentaje().doubleValue();
                    }
                    return totalBruto * porcSindicato / 100;
                } else {
                    return 0;
                }
           
            default:
                return 0;
        }
    }
   
    /**
     * Calcula el valor neto sobre un bruto para el personal
     * ATENCION: por ahora solo anda para trabajadores de Horas, no para mensual.
     * @param personal
     * @param bruto
     * @return 
     */
    public double calcularNeto(Personal personal, double bruto){
   
        if (conceptosDeductivosHorasCache == null){
            conceptosDeductivosHorasCache = obtenerConceptos(
                                fixedListF.find(TipoRecibo.HORAS), 
                                fixedListF.find(TipoConceptoRecibo.DEDUCTIVO));
        }
        
        double neto = bruto;
        
        for (ConceptoRecibo cr : conceptosDeductivosHorasCache){
            neto -= calcularValorConcepto(cr, bruto, personal);
        }
        
        return neto;
    }
    
    /**
     * Metodo que devuelve la cantidad de acuerdo al concepto
     * @param concepto
     * @param totalBruto
     * @return 
     */
    public BigDecimal calcularCantidadConcepto(ConceptoRecibo concepto, Personal personal){
        
        switch (concepto.getTipoValor().getId()){
        
            case TipoValorConcepto.FIJO:
                return null;
            case TipoValorConcepto.PORCENTUAL:
                return new Moneda(concepto.getValor().doubleValue()); 
            case TipoValorConcepto.JUBILACION:
                return new Moneda(concepto.getValor().doubleValue()); 
            case TipoValorConcepto.OBRA_SOCIAL:
                return new Moneda(concepto.getValor().doubleValue()); 
            case TipoValorConcepto.SINDICATO:
                if (personal.getSindicato()){
                    double porcSindicato = concepto.getValor().doubleValue();
                    if (personal.getCategoriaPrincipal().getSindicato() != null){
                        porcSindicato = personal.getCategoriaPrincipal().getSindicato().getPorcentaje().doubleValue();
                    }
                return new Moneda(porcSindicato); 
                } else {
                    return BigDecimal.ZERO;
                }
           
            default:
                return BigDecimal.ZERO;
        }
    }
    
    /**
     * Metodo que calcula el SAC del trabajador entre esas fechas
     * OBS: Deben estar todos los sueldos persistidos sino puede calcular incorrectamente
     * @param listaSueldos lista de los sueldos en el periodo calculado
     * @param conceptoSAC concepto del recibo, si es null el metodo se encarga de proveerlo
     * @return 
     */
    public double calcularValorSAC(Personal personal, Date desde, Date hasta, ConceptoRecibo conceptoSAC){
                
        switch (personal.getTipoRecibo().getId()){
            case TipoRecibo.HORAS :
                /**
                 * Debo realizar nuevamente el calculo del total de jornales que tiene el personal
                 * desde la fecha desde y hasta
                 * de ahi levanto el concepto de sac para sacar el porcentaje y lo calculo, de ahi lo devuelvo
                */
                if (conceptoSAC == null) {
                    conceptoSAC = obtenerConcepto(fixedListF.find(TipoRecibo.HORAS), fixedListF.find(TipoValorConcepto.SAC));
                }
                
                double totalAcumulado = acumuladoBrutoTrabajadores(personal, desde, hasta);
                           
                return totalAcumulado * conceptoSAC.getValor().doubleValue() / 100;
                
                                
            case TipoRecibo.MENSUAL:
                //Busco el Mayor de los sueldos y divido x la cantidad de días
                
            default : return 0;
        }
    }
    
    
    /**
     * Metodo que calcula las Vacaciones del trabajador entre esas fechas
     * OBS: Deben estar todos los sueldos persistidos sino puede calcular incorrectamente
     * @param listaSueldos lista de los sueldos en el periodo calculado
     * @param conceptoVacaciones concepto del recibo, si es null el metodo se encarga de proveerlo
      * @return 
     */
    public double calcularValorVacaciones(Personal personal, Date desde, Date hasta, ConceptoRecibo conceptoVacaciones){
                
        switch (personal.getTipoRecibo().getId()){
            case TipoRecibo.HORAS :
                /**
                 * Debo realizar nuevamente el calculo del total de jornales que tiene el personal
                 * desde la fecha desde y hasta
                 * de ahi levanto el concepto de sac para sacar el porcentaje y lo calculo, de ahi lo devuelvo
                */
                if (conceptoVacaciones == null){
                    conceptoVacaciones = obtenerConcepto(fixedListF.find(TipoRecibo.HORAS), fixedListF.find(TipoValorConcepto.VACACIONES));
                }
                
                double totalAcumulado = acumuladoBrutoTrabajadores(personal, desde, hasta);
                           
                return totalAcumulado * conceptoVacaciones.getValor().doubleValue() / 100;
                                
            case TipoRecibo.MENSUAL:
                //Busco el Mayor de los sueldos y divido x la cantidad de días
                
            default : return 0;
        }
    }
    
    
    
    
}
