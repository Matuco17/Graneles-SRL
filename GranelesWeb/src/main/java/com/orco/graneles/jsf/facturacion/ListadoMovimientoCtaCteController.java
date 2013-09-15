package com.orco.graneles.jsf.facturacion;

import com.orco.graneles.domain.facturacion.Empresa;
import com.orco.graneles.domain.facturacion.MovimientoCtaCte;
import com.orco.graneles.domain.miscelaneos.FixedList;
import com.orco.graneles.domain.miscelaneos.TipoValorMovimientoCtaCte;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.facturacion.MovimientoCtaCteFacade;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import com.orco.graneles.reports.MovCtaCteReport;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

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
    private FixedList tipoMovimiento;
    
    List<MovimientoCtaCte> movimientos = null;
    
    private String urlReporte = null;
    
    public ListadoMovimientoCtaCteController() {
    }

    public void init() {
        JsfUtil.minimoRolRequerido(Grupo.ROL_USUARIO);
    }

    public void generarReporte(){
        movimientos = ejbFacade.findByEmpresaYFechaYTipoValor(currentEmpresa, currentDesde, currentHasta, tipoMovimiento);
        boolean esDinero = tipoMovimiento.getId().equals(TipoValorMovimientoCtaCte.DINERO);
        boolean ocultarEmpresa = !agruparXEmpresa && (currentEmpresa == null);
        
        urlReporte =  (new MovCtaCteReport(movimientos, currentDesde, currentHasta, ocultarEmpresa, !agruparXFactura, noMostrarDetalles, noMostrarDetalles, esDinero)).obtenerReportePDF();
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

    public FixedList getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(FixedList tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public String getUrlReporte() {
        return urlReporte;
    }

    public void setUrlReporte(String urlReporte) {
        this.urlReporte = urlReporte;
    }

    
    
    
}