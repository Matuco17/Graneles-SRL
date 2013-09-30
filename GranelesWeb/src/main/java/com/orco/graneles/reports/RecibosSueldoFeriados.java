/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.miscelaneos.TipoConceptoRecibo;
import com.orco.graneles.domain.miscelaneos.TipoValorConcepto;
import com.orco.graneles.domain.salario.Feriado;
import com.orco.graneles.domain.salario.ItemsSueldo;
import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.domain.salario.Sueldo;
import com.orco.graneles.vo.ItemSueldoVO;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Clase que genera el reporte de libro de sueldos
 * @author orco
 */
public class RecibosSueldoFeriados extends ReporteGenerico {
    
    private List<ItemSueldoVO> dataSource;
    private Feriado feriado;

    
    public RecibosSueldoFeriados(Periodo periodo, List<Sueldo> sueldos, Feriado feriado){
        this.feriado = feriado;
        dataSource = new ArrayList<ItemSueldoVO>();

        String descripcionConcepto = "Vispera de Feriado " + feriado.getDescripcion();
        
        //Agrego todos los items del periodo para el reporte
        for(Sueldo s : sueldos){
            
            for (ItemsSueldo is : s.getItemsSueldoCollection()){
                if (is.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.REMUNERATIVO)){
                    dataSource.add(new ItemSueldoVO(is, true, feriado.getFecha(), feriado.getFecha(), descripcionConcepto));
                } else {
                    dataSource.add(new ItemSueldoVO(is, true, feriado.getFecha(), feriado.getFecha()));
                }
            }
        }
                
        //Ordeno antes de devolver todo
        Collections.sort(dataSource, new ComparadorItems());
    }

    @Override
    public String obtenerReportePDF() {
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(dataSource);
        
        params.put("sac", Boolean.FALSE);
        
        return printGenerico(ds, "ReciboSueldos", "RecibosFeriado_"+ (new SimpleDateFormat("yyyyMMdd")).format(feriado.getFecha()));
    }
    
    
    private class ComparadorItems implements Comparator<ItemSueldoVO>{
        /*
         * Criterio de Ordenamiento, si es la misma persona ordeno por el orden del concepto, sino ordento por el apellido y nombre de la persona
         */
        @Override
        public int compare(ItemSueldoVO o1, ItemSueldoVO o2) {
            if (!o1.getSueldo().getPersonal().getId().equals(o2.getSueldo().getPersonal().getId())){
                return o1.getSueldo().getPersonal().getApellido().compareTo(o2.getSueldo().getPersonal().getApellido());
            } else {
                return o1.getConceptoRecibo().getOrden().compareTo(o2.getConceptoRecibo().getOrden());
            }            
        }
    } 
    
}
