/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.miscelaneos.*;
import com.orco.graneles.domain.personal.Accidentado;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.ConceptoRecibo;
import com.orco.graneles.domain.salario.ItemsSueldo;
import com.orco.graneles.domain.salario.SalarioBasico;
import com.orco.graneles.domain.salario.Sueldo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.carga.TrabajadoresTurnoEmbarqueFacade;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import com.orco.graneles.model.personal.AccidentadoFacade;
import com.orco.graneles.vo.TrabajadorTurnoEmbarqueVO;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
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

                totalAcumulado += calcularDiaTTE(salarioActivo, tte, true);
            }

            List<Accidentado> accs = accidentadoF.getAccidentadosPeriodoYPersonal(desde, hasta, personal);
            for (Accidentado acc : accs){
                totalAcumulado += calculoSueldoAccidentado(acc, calculoDiasAccidentado(desde, hasta, acc)); 
            }

            //Completo los cache para aminorar los calculos
            personalCacheId = personal.getId();
            totalAcumuladoCache = totalAcumulado;
            
            return totalAcumulado;
        }
    }

    /**
     * Calcula el sueldo del accidentado de acuerdo a su valor y los dias trabajados
     * @param accidentado
     * @param diasTrabajados
     * @return 
     */
    public double calculoSueldoAccidentado(Accidentado accidentado, int diasTrabajados){
        return accidentado.getBruto().doubleValue() * diasTrabajados;
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
            desdeCalculado = new DateTime(accidentado.getDesde());
        }
        
        if (accidentado.getHasta().before(hasta)){
            hastaCalculado = new DateTime(accidentado.getHasta()).plusDays(1);
        } else {
            hastaCalculado = new DateTime(hasta).plusDays(1);
        }
        //OBS: le sumo un dia ya que la comparacion es solamente isBefore y no tengo el equal en la fecha.
                
        //calculo los dias de acuerdo al periodo limite impuesto
        DateTime currentFecha = desdeCalculado;
        while (currentFecha.isBefore(hastaCalculado)){
            if (currentFecha.getDayOfWeek() != DateTimeConstants.SATURDAY){
                diasTrabajados++;
            }
            currentFecha = currentFecha.plusDays(1);
        }
        
        return diasTrabajados;
    }
    
    protected double calcularDiaTTE(SalarioBasico salario, TrabajadoresTurnoEmbarque tte, boolean incluirAdicionales) {
        if (salario == null){
            return 0.0;
        } else {
            //Obtengo el valor del bruto ya que depende si trabajo 6 o 3 horas (y el salario está en valor de horas
            double basicoBruto = salario.getBasico().doubleValue() / 6 * tte.getHoras().doubleValue();
            double totalConcepto = basicoBruto; //resultado de la suma del concepto
            //Realizo el agregado de los modificadores de tarea
            if (incluirAdicionales){
                if (tte.getTarea().getInsalubre()){
                    totalConcepto += basicoBruto * (getMapAdicTarea().get(AdicionalTarea.INSALUBRE).getValorDefecto().doubleValue() / 100);
                }
                if (tte.getTarea().getPeligrosa()){
                    totalConcepto += basicoBruto * (getMapAdicTarea().get(AdicionalTarea.PELIGROSA).getValorDefecto().doubleValue() / 100);
                }
                if (tte.getTarea().getPeligrosa2()){
                    totalConcepto += basicoBruto * (getMapAdicTarea().get(AdicionalTarea.PELIGROSA2).getValorDefecto().doubleValue() / 100);
                }
                if (tte.getTarea().getProductiva()){
                    totalConcepto += basicoBruto * (getMapAdicTarea().get(AdicionalTarea.PRODUCTIVA).getValorDefecto().doubleValue() / 100);
                }
                if (tte.getTarea().getEspecidalidad()){
                    totalConcepto += basicoBruto * (getMapAdicTarea().get(AdicionalTarea.ESPECIALIDAD).getValorDefecto().doubleValue() / 100);
                }
            }
            //Ahora aplico el valor del modificador del tipo de jornal
            totalConcepto += totalConcepto * tte.getPlanilla().getTipo().getPorcExtraBruto().doubleValue() / 100;
            totalConcepto += basicoBruto * tte.getPlanilla().getTipo().getPorcExtraBasico().doubleValue() / 100;
            return totalConcepto;
        }
    }
    
    public TrabajadorTurnoEmbarqueVO calcularDiaTTE(TrabajadoresTurnoEmbarque tte, boolean incluirAdicionales) {
        //Obtengo el valor del bruto ya que depende si trabajo 6 o 3 horas (y el salario está en valor de horas
        SalarioBasico salario = salarioBasicoF.obtenerSalarioActivo(tte.getTarea(), tte.getCategoria(), tte.getPlanilla().getFecha());
        
        double basicoBruto = 0.0;
        double totalConcepto = 0.0; //resultado de la suma del concepto

        TrabajadorTurnoEmbarqueVO tteVO = new TrabajadorTurnoEmbarqueVO(tte, BigDecimal.ZERO);
        
        if (salario != null){
            
            basicoBruto = salario.getBasico().doubleValue() / 6 * tte.getHoras().doubleValue();
            totalConcepto = basicoBruto; //resultado de la suma del concepto

            //Realizo el agregado de los modificadores de tarea
            if (incluirAdicionales){
                if (tte.getTarea().getInsalubre()){
                    double conceptoInsalubre = basicoBruto * (getMapAdicTarea().get(AdicionalTarea.INSALUBRE).getValorDefecto().doubleValue() / 100);
                    conceptoInsalubre += tte.getPlanilla().getTipo().getPorcExtraBruto().doubleValue() / 100;
                    tteVO.setInsalubre(new BigDecimal(conceptoInsalubre));
                    totalConcepto += conceptoInsalubre ;
                }
                if (tte.getTarea().getPeligrosa()){
                    double conceptoPeligrosa = basicoBruto * (getMapAdicTarea().get(AdicionalTarea.PELIGROSA).getValorDefecto().doubleValue() / 100);
                    conceptoPeligrosa += tte.getPlanilla().getTipo().getPorcExtraBruto().doubleValue() / 100;
                    tteVO.setPeligrosa(new BigDecimal(conceptoPeligrosa));
                    totalConcepto += conceptoPeligrosa ;
                }
                if (tte.getTarea().getPeligrosa2()){
                    double conceptoPeligrosa2 = basicoBruto * (getMapAdicTarea().get(AdicionalTarea.PELIGROSA2).getValorDefecto().doubleValue() / 100);
                    conceptoPeligrosa2 += tte.getPlanilla().getTipo().getPorcExtraBruto().doubleValue() / 100;
                    tteVO.setPeligrosa2(new BigDecimal(conceptoPeligrosa2));
                    totalConcepto += conceptoPeligrosa2;
                }
                if (tte.getTarea().getProductiva()){
                    double conceptoProductiva = basicoBruto * (getMapAdicTarea().get(AdicionalTarea.PRODUCTIVA).getValorDefecto().doubleValue() / 100);
                    conceptoProductiva += tte.getPlanilla().getTipo().getPorcExtraBruto().doubleValue() / 100;
                    tteVO.setProductiva(new BigDecimal(conceptoProductiva));
                    totalConcepto += conceptoProductiva ;
                }
                if (tte.getTarea().getEspecidalidad()){
                    double conceptoEspecialidad = basicoBruto * (getMapAdicTarea().get(AdicionalTarea.ESPECIALIDAD).getValorDefecto().doubleValue() / 100);
                    conceptoEspecialidad += tte.getPlanilla().getTipo().getPorcExtraBruto().doubleValue() / 100;
                    tteVO.setEspecialidad(new BigDecimal(conceptoEspecialidad));
                    totalConcepto += conceptoEspecialidad ;
                }
            }
            //Ahora aplico el valor del modificador del tipo de jornal
            totalConcepto += basicoBruto * tte.getPlanilla().getTipo().getPorcExtraBasico().doubleValue() / 100;
        }
        
        tteVO.setValorBruto(new BigDecimal(totalConcepto));
        tteVO.setJornalBasico(new BigDecimal(basicoBruto));
        tteVO.setValorTurno(new BigDecimal(calcularNeto(tte.getPersonal(), totalConcepto)));
        
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
     * Metodo que calcula el valor total del dia trabajado para el Trabajador
     * @param tte
     * @param mapAdicTarea
     * @return 
     */
    public double calcularDiaTrabajadoTTE(TrabajadoresTurnoEmbarque tte, boolean incluirAdicionales) {
        //Esto significa que debo realizar el calculo del total bruto con el salario basico
        //Obtengo el salario correspondiente al tte
        SalarioBasico salario = salarioBasicoF.obtenerSalarioActivo(tte.getTarea(), tte.getCategoria(), tte.getPlanilla().getFecha());
        return calcularDiaTTE(salario, tte, incluirAdicionales);
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
                return new BigDecimal(concepto.getValor().doubleValue()); 
            case TipoValorConcepto.JUBILACION:
                return new BigDecimal(concepto.getValor().doubleValue()); 
            case TipoValorConcepto.OBRA_SOCIAL:
                return new BigDecimal(concepto.getValor().doubleValue()); 
            case TipoValorConcepto.SINDICATO:
                if (personal.getSindicato()){
                    double porcSindicato = concepto.getValor().doubleValue();
                    if (personal.getCategoriaPrincipal().getSindicato() != null){
                        porcSindicato = personal.getCategoriaPrincipal().getSindicato().getPorcentaje().doubleValue();
                    }
                return new BigDecimal(porcSindicato); 
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
