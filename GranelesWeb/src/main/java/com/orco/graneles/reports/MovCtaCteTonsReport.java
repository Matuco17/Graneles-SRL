/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.facturacion.MovimientoCtaCte;
import com.orco.graneles.domain.facturacion.MovimientoCtaCteTons;
import com.orco.graneles.vo.MovCtaCteTonsVO;
import com.orco.graneles.vo.MovCtaCteVO;
import java.util.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author orco
 */
public class MovCtaCteTonsReport extends ReporteGenerico {

    private List<MovCtaCteTonsVO> movimientoVOs;
    private Date desde;
    private Date hasta;
    private Boolean ocultarEmbarques;
    private Boolean ocultarEmpresas;
    private Boolean ocultarTipoTurno;
    private Boolean ocultarTotales;
    private Boolean ocultarDetalles;
    
    
    @Override
    protected String[] getUrlImagenes() {
        return new String[]{"logoReducido.jpg"};
    }
    
    public MovCtaCteTonsReport(List<MovimientoCtaCteTons> movimientos, Date desde, Date hasta, Boolean ocultarEmpresas, Boolean ocultarEmbarques, Boolean ocultarTipoTurno, Boolean ocultarTotales, Boolean ocultarDetalles){
        this.desde = desde;
        this.hasta = hasta;
        this.ocultarEmpresas = ocultarEmpresas;
        this.ocultarEmbarques = ocultarEmbarques;
        this.ocultarTipoTurno = ocultarTipoTurno;
        this.ocultarDetalles = ocultarDetalles;
        this.ocultarTotales = ocultarTotales;
    
        Comparator<MovCtaCteTonsVO> comparador = null;
        
        if (ocultarEmpresas && ocultarEmbarques) {
            comparador = new ComparadorTotal();
        } else if (ocultarEmbarques) {
            comparador = new ComparadorEmpresa();
        } else if (ocultarTipoTurno) {
            comparador = new ComparadorTipoTurno();
        } else {
            comparador = new ComparadorEmpresaEmbarque();
        }
    
        movimientoVOs = new ArrayList<MovCtaCteTonsVO>();
    
        for (MovimientoCtaCteTons mCC : movimientos){
            movimientoVOs.add(new MovCtaCteTonsVO(mCC));
        }
    
        Collections.sort(movimientoVOs, comparador);
    }
    
    @Override
    public String obtenerReportePDF() {
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(movimientoVOs);
        
        params.put("desde", desde);
        params.put("hasta", hasta);
        params.put("ocultarEmpresas", ocultarEmpresas);
        params.put("ocultarEmbarques", ocultarEmbarques);
        params.put("ocultarTipoTurno", ocultarTipoTurno);
        params.put("ocultarDetalles", ocultarDetalles);
        params.put("ocultarTotales", ocultarTotales);
        
        
        return printGenerico(ds, "ResumenCtaCteTonsXEmbarque", "ResumenCuentaCorrienteTons_"+ (new Date()).getTime());
    }

   
    private class ComparadorTotal implements Comparator<MovCtaCteTonsVO>{

        @Override
        public int compare(MovCtaCteTonsVO o1, MovCtaCteTonsVO o2) {
            return o1.getFecha().compareTo(o2.getFecha());
        }
        
    }
    
    private class ComparadorEmpresa implements Comparator<MovCtaCteTonsVO>{

        @Override
        public int compare(MovCtaCteTonsVO o1, MovCtaCteTonsVO o2) {
            if (o1.getEmpresaNombre().equalsIgnoreCase(o2.getEmpresaNombre())){
                return o1.getFecha().compareTo(o2.getFecha());
            } else {
                return o1.getEmpresaNombre().compareToIgnoreCase(o2.getEmpresaNombre());
            }
        }
        
    }
    
    private class ComparadorTipoTurno implements Comparator<MovCtaCteTonsVO>{

        @Override
        public int compare(MovCtaCteTonsVO o1, MovCtaCteTonsVO o2) {
            if (o1.getTipoTurno().equalsIgnoreCase(o2.getTipoTurno())){
                return o1.getFecha().compareTo(o2.getFecha());
            } else {
                return o1.getTipoTurno().compareToIgnoreCase(o2.getTipoTurno());
            }
        }
        
    }
    
    
    private class ComparadorEmpresaEmbarque implements Comparator<MovCtaCteTonsVO>{

        @Override
        public int compare(MovCtaCteTonsVO o1, MovCtaCteTonsVO o2) {
            if (o1.getEmpresaNombre().equalsIgnoreCase(o2.getEmpresaNombre())){
                if (o1.getEmbarqueDescripcion().equals(o1.getEmbarqueDescripcion())){
                    return o1.getFecha().compareTo(o2.getFecha());
                } else {
                    return o1.getFechaTurnoEmbarque().compareTo(o2.getFechaTurnoEmbarque());
                }
            } else {
                return o1.getEmpresaNombre().compareToIgnoreCase(o2.getEmpresaNombre());
            }
        }
        
    }
    
    
}
