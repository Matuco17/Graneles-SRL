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
import com.orco.graneles.model.miscelaneos.FixedListFacade;
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
    
    
    @EJB
    private ItemsSueldoFacade itemSueldoF;
    @EJB
    private ConceptoReciboFacade conceptoReciboF;
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
            double totalConcepto = conceptoReciboF.calcularDiaTrabajadoTTE(tte, true);
            //Agrego el valor del total del concepto al valor del total del bruto
            
            return crearSueldoXItemBruto(tte.getPlanilla().getTipo().getConceptoRecibo(),
                    new BigDecimal(tte.getHoras()), 
                    new BigDecimal(totalConcepto),
                    periodo, conceptos, tte.getPersonal());
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
                                                                fixedListF.find(TipoValorConcepto.HORAS_HABILES));
        }
        
        
        int diasTrabajados = conceptoReciboF.calculoDiasAccidentado(periodo.getDesde(), periodo.getHasta(), accidentado);
        
        double brutoCalculado = conceptoReciboF.calculoSueldoAccidentado(accidentado, diasTrabajados);
        
        
        return crearSueldoXItemBruto(conceptoReciboAccidentadoCache, 
                        new BigDecimal(diasTrabajados * 6),
                        new BigDecimal(brutoCalculado),
                        periodo, conceptos, accidentado.getPersonal());
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
        
        double brutoCalculado = conceptoReciboF.calculoSueldoAccidentado(accidentado, diasTrabajados);
        
        
        Sueldo sueldoCalculado = crearSueldoXItemBruto(conceptoReciboAccidentadoCache, 
                        new BigDecimal(diasTrabajados * 6),
                        new BigDecimal(brutoCalculado),
                        null, conceptos, accidentado.getPersonal());
        
        return sueldoCalculado.getTotalSueldoNeto();
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
                                new BigDecimal(calculadoSAC),
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
                    new BigDecimal(calculadoVacaciones),
                    periodo, conceptos, personal);
    
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
    
    /**
     * Agrega al map de Registros la suma de los embarques de cada uno de los trabajadores
     * @param archivoXLS archivo con los registros
     * @param registros mapeo de los registros (parametro de salida)
     * @param planillas planillas que participan la busqueda de las horas trabajadas
     */
    public void salariosPlanillaDesdeExcel(InputStream archivoXLS, Map<String, CargaRegVO[]> registros, Map<Long, TurnoEmbarqueExcelVO> planillas){
        
        try {
            //Creo el libro y selecciono la primera hoja
            HSSFWorkbook workBook = new HSSFWorkbook(archivoXLS);
            HSSFSheet hssfSheet = workBook.getSheetAt(0);

            //Itero sobre las filas
            Iterator<Row> filaIterator = hssfSheet.rowIterator();
            filaIterator.next(); //Avanzo una fila ya que es la primera con los titulos de la tabla
            while (filaIterator.hasNext())
            {
                Row filaActual =  filaIterator.next();
                
                //Obtengo la planilla
                Long planilla = (new Double(filaActual.getCell(7).getNumericCellValue())).longValue();
                
                //SPregunto si tiene limites, si no tiene siempre se agrega sino solo la fecha q entre entre los limites
                if ((planillas == null) || 
                    (planillas.keySet().contains(planilla))){
                    
                    String cuilExcel = filaActual.getCell(3).getStringCellValue();
                    //Verifico si está la carga, si está entonces le sumo al anterior, sino le creo uno nuevo
                    CargaRegVO[] registroActual = obtenerRegistrosDelEmpleado(registros, cuilExcel);
                    
                    //Selecciono el tipo del PagoFeri
                    int tipoJornal = planillas.get(planilla).getTipoJornal(); 
                    
                    //Cantidad del bruto
                    registroActual[tipoJornal].setCantidadBruto(registroActual[tipoJornal].getCantidadBruto().add(new BigDecimal(new Double(filaActual.getCell(5).getNumericCellValue()))));
                                       
                    //Sueldo bruto
                    registroActual[tipoJornal].setSueldoBruto(registroActual[tipoJornal].getSueldoBruto().add(new BigDecimal(new Double(filaActual.getCell(6).getNumericCellValue()))));
                    
                    //Jubilacion
                    registroActual[tipoJornal].setJubilacion(registroActual[tipoJornal].getJubilacion().add(new BigDecimal(new Double(filaActual.getCell(19).getNumericCellValue()))));
                    
                    //Obra social
                    registroActual[tipoJornal].setObraSocial(registroActual[tipoJornal].getObraSocial().add(new BigDecimal(new Double(filaActual.getCell(20).getNumericCellValue()))));
                    
                    //Fondo Comp
                    registroActual[tipoJornal].setFondoComp(registroActual[tipoJornal].getFondoComp().add(new BigDecimal(new Double(filaActual.getCell(21).getNumericCellValue()))));
                    
                    //Sindicato
                    registroActual[tipoJornal].setSindicato(registroActual[tipoJornal].getSindicato().add(new BigDecimal(new Double(filaActual.getCell(22).getNumericCellValue()))));
                    
                    //No Remunerativo
                    registroActual[tipoJornal].setNoRemunerativo(registroActual[tipoJornal].getNoRemunerativo().add(new BigDecimal(new Double(filaActual.getCell(23).getNumericCellValue()))));
                    
                    //Dto Judicial
                    registroActual[tipoJornal].setDtoJudicial(registroActual[tipoJornal].getDtoJudicial().add(new BigDecimal(new Double(filaActual.getCell(24).getNumericCellValue()))));
                  
                    //Agrego el registro al mapa de registros
                    registros.put(cuilExcel, registroActual);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    } 
        
    /**
     * Devuelve un map con los registros individuales de las personas en el tiempo cumplimentado dependiendo del tipo
     * @param archivoXLS archivo excel
     * @param desde fecha limite
     * @param hasta fecha limite
     * @return map con los registros que cumplen con las fechas siempre que tengan limites, sino se devuelven todos
     */
    public void otrosConceptosDesdeExcel(InputStream archivoXLS, Map<String, CargaRegVO[]> registros, Date desde, Date hasta){
        
        try {
            //Creo el libro y selecciono la primera hoja
            HSSFWorkbook workBook = new HSSFWorkbook(archivoXLS);
            HSSFSheet hssfSheet = workBook.getSheetAt(0);

            //Itero sobre las filas
            Iterator<Row> filaIterator = hssfSheet.rowIterator();
            filaIterator.next(); //Avanzo una fila ya que es la primera con los titulos de la tabla
            while (filaIterator.hasNext())
            {
                Row filaActual =  filaIterator.next();
                
                //Obtengo la fecha del registro, si es valida entonces la proceso sino no
                Date fechaPagoExcel = filaActual.getCell(0).getDateCellValue();
                                
                //Pregunto si tiene limites, si no tiene siempre se agrega sino solo la fecha q entre entre los limites
                if ((desde == null) || 
                    (desde != null && hasta != null && (fechaPagoExcel.compareTo(desde) >= 0) && (fechaPagoExcel.compareTo(hasta) <= 0))){
                    
                    final String cuilExcel = filaActual.getCell(1).getStringCellValue();
                    CargaRegVO[] registroActual = obtenerRegistrosDelEmpleado(registros, cuilExcel);
                    
                    //Selecciono el tipo del PagoFeri
                    int tipoPagoFeri = new Double(filaActual.getCell(2).getNumericCellValue()).intValue();
                                     
                    //Cantidad
                    registroActual[tipoPagoFeri].setCantidadBruto(registroActual[tipoPagoFeri].getCantidadBruto().add(new BigDecimal(new Double(filaActual.getCell(11).getNumericCellValue()))));
                    
                    //Sueldo bruto
                    registroActual[tipoPagoFeri].setSueldoBruto(registroActual[tipoPagoFeri].getSueldoBruto().add(new BigDecimal(new Double(filaActual.getCell(5).getNumericCellValue()))));
                    
                    //Jubilacion
                    registroActual[tipoPagoFeri].setJubilacion(registroActual[tipoPagoFeri].getJubilacion().add(new BigDecimal(new Double(filaActual.getCell(7).getNumericCellValue()))));
                    
                    //Obra social
                    registroActual[tipoPagoFeri].setObraSocial(registroActual[tipoPagoFeri].getObraSocial().add(new BigDecimal(new Double(filaActual.getCell(10).getNumericCellValue()))));
                    
                    //Fondo Comp
                    registroActual[tipoPagoFeri].setFondoComp(registroActual[tipoPagoFeri].getFondoComp().add(new BigDecimal(new Double(filaActual.getCell(9).getNumericCellValue()))));
                    
                    //Sindicato
                    registroActual[tipoPagoFeri].setSindicato(registroActual[tipoPagoFeri].getSindicato().add(new BigDecimal(new Double(filaActual.getCell(8).getNumericCellValue()))));
                    
                    //No Remunerativo
                    if (filaActual.getCell(15) != null)
                        registroActual[tipoPagoFeri].setNoRemunerativo(registroActual[tipoPagoFeri].getNoRemunerativo().add(new BigDecimal(new Double(filaActual.getCell(15).getNumericCellValue()))));
                    
                    //Dto Judicial
                    if (filaActual.getCell(16) != null)
                        registroActual[tipoPagoFeri].setDtoJudicial(registroActual[tipoPagoFeri].getDtoJudicial().add(new BigDecimal(new Double(filaActual.getCell(16).getNumericCellValue()))));
                  
                    //Agrego el registro al mapa de registros
                    registros.put(cuilExcel, registroActual);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    /**
     * OBtiene el registro del map del empleado utilizando su cuil. Si no se encuentra crea un registro nuevo y limpio
     * @param registros
     * @param cuilExcel
     * @return 
     */
    private CargaRegVO[] obtenerRegistrosDelEmpleado(Map<String, CargaRegVO[]> registros, final String cuilExcel) {
        //Verifico si está la carga, si está entonces le sumo al anterior, sino le creo uno nuevo
        CargaRegVO[] registroActual = registros.get(cuilExcel) ;
        if (registroActual == null){
            registroActual = new CargaRegVO[PeriodoFacade.MAXIMO_TIPOS_JORNALES_XLS];
            //CUIL del personal
            for (int i=0; i< PeriodoFacade.MAXIMO_TIPOS_JORNALES_XLS; i++){
                registroActual[i] = new CargaRegVO();
                registroActual[i].setCuilPersonal(cuilExcel);
            }
        }
        return registroActual;
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
