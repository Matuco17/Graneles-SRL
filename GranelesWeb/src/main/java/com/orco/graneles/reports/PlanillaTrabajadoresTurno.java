/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.carga.TurnoEmbarque;
import com.orco.graneles.domain.carga.TurnoEmbarqueObservaciones;
import com.orco.graneles.domain.miscelaneos.TipoConceptoRecibo;
import com.orco.graneles.domain.miscelaneos.TipoRecibo;
import com.orco.graneles.domain.salario.ConceptoRecibo;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import com.orco.graneles.model.salario.ConceptoReciboFacade;
import com.orco.graneles.model.salario.SueldoFacade;
import com.orco.graneles.vo.TrabajadorTurnoEmbarqueVO;
import com.orco.graneles.vo.TurnoObservacionVO;
import java.math.BigDecimal;
import java.util.*;
import javax.ejb.EJB;
import javax.ejb.Stateless;
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
    
    public PlanillaTrabajadoresTurno(TurnoEmbarque planilla, List<TrabajadorTurnoEmbarqueVO> trabajadoresPlanilla){
        this.planilla = planilla;
        this.trabajadores = trabajadoresPlanilla;
        
        BigDecimal totalBruto = BigDecimal.ZERO;
        BigDecimal totalNeto = BigDecimal.ZERO;
        
        for (TrabajadorTurnoEmbarqueVO tteVO : this.trabajadores){
            totalBruto = totalBruto.add(tteVO.getValorBruto());
            totalNeto = totalNeto.add(tteVO.getValorTurno());
        }
        
        List<TurnoObservacionVO> observaciones = new ArrayList<TurnoObservacionVO>();
        for (TurnoEmbarqueObservaciones teObs : planilla.getTurnoEmbarqueObservacionesCollection()){
            observaciones.add(new TurnoObservacionVO(teObs));
        }
        
        for (TrabajadorTurnoEmbarqueVO tteVO : this.trabajadores){
            tteVO.setObservaciones(observaciones);
            tteVO.setTotalBruto(totalBruto);
            tteVO.setTotalNeto(totalNeto);
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
            if (Math.abs(o1.getValorBruto().doubleValue() - o2.getValorBruto().doubleValue()) >= 0.01) {
                return (-1) * o1.getValorBruto().compareTo(o2.getValorBruto());            
            }   else {
                return o1.getApellido().compareToIgnoreCase(o2.getApellido());
            }
        }
        
    }
}
