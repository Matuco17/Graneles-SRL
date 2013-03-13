/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.carga.Embarque;
import com.orco.graneles.vo.EmbarqueVO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author ms.gonzalez
 */
public class NoDanios extends ReporteGenerico {

    private EmbarqueVO embVO;
    
    @Override
    protected String[] getUrlImagenes() {
        return new String[]{"logoReducido.jpg"};
    }
        
    @Override
    protected Locale getReportLocale() {
        return new Locale("en");
    }
    
    public NoDanios(Embarque embarque){
        embVO = new EmbarqueVO(embarque);
    }
    
    @Override
    public String obtenerReportePDF() {
        Collection<EmbarqueVO> colDs = new ArrayList<EmbarqueVO>();
        colDs.add(embVO);
        
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(colDs);
        
        return printGenerico(ds, "NoDanios", "NoDanios_" + embVO.getId());
    }
    
}
