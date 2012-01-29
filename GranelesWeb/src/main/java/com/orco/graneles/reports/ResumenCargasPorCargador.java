/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.carga.*;
import com.orco.graneles.vo.CargaTurnoVO;
import com.orco.graneles.vo.ResumenCargaEmbarqueVO;
import com.orco.graneles.vo.TrabajadorTurnoEmbarqueVO;
import com.orco.graneles.vo.TurnoObservacionVO;
import java.math.BigDecimal;
import java.util.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author orco
 */
public class ResumenCargasPorCargador extends ReporteGenerico {

    private ResumenCargaEmbarqueVO resumenCarga;
    private List<CargaTurnoVO> cargasTurnos;
    
    @Override
    protected String[] getUrlImagenes() {
        return new String[]{"logoReducido.jpg"};
    }
    
    public ResumenCargasPorCargador(Embarque embarque){
        resumenCarga = new ResumenCargaEmbarqueVO(embarque);
        
        cargasTurnos = new ArrayList<CargaTurnoVO>();
        
        for (TurnoEmbarque te : embarque.getTurnoEmbarqueCollection()){
            for (CargaTurno ct : te.getCargaTurnoCollection()){
                cargasTurnos.add(new CargaTurnoVO(ct, resumenCarga));
            }
        }
        
        //Completo las observaciones
        Map<Long, List<TurnoObservacionVO>> mapObservaciones = new HashMap<Long, List<TurnoObservacionVO>>();
        for (EmbarqueCargador ec : embarque.getEmbarqueCargadoresCollection()){
            mapObservaciones.put(ec.getCargador().getId(), new ArrayList<TurnoObservacionVO>());
        }
        
        //Agrego las observaciones, si no figura cargador, entonces se los agrego a cada empresa que cargo en ese turno
        for (TurnoEmbarque te : embarque.getTurnoEmbarqueCollection()){
            for (TurnoEmbarqueObservaciones teObs : te.getTurnoEmbarqueObservacionesCollection()){
                if (teObs.getCargador() != null){
                    mapObservaciones.get(teObs.getCargador().getId()).add(new TurnoObservacionVO(teObs));
                } else {
                    for (CargaTurno ct : te.getCargaTurnoCollection()){
                        mapObservaciones.get(ct.getCargador().getId()).add(new TurnoObservacionVO(teObs));
                    }
                }
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
            ctVO.setObservaciones(mapObservaciones.get(ctVO.getCoordinadorId()));
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
