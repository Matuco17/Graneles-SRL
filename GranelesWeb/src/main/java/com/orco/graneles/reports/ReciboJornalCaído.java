/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.carga.Embarque;
import com.orco.graneles.domain.personal.JornalCaido;
import com.orco.graneles.vo.JornalCaidoVO;
import com.orco.graneles.vo.ResumenCargaEmbarqueVO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author orco
 */
public class ReciboJornalCaído extends ReporteGenerico {

    JornalCaidoVO jcVO;

    @Override
    protected String[] getUrlImagenes() {
        return new String[]{"logoReducido.jpg"};
    }
    
    
    
    public ReciboJornalCaído(JornalCaido jornalCaido) {
        jcVO = new JornalCaidoVO(jornalCaido);
    }
    
    @Override
    public String obtenerReportePDF() {
        List<JornalCaidoVO> jornalCaido = new ArrayList<JornalCaidoVO>();
        jornalCaido.add(jcVO);
        
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(jornalCaido);
        
        return printGenerico(ds, "ReciboJornalCaido", "ReciboJornalCaido_"+ 
                ((jcVO.getId() != null) 
                    ? jcVO.getId().toString() 
                    : jcVO.getId() + (new Date()).toString().replaceAll(" ", ""))
                );
    }
    
    
}
