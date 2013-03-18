/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.carga.*;
import com.orco.graneles.model.carga.CargaTurnoFacade;
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

    private List<CargaTurnoVO> cargasTurnos;
    private Embarque embarque;
    
    @Override
    protected String[] getUrlImagenes() {
        return new String[]{"logoReducido.jpg"};
    }
    
    public ResumenCargasPorCargador(Embarque embarque, CargaTurnoFacade cargaTurnoF){
        this.embarque = embarque;
        cargasTurnos = cargaTurnoF.completarCargaTurnosXCargador(embarque);
    }
    
    @Override
    public String obtenerReportePDF() {
         JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(cargasTurnos);
        
        return printGenerico(ds, "ResumenTurnosEmbarqueXCoordinador", "ResumenTurnosEmbarqueXCoordinador_"+ embarque.getCodigo());
    }

   
    
    
}
