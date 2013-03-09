/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.facturacion.Factura;
import com.orco.graneles.domain.facturacion.LineaFactura;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author orco
 */
public class FacturaReport extends ReporteGenerico {

    private Factura factura;
    
    @Override
    protected String[] getUrlImagenes() {
        return new String[]{};
    }
    
    public FacturaReport(Factura factura){
        this.factura = factura;
    }
    
    @Override
    public String obtenerReportePDF() {
        //INVIERTO LAS LINEAS YA QUE SE ESTAN GUARDANDO EN ORDEN INVERSO
        List<LineaFactura> datos = new ArrayList<LineaFactura>(this.factura.getLineaFacturaCollection());
        //Collections.reverse(datos);
        
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(datos);
        
        return printGenerico(ds, "factura", "Factura_"+ this.factura.getId());
    }    
    
}
