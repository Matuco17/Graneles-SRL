/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.domain.salario.Sueldo;
import com.orco.graneles.vo.TrabajadorConBrutoVO;
import java.util.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Clase que genera el reporte de libro de sueldos
 * @author orco
 */
public class CierreMesReport extends ReporteGenerico {
    
    private List<TrabajadorConBrutoVO> dataSource;
    private Periodo periodo;
    
    @Override
    protected String[] getUrlImagenes() {
        return new String[]{"logoReducido.jpg"};
    }
    
    public CierreMesReport(Periodo periodo){
        this.periodo = periodo;
        dataSource = new ArrayList<TrabajadorConBrutoVO>();
        
        //Agrego todos los items del periodo para el reporte
        for(Sueldo s : periodo.getSueldoCollection()){
            dataSource.add(new TrabajadorConBrutoVO(s));
        }
            
                
        //Ordeno antes de devolver todo
        Collections.sort(dataSource, new ComparadorItems());
    }

    @Override
    public String obtenerReportePDF() {
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(dataSource);
        
        return printGenerico(ds, "ListadoCierreMes", "CierreMes_"+ periodo.getDescripcion());
    }
    
    
    private class ComparadorItems implements Comparator<TrabajadorConBrutoVO>{
        
        @Override
        public int compare(TrabajadorConBrutoVO o1, TrabajadorConBrutoVO o2) {
            if (o1.getCategoriaId().equals(o2.getCategoriaId())){
                return o1.getApellidoYNombre().compareToIgnoreCase(o2.getApellidoYNombre());
            } else {
                return o1.getCategoriaDescripcion().compareToIgnoreCase(o2.getCategoriaDescripcion());          
            }
        }
    } 
    
}
