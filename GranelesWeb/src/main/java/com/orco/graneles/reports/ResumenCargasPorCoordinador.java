/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.carga.Embarque;
import com.orco.graneles.domain.carga.TurnoEmbarque;
import com.orco.graneles.vo.CargaTurnoVO;
import com.orco.graneles.vo.ResumenCargaEmbarqueVO;
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
public class ResumenCargasPorCoordinador extends ReporteGenerico {

    private ResumenCargaEmbarqueVO resumenCarga;
    private List<CargaTurnoVO> cargasTurnos;
    
    @Override
    protected String[] getUrlImagenes() {
        return new String[]{"logoReducido.jpg"};
    }
    
    public ResumenCargasPorCoordinador(Embarque embarque){
        resumenCarga = new ResumenCargaEmbarqueVO(embarque);
        
        cargasTurnos = new ArrayList<CargaTurnoVO>();
        
        for (TurnoEmbarque te : embarque.getTurnoEmbarqueCollection()){
            for (CargaTurno ct : te.getCargaTurnoCollection()){
                cargasTurnos.add(new CargaTurnoVO(ct, resumenCarga));
            }
        }
        
        Collections.sort(cargasTurnos, new ComparadorCargaTurno());
                
        //Completo el valor del calculo de abordo
        BigDecimal totalAcumulado = BigDecimal.ZERO;
        Long coordinadorActualId = 0L;
        
        for (CargaTurnoVO ctVO : cargasTurnos){
            if (!coordinadorActualId.equals(ctVO.getCoordinadorId())){
                totalAcumulado = BigDecimal.ZERO;
                coordinadorActualId = ctVO.getCoordinadorId();
            }
            totalAcumulado = totalAcumulado.add(ctVO.getTotalCargaTurno());
            ctVO.setAcumulado(ctVO.getAcumulado().add(totalAcumulado));
        }
        
    }
    
    @Override
    public String obtenerReportePDF() {
         JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(cargasTurnos);
        
        return printGenerico(ds, "ResumenTurnosEmbarqueXCoordinador", "ResumenTurnosEmbarqueXCoordinador_"+ resumenCarga.getEmbarqueCodigo());
    }
    
    
    private class ComparadorCargaTurno implements Comparator<CargaTurnoVO>{

        @Override
        public int compare(CargaTurnoVO o1, CargaTurnoVO o2) {
            if (o1.getCoordinadorId().equals(o2.getCoordinadorId())){
                if (o1.getTurnoFecha().equals(o2.getTurnoFecha())){
                    return o1.getTurno().compareTo(o2.getTurno());                    
                } else {
                    return o1.getTurnoFecha().compareTo(o2.getTurnoFecha());
                }
            } else {
                return o1.getCoodinadorNombre().compareToIgnoreCase(o2.getCoodinadorNombre());
            }                
        }        
    }
    
}
