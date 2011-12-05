/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.carga;

import com.orco.graneles.domain.carga.Embarque;
import com.orco.graneles.domain.carga.TurnoEmbarque;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.vo.TurnoEmbarqueExcelVO;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
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
public class TurnoEmbarqueFacade extends AbstractFacade<TurnoEmbarque> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public TurnoEmbarqueFacade() {
        super(TurnoEmbarque.class);
    }
    
    public TurnoEmbarque crearNuevoTurnoEmbarque(Embarque embarque){
        TurnoEmbarque te = new TurnoEmbarque();
        te.setEmbarque(embarque);
        te.setFecha(new Date());
        return te;
    }
    
    
     /**
     * Devuelve un map de turnos teniendo como clave el nro de embarque (nro de planilla originalmente) del excel
     * @param archivoXLS archivo excel
     * @param desde fecha limite
     * @param hasta fecha limite
     * @return map con los turnos que cumplen con las fechas siempre que tengan limites, sino se devuelven todos
     */
    public Map<Long, TurnoEmbarqueExcelVO> embarquesDesdeExcel(InputStream archivoXLS, Date desde, Date hasta){
        Map<Long, TurnoEmbarqueExcelVO> turnos = new HashMap<Long, TurnoEmbarqueExcelVO>();
        
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
                
                //Obtengo la fecha del embarque
                Date fechaJornalExcel = filaActual.getCell(4).getDateCellValue();
                
                //Pregunto si tiene limites, si no tiene siempre se agrega sino solo la fecha q entre entre los limites
                if ((desde == null) || 
                    (desde != null && hasta != null && (fechaJornalExcel.compareTo(desde) >= 0) && (fechaJornalExcel.compareTo(hasta) <= 0))){
                    
                    TurnoEmbarqueExcelVO turnoActual = new TurnoEmbarqueExcelVO();
                    
                    //ID del embarque (Planilla)
                    turnoActual.setPlanilla((new Double(filaActual.getCell(0).getNumericCellValue()).longValue()));
                    
                    //Seteo la fecha tambien
                    turnoActual.setFechaJornada(fechaJornalExcel);
                    
                    //Seteo el tipo de Jornal
                    turnoActual.setTipoJornal((new Double(filaActual.getCell(5).getNumericCellValue()).intValue()));
                    
                    turnos.put(turnoActual.getPlanilla(), turnoActual);
                }
            }
        } catch (Exception e) {
            turnos = null;
        }
        
        
        
        return turnos;
    }
    
}
