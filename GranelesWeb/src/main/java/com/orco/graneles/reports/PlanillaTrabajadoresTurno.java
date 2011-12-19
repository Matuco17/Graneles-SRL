/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.carga.TurnoEmbarque;
import com.orco.graneles.vo.TrabajadorTurnoEmbarqueVO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author orco
 */
public class PlanillaTrabajadoresTurno extends ReporteGenerico {

    private List<TrabajadorTurnoEmbarqueVO> trabajadores;
    private TurnoEmbarque planilla;
    
    @Override
    protected String[] getUrlImagenes() {
        return new String[]{"logoReducido.jpg"};
    }
    
    public PlanillaTrabajadoresTurno(TurnoEmbarque planilla){
        this.planilla = planilla;
        trabajadores = new ArrayList<TrabajadorTurnoEmbarqueVO>();
        
        for(TrabajadoresTurnoEmbarque tte : planilla.getTrabajadoresTurnoEmbarqueCollection()){
            TrabajadorTurnoEmbarqueVO tteVO = new TrabajadorTurnoEmbarqueVO(tte, BigDecimal.ZERO);
            
            //TODO: Realizar los calculos de sueldos y adicionales y agregarlos
                        
            trabajadores.add(tteVO);
        }
        
        Collections.sort(trabajadores, new ComparadorTteVo());
        
    }
    
    
    @Override
    public String obtenerReportePDF() {
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(trabajadores);
        
        return printGenerico(ds, "TrabajadoresTurno", "PlanillaTrabajadores_"+ planilla.getId());  
    }
    
    
    private class ComparadorTteVo implements Comparator<TrabajadorTurnoEmbarqueVO>{

        @Override
        public int compare(TrabajadorTurnoEmbarqueVO o1, TrabajadorTurnoEmbarqueVO o2) {
            if (o1.getCategoriaDescripcion().equals(o2.getCategoriaDescripcion())){
                return o1.getApellido().compareToIgnoreCase(o2.getApellido());
            } else {
                return o1.getCategoriaDescripcion().compareTo(o2.getCategoriaDescripcion());
            }
        }
        
    }
}
