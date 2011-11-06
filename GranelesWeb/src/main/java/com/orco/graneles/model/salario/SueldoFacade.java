/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.miscelaneos.AdicionalTarea;
import com.orco.graneles.domain.miscelaneos.FixedList;
import com.orco.graneles.domain.miscelaneos.TipoConceptoRecibo;
import com.orco.graneles.domain.miscelaneos.TipoValorConcepto;
import com.orco.graneles.domain.salario.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.vo.CargaRegVO;
import com.orco.graneles.vo.TurnoEmbarqueExcelVO;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
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

    @EJB
    private ItemsSueldoFacade itemSueldoF;
    @EJB
    private ConceptoReciboFacade conceptoReciboF;

    protected EntityManager getEntityManager() {
        return em;
    }

    public SueldoFacade() {
        super(Sueldo.class);
    }
    
    public Sueldo calcularSueldoTTE(Periodo periodo, TrabajadoresTurnoEmbarque tte, Map<Integer, List<ConceptoRecibo>> conceptos, Map<Integer, FixedList> mapAdicTarea) {
        double totalBruto = 0; //Valor total del bruto para que se termine de cerrar esto
        Sueldo sueldoTTE = new Sueldo();
        sueldoTTE.setPeriodo(periodo);
        sueldoTTE.setPersonal(tte.getPersonal());
        
        //Por cada tipo de Concepto Remunerativo
        for (ConceptoRecibo cRemunerativo : conceptos.get(TipoConceptoRecibo.REMUNERATIVO)){
            if (cRemunerativo.getTipo().getId().equals(TipoValorConcepto.DIAS_TRABAJO)){ 
                double totalConcepto = conceptoReciboF.calcularDiaTrabajadoTTE(tte, mapAdicTarea);
                //Agrego el valor del total del concepto al valor del total del bruto
                totalBruto += totalConcepto;
                //Una vez que tengo el valor de esta hora, lo agrego
                itemSueldoF.crearItemSueldo(tte.getPlanilla().getTipo().getConceptoRecibo(), new BigDecimal(tte.getHoras()), new BigDecimal(totalConcepto), sueldoTTE);
            }
        }
        
        //Por cada tipo de Concepto Deductivo
        for (ConceptoRecibo cDeductivo : conceptos.get(TipoConceptoRecibo.DEDUCTIVO)){
            double totalConcepto = conceptoReciboF.calcularValorConcepto(cDeductivo, totalBruto);
            
            //Una vez que tengo el valor de esta hora, lo agrego
            itemSueldoF.crearItemSueldo(cDeductivo, cDeductivo.getValor(), new BigDecimal(totalConcepto), sueldoTTE);
        }
        
        //Por cada tipo de Concepto No Remunerativo
        for (ConceptoRecibo cNoRemunerativo : conceptos.get(TipoConceptoRecibo.NO_REMUNERATIVO)){
            double totalConcepto = conceptoReciboF.calcularValorConcepto(cNoRemunerativo, totalBruto);
            
            //Una vez que tengo el valor de esta hora, lo agrego
            itemSueldoF.crearItemSueldo(cNoRemunerativo, cNoRemunerativo.getValor(), new BigDecimal(totalConcepto), sueldoTTE);
        }
                
        return sueldoTTE;
    }
    
    
    /**
     * Método que suma de manera correcta los items sueldo de cada uno de los sueldos
     * @param s1
     * @param s2
     * @return 
     */
    public Sueldo mergeSueldos(Sueldo s1, Sueldo s2){
        //Junto cada uno de los items con el mismo concepto y saco de s2 los que uso
        for (ItemsSueldo isS1 : s1.getItemsSueldoCollection()){
            for (ItemsSueldo isS2 : s2.getItemsSueldoCollection()){
                if (isS1.getConceptoRecibo().getId().equals(isS2.getConceptoRecibo().getId())){
                    //Si es remunerativo, sumo todo, sino solo sumo el valor
                    if (isS1.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.REMUNERATIVO)){
                        isS1.setCantidad(isS1.getCantidad().add(isS2.getCantidad()));
                    }
                    
                    isS1.setValorCalculado(isS1.getValorCalculado().add(isS2.getValorCalculado()));
                    isS1.setValorIngresado(isS1.getValorIngresado().add(isS2.getValorIngresado()));
                    
                    //Saco el valor de S2
                    s2.getItemsSueldoCollection().remove(isS2);
                }
            }
        }
        
        //Agrego a s1 todos los items de s2 que no compartian concepto
        for (ItemsSueldo isS2 : s2.getItemsSueldoCollection()){
            s1.getItemsSueldoCollection().add(isS2);
        }
        
        return s1;
    }
    
        /**
     * Devuelve un map de registros teniendo como clave el nro de embarque (nro de planilla originalmente) del excel
     * @param archivoXLS archivo excel
     * @param desde fecha limite
     * @param hasta fecha limite
     * @return map con los registros que cumplen con las fechas siempre que tengan limites, sino se devuelven todos
     */
    
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
