/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.carga.Embarque;
import com.orco.graneles.domain.personal.JornalCaido;
import com.orco.graneles.domain.salario.Adelanto;
import com.orco.graneles.vo.AdelantoVO;
import com.orco.graneles.vo.JornalCaidoVO;
import com.orco.graneles.vo.ResumenCargaEmbarqueVO;
import java.util.ArrayList;
import java.util.List;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author orco
 */
public class ReciboAdelanto extends ReporteGenerico {

    AdelantoVO adVO;

    @Override
    protected String[] getUrlImagenes() {
        return new String[]{"logoReducido.jpg"};
    }
    
    
    
    public ReciboAdelanto(Adelanto adelanto) {
        adVO = new AdelantoVO(adelanto);
    }
    
    @Override
    public String obtenerReportePDF() {
        List<AdelantoVO> adelanto = new ArrayList<AdelantoVO>();
        adelanto.add(adVO);
        
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(adelanto);
        
        return printGenerico(ds, "ReciboAdelanto", "ReciboAdelanto_"+ adVO.getId().toString());
    }
    
    
}
