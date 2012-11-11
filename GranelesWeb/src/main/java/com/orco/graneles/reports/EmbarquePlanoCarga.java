/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.carga.Embarque;
import com.orco.graneles.vo.ResumenCargaEmbarqueVO;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author orco
 */
public class EmbarquePlanoCarga extends ReporteGenerico {

    ResumenCargaEmbarqueVO resumenEmbarque;

    @Override
    protected String[] getUrlImagenes() {
        return new String[]{"logoGraneles.jpg"};
    }
    
    
    
    public EmbarquePlanoCarga(Embarque embarque) {
        resumenEmbarque = new ResumenCargaEmbarqueVO(embarque);
    }
    
    @Override
    public String obtenerReportePDF() {
        List<ResumenCargaEmbarqueVO> resumenes = new ArrayList<ResumenCargaEmbarqueVO>();
        resumenes.add(resumenEmbarque);
        
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(resumenes);
        
        params.put(JRParameter.REPORT_LOCALE, Locale.ENGLISH); 
                
        return printGenerico(ds, "ResumenEmbarque", "ResumenEmbarque_"+ resumenEmbarque.getEmbarqueCodigo());
    }
    
    
}
