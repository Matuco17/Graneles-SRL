/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.facturacion.Factura;
import com.orco.graneles.domain.facturacion.TurnoFacturado;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author orco
 */
public class TurnosFacturados extends ReporteGenerico {

    private Factura factura;
    
    @Override
    protected String[] getUrlImagenes() {
        return new String[]{"logoReducido.jpg"};
    }
    
    public TurnosFacturados(Factura factura) {
        this.factura = factura;
    }
    
    @Override
    public String obtenerReportePDF() {
        List<TurnoFacturado> turnos = new ArrayList<TurnoFacturado>(factura.getTurnosFacturadosCollection());
        Collections.sort(turnos);
        
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(turnos);
        
        return printGenerico(ds, "TurnosFacturados", "TurnosFacturados_" + factura.getComprobante());
    }
    
}
