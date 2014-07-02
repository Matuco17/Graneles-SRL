/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.orco.graneles.reports;

import com.orco.graneles.domain.facturacion.Factura;
import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.vo.ComprobanteVO;
import java.util.ArrayList;
import java.util.List;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author groupon
 */
public class LibroIVA extends ReporteGenerico {

    List<ComprobanteVO> comprobantes;
    String periodoDescripcion;
    int paginaInicial;

    public LibroIVA(List<Factura> facturas, String periodoDescripcion, int paginaInicial) {
        this.comprobantes = new ArrayList<ComprobanteVO>();
        for (Factura f : facturas) {
            this.comprobantes.add(new ComprobanteVO(f));
        }
        this.periodoDescripcion = periodoDescripcion;
        this.paginaInicial = paginaInicial;
    }
    
    
    @Override
    public String obtenerReportePDF() {
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(comprobantes);
        
        params.put("paginaInicial", this.paginaInicial);
        
        return printGenerico(ds, "IvaVentas", "IvaVentas_"+ this.periodoDescripcion);
    }
    
}
