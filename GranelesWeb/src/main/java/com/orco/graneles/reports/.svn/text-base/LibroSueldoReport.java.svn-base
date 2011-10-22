/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.salario.ItemsSueldo;
import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.domain.salario.Sueldo;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Clase que genera el reporte de libro de sueldos
 * @author orco
 */
public class LibroSueldoReport extends ReporteGenerico {
    
    private List<ItemsSueldo> dataSource;
    private Periodo periodo;
    
    public LibroSueldoReport(Periodo periodo){
        this.periodo = periodo;
        dataSource = new ArrayList<ItemsSueldo>();
        
        //Agrego todos los items del periodo para el reporte
        for(Sueldo s : periodo.getSueldoCollection())
            for (ItemsSueldo is : s.getItemsSueldoCollection())
                dataSource.add(is);
                
        //Ordeno antes de devolver todo
        Collections.sort(dataSource, new ComparadorItems());
    }

    @Override
    public String obtenerReportePDF() {
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(dataSource);
        
        return printGenerico(ds, "LibroSueldos", "LibroSueldo_"+ periodo.getDescripcion());
    }
    
    
    private class ComparadorItems implements Comparator<ItemsSueldo>{
        /*
         * Criterio de Ordenamiento, si es la misma persona ordeno por el orden del concepto, sino ordento por el apellido y nombre de la persona
         */
        @Override
        public int compare(ItemsSueldo o1, ItemsSueldo o2) {
            if (!o1.getSueldo().getPersonal().getId().equals(o2.getSueldo().getPersonal().getId())){
                return o1.getSueldo().getPersonal().getApellido().compareTo(o2.getSueldo().getPersonal().getApellido());
            } else {
                return o1.getConceptoRecibo().getOrden().compareTo(o2.getConceptoRecibo().getOrden());
            }            
        }
    } 
    
}
