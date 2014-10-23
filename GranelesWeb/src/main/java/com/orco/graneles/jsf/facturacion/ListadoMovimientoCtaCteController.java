package com.orco.graneles.jsf.facturacion;

import com.orco.graneles.domain.facturacion.Empresa;
import com.orco.graneles.domain.facturacion.MovimientoCtaCte;
import com.orco.graneles.domain.miscelaneos.FixedList;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.facturacion.MovimientoCtaCteFacade;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import com.orco.graneles.reports.MovCtaCteReport;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "listadoMovimientoCtaCteController")
@SessionScoped
public class ListadoMovimientoCtaCteController implements Serializable {

    @EJB
    private MovimientoCtaCteFacade ejbFacade;
    @EJB
    private FixedListFacade fixedListF;
    
    private Empresa currentEmpresa;
    private Date currentDesde;
    private Date currentHasta;
    private Boolean agruparXFactura;
    private Boolean agruparXEmpresa;
    private Boolean noMostrarDetalles;
    
    List<MovimientoCtaCte> movimientos = null;
    
    private String urlReporte = null;
    
    public ListadoMovimientoCtaCteController() {
    }

    public void init() {
        JsfUtil.minimoRolRequerido(Grupo.ROL_CONTADOR);
    }

    public void generarReporte(){
        movimientos = ejbFacade.findByEmpresaYFecha(currentEmpresa, currentDesde, currentHasta);
        boolean ocultarEmpresa = !agruparXEmpresa && (currentEmpresa == null);
        
        urlReporte =  (new MovCtaCteReport(movimientos, currentDesde, currentHasta, ocultarEmpresa, !agruparXFactura, noMostrarDetalles, noMostrarDetalles)).obtenerReportePDF();
    }

    public Empresa getCurrentEmpresa() {
        return currentEmpresa;
    }

    public void setCurrentEmpresa(Empresa currentEmpresa) {
        this.currentEmpresa = currentEmpresa;
    }

    public Date getCurrentDesde() {
        return currentDesde;
    }

    public void setCurrentDesde(Date currentDesde) {
        this.currentDesde = currentDesde;
    }

    public Date getCurrentHasta() {
        return currentHasta;
    }

    public void setCurrentHasta(Date currentHasta) {
        this.currentHasta = currentHasta;
    }

    public Boolean getAgruparXFactura() {
        return agruparXFactura;
    }

    public void setAgruparXFactura(Boolean agruparXFactura) {
        this.agruparXFactura = agruparXFactura;
    }

    public Boolean getAgruparXEmpresa() {
        return agruparXEmpresa;
    }

    public void setAgruparXEmpresa(Boolean agruparXEmpresa) {
        this.agruparXEmpresa = agruparXEmpresa;
    }

    public Boolean getNoMostrarDetalles() {
        return noMostrarDetalles;
    }

    public void setNoMostrarDetalles(Boolean noMostrarDetalles) {
        this.noMostrarDetalles = noMostrarDetalles;
    }

    public String getUrlReporte() {
        return urlReporte;
    }

    public void setUrlReporte(String urlReporte) {
        this.urlReporte = urlReporte;
    }

}