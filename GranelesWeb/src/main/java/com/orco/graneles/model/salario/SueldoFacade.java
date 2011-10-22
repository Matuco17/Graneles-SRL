/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.salario.ItemsSueldo;
import com.orco.graneles.domain.salario.Sueldo;
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
import java.util.Map;
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

    protected EntityManager getEntityManager() {
        return em;
    }

    public SueldoFacade() {
        super(Sueldo.class);
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
