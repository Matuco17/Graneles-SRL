/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.carga.*;
import com.orco.graneles.vo.CargaTurnoVO;
import com.orco.graneles.vo.ResumenCargaEmbarqueVO;
import com.orco.graneles.vo.TurnoObservacionVO;
import java.math.BigDecimal;
import java.util.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author orco
 */
public class ResumenCargasPorTurno extends ReporteGenerico {

    private ResumenCargaEmbarqueVO resumenCarga;
    private List<CargaTurnoVO> cargasTurnos;
    
    @Override
    protected String[] getUrlImagenes() {
        return new String[]{"logoReducido.jpg"};
    }
    
    public ResumenCargasPorTurno(TurnoEmbarque turno){
        calcularResumen(turno.getEmbarque(), turno.getId());
    }
    
    public ResumenCargasPorTurno(Embarque embarque){
        calcularResumen(embarque, null);        
    }
    
    @Override
    public String obtenerReportePDF() {
         JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(cargasTurnos);
        
        return printGenerico(ds, "ResumenTurnosEmbarque", "ResumenTurnosEmbarque_"+ resumenCarga.getEmbarqueCodigo());
    }

    private void calcularResumen(Embarque embarque, Long idTurno) {
        resumenCarga = new ResumenCargaEmbarqueVO(embarque);
        
        cargasTurnos = new ArrayList<CargaTurnoVO>();
        
        for (TurnoEmbarque te : embarque.getTurnoEmbarqueCollection()){
            if (idTurno == null || idTurno.equals(te.getId())){
                for (CargaTurno ct : te.getCargaTurnoCollection()){
                    cargasTurnos.add(new CargaTurnoVO(ct, resumenCarga));
                }
            }
        }
        
        Collections.sort(cargasTurnos, new ComparadorCargaTurno());
        
        
        //Completo las observaciones
        List<TurnoObservacionVO> observaciones = new ArrayList<TurnoObservacionVO>();
        for (TurnoEmbarque te : embarque.getTurnoEmbarqueCollection()){
            if (idTurno == null || idTurno.equals(te.getId())){
                for (TurnoEmbarqueObservaciones teObs : te.getTurnoEmbarqueObservacionesCollection()){
                    observaciones.add(new TurnoObservacionVO(teObs));
                }
            }
        }
        
        //Completo el valor del calculo de abordo
        BigDecimal totalAcumulado = BigDecimal.ZERO;
        
        for (CargaTurnoVO ctVO : cargasTurnos){
            totalAcumulado = totalAcumulado.add(ctVO.getTotalCargaTurno());
            ctVO.setAcumulado(ctVO.getAcumulado().add(totalAcumulado));
            ctVO.setObservaciones(observaciones);
        }
    }
    
    
    private class ComparadorCargaTurno implements Comparator<CargaTurnoVO>{

        @Override
        public int compare(CargaTurnoVO o1, CargaTurnoVO o2) {
            if (o1.getTurnoFecha().equals(o2.getTurnoFecha())){
                if (o1.getTurno().equals(o2.getTurno())){
                    return o1.getCoodinadorNombre().compareToIgnoreCase(o2.getCoodinadorNombre());
                } else {
                    return o1.getTurno().compareTo(o2.getTurno());
                }
            } else {
                return o1.getTurnoFecha().compareTo(o2.getTurnoFecha());
            }
        }
        
    }
    
}
