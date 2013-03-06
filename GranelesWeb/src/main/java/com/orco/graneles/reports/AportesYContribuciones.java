/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.facturacion.Factura;
import com.orco.graneles.domain.facturacion.TurnoFacturado;
import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.vo.AporteContribucionVO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author orco
 */
public class AportesYContribuciones extends ReporteGenerico {

    private List<AporteContribucionVO> aportesYContribuciones;
    private Periodo periodo;
    
    @Override
    protected String[] getUrlImagenes() {
        return new String[]{"logoReducido.jpg"};
    }
    
    public AportesYContribuciones(Periodo periodo, List<AporteContribucionVO> aportesYContribuciones) {
        this.periodo = periodo;
        this.aportesYContribuciones = aportesYContribuciones;
    }
    
    @Override
    public String obtenerReportePDF() {
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(aportesYContribuciones);
        
        return printGenerico(ds, "AportesContribuciones", "AportesYContribuciones_" + periodo.getDescripcion());
    }
    
}
