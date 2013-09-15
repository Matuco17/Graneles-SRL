/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.carga.*;
import com.orco.graneles.domain.facturacion.MovimientoCtaCte;
import com.orco.graneles.model.carga.CargaTurnoFacade;
import com.orco.graneles.vo.CargaTurnoVO;
import com.orco.graneles.vo.MovCtaCteVO;
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
public class MovCtaCteReport extends ReporteGenerico {

    private List<MovCtaCteVO> movimientoVOs;
    private Date desde;
    private Date hasta;
    private Boolean ocultarFacturas;
    private Boolean ocultarEmpresas;
    private Boolean ocultarTotales;
    private Boolean ocultarDetalles;
    private Boolean dinero;
    
    
    @Override
    protected String[] getUrlImagenes() {
        return new String[]{"logoReducido.jpg"};
    }
    
    public MovCtaCteReport(List<MovimientoCtaCte> movimientos, Date desde, Date hasta, Boolean ocultarEmpresas, Boolean ocultarFacturas, Boolean ocultarTotales, Boolean ocultarDetalles, boolean dinero){
        this.desde = desde;
        this.hasta = hasta;
        this.ocultarEmpresas = ocultarEmpresas;
        this.ocultarFacturas = ocultarFacturas;
        this.ocultarDetalles = ocultarDetalles;
        this.ocultarTotales = ocultarTotales;
        this.dinero = dinero;
    
        Comparator<MovCtaCteVO> comparador = null;
        
        if (ocultarEmpresas && ocultarFacturas){
            comparador = new ComparadorTotal();
        } else if (ocultarFacturas){
            comparador = new ComparadorEmpresa();
        } else {
            comparador = new ComparadorEmpresaFactura();
        }
    
        movimientoVOs = new ArrayList<MovCtaCteVO>();
    
        for (MovimientoCtaCte mCC : movimientos){
            movimientoVOs.add(new MovCtaCteVO(mCC));
        }
    
        Collections.sort(movimientoVOs, comparador);
    }
    
    @Override
    public String obtenerReportePDF() {
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(movimientoVOs);
        
        params.put("desde", desde);
        params.put("hasta", hasta);
        params.put("ocultarEmpresas", ocultarEmpresas);
        params.put("ocultarFacturas", ocultarFacturas);
        params.put("ocultarDetalles", ocultarDetalles);
        params.put("ocultarTotales", ocultarTotales);
        params.put("dinero", dinero);
        
        
        return printGenerico(ds, "ResumenCtaCteXFactura", "ResumenCuentaCorriente_"+ (new Date()).getTime());
    }

   
    private class ComparadorTotal implements Comparator<MovCtaCteVO>{

        @Override
        public int compare(MovCtaCteVO o1, MovCtaCteVO o2) {
            return o1.getFecha().compareTo(o2.getFecha());
        }
        
    }
    
    private class ComparadorEmpresa implements Comparator<MovCtaCteVO>{

        @Override
        public int compare(MovCtaCteVO o1, MovCtaCteVO o2) {
            if (o1.getEmpresaNombre().equalsIgnoreCase(o2.getEmpresaNombre())){
                return o1.getFecha().compareTo(o2.getFecha());
            } else {
                return o1.getEmpresaNombre().compareToIgnoreCase(o2.getEmpresaNombre());
            }
        }
        
    }
    
    
    private class ComparadorEmpresaFactura implements Comparator<MovCtaCteVO>{

        @Override
        public int compare(MovCtaCteVO o1, MovCtaCteVO o2) {
            if (o1.getEmpresaNombre().equalsIgnoreCase(o2.getEmpresaNombre())){
                if (o1.getFacturaDescripcion().equals(o1.getFacturaDescripcion())){
                    return o1.getFecha().compareTo(o2.getFecha());
                } else {
                    return o1.getFechaFactura().compareTo(o2.getFechaFactura());
                }
            } else {
                return o1.getEmpresaNombre().compareToIgnoreCase(o2.getEmpresaNombre());
            }
        }
        
    }
    
    
}
