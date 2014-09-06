package com.orco.graneles.jsf.facturacion;

import com.orco.graneles.domain.facturacion.Empresa;
import com.orco.graneles.domain.facturacion.MovimientoCtaCteTons;
import com.orco.graneles.domain.miscelaneos.FixedList;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.facturacion.MovimientoCtaCteTonsFacade;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import com.orco.graneles.reports.MovCtaCteReport;
import com.orco.graneles.reports.MovCtaCteTonsReport;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "listadoMovimientoCtaCteTonsController")
@SessionScoped
public class ListadoMovimientoCtaCteTonsController implements Serializable {

    @EJB
    private MovimientoCtaCteTonsFacade ejbFacade;
    @EJB
    private FixedListFacade fixedListF;
    
    private Empresa currentEmpresa;
    private Date currentDesde;
    private Date currentHasta;
    private Boolean agruparXTipoTurno;
    private Boolean agruparXEmpresa;
    private Boolean agruparXEmbarque;
    private Boolean noMostrarDetalles;
    private FixedList tipoMovimiento;
    
    List<MovimientoCtaCteTons> movimientos = null;
    
    private String urlReporte = null;
    
    public ListadoMovimientoCtaCteTonsController() {
    }

    public void init() {
        JsfUtil.minimoRolRequerido(Grupo.ROL_CONTADOR);
    }

    public void generarReporte(){
        movimientos = ejbFacade.findByEmpresaYFecha(currentEmpresa, currentDesde, currentHasta);
        boolean ocultarEmpresa = !agruparXEmpresa && (currentEmpresa == null);
        
        urlReporte =  (new MovCtaCteTonsReport(movimientos, currentDesde, currentHasta, ocultarEmpresa, !agruparXEmbarque, !agruparXTipoTurno, noMostrarDetalles, noMostrarDetalles)).obtenerReportePDF();
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

    public Boolean getAgruparXTipoTurno() {
        return agruparXTipoTurno;
    }

    public void setAgruparXTipoTurno(Boolean agruparXTipoTurno) {
        this.agruparXTipoTurno = agruparXTipoTurno;
    }

    public Boolean getAgruparXEmpresa() {
        return agruparXEmpresa;
    }

    public void setAgruparXEmpresa(Boolean agruparXEmpresa) {
        this.agruparXEmpresa = agruparXEmpresa;
    }

    public Boolean getAgruparXEmbarque() {
        return agruparXEmbarque;
    }

    public void setAgruparXEmbarque(Boolean agruparXEmbarque) {
        this.agruparXEmbarque = agruparXEmbarque;
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