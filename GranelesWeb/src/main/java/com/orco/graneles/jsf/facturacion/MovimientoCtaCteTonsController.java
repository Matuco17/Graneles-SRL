package com.orco.graneles.jsf.facturacion;

import com.orco.graneles.domain.facturacion.Empresa;
import com.orco.graneles.domain.facturacion.MovimientoCtaCteTons;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.facturacion.MovimientoCtaCteTonsFacade;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import com.orco.graneles.reports.MovCtaCteReport;
import com.orco.graneles.reports.MovCtaCteTonsReport;

import java.io.Serializable;
import java.math.BigDecimal;
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

@ManagedBean(name = "movimientoCtaCteTonsController")
@SessionScoped
public class MovimientoCtaCteTonsController implements Serializable {

    public static final String TIPO_DEBITO = "+";
    public static final String TIPO_CREDITO = "-";
    
    private MovimientoCtaCteTons current;
    private DataModel items = null;
    private DataModel currentEmpresaMovimientos;
    
    @EJB
    private MovimientoCtaCteTonsFacade ejbFacade;
    @EJB
    private FixedListFacade fixedListF;
    
    private int selectedItemIndex;
    
    private Empresa currentEmpresa;
    
    
    List<MovimientoCtaCteTons> movimientos = null;
            
    private String tipo;
    private BigDecimal totalDebitos;
    private BigDecimal totalCreditos;
    private BigDecimal monto;
    
    private String urlReporteXEmpresa;
    private String urlReporteXEmpresaYEmbarque;
    
    

    public MovimientoCtaCteTonsController() {
    }

    public void init() {
        recreateModel();
        JsfUtil.minimoRolRequerido(Grupo.ROL_USUARIO);
    }

    public MovimientoCtaCteTons getSelected() {
        if (current == null) {
            current = new MovimientoCtaCteTons();
            selectedItemIndex = -1;
        }
        return current;
    }
    
    public void seleccionarEmpresa(){
        if (currentEmpresa == null){
            currentEmpresaMovimientos = null;
        } else {
            movimientos = ejbFacade.findByEmpresa(currentEmpresa);
            
            totalDebitos = BigDecimal.ZERO;
            totalCreditos = BigDecimal.ZERO;
            for (MovimientoCtaCteTons mCC : movimientos){
                if (mCC.getDebito()!= null){
                    totalDebitos = totalDebitos.add(mCC.getDebito());
                }
                if (mCC.getCredito()!= null){
                    totalCreditos = totalCreditos.add(mCC.getCredito());
                }
            }
            
            currentEmpresaMovimientos = new ListDataModel(movimientos);
            urlReporteXEmpresa = null;
            urlReporteXEmpresaYEmbarque = null;            
        }
    }
    
    public void generarReporteEmpresa(){
        urlReporteXEmpresa =  (new MovCtaCteTonsReport(movimientos, null, null, false, true, false, false, false)).obtenerReportePDF();
    }

    public void generarReporteEmpresaYEmbarque(){
        urlReporteXEmpresaYEmbarque =  (new MovCtaCteTonsReport(movimientos, null, null, false, false, false, false, false)).obtenerReportePDF();
    }

    private MovimientoCtaCteTonsFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        seleccionarEmpresa();
        return "List";
    }

    public String prepareView() {
        seleccionarMovimiento();        
        return "View";
    }

    public String prepareCreate() {
        current = new MovimientoCtaCteTons();
        current.setEmpresa(currentEmpresa);
        monto = BigDecimal.ZERO;
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            prepararParaGuardar(); 
            current.setManual(Boolean.TRUE);
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleFacturacion").getString("MovimientoCtaCteTonsCreated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleFacturacion").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        seleccionarMovimiento();
        return "Edit";
    }

    public String update() {
        try {
            prepararParaGuardar(); 
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleFacturacion").getString("MovimientoCtaCteTonsUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleFacturacion").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (MovimientoCtaCteTons) getCurrentEmpresaMovimientos().getRowData();
        selectedItemIndex = getCurrentEmpresaMovimientos().getRowIndex();
        performDestroy();
        recreateModel();
        seleccionarEmpresa();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        //updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleFacturacion").getString("MovimientoCtaCteTonsDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleFacturacion").getString("PersistenceErrorOccured"));
        }
    }
    
    private void recreateModel() {
        items = null;
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    private void seleccionarMovimiento() {
        current = (MovimientoCtaCteTons) getCurrentEmpresaMovimientos().getRowData();
        selectedItemIndex = getCurrentEmpresaMovimientos().getRowIndex();
        if (current.getValor().doubleValue() >= 0){
            tipo = TIPO_DEBITO;
        } else {
            tipo = TIPO_CREDITO;
        }
        monto = current.getValor().abs();
    }

    private void prepararParaGuardar() {
        if (tipo.equalsIgnoreCase(TIPO_DEBITO)){
            current.setValor(monto.abs());
        }
        if (tipo.equalsIgnoreCase(TIPO_CREDITO)){
            current.setValor(monto.abs().negate());
        }
    }

    @FacesConverter(forClass = MovimientoCtaCteTons.class)
    public static class MovimientoCtaCteControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MovimientoCtaCteTonsController controller = (MovimientoCtaCteTonsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "movimientoCtaCteTonsController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value);
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof MovimientoCtaCteTons) {
                MovimientoCtaCteTons o = (MovimientoCtaCteTons) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Empresa getCurrentEmpresa() {
        return currentEmpresa;
    }

    public void setCurrentEmpresa(Empresa currentEmpresa) {
        this.currentEmpresa = currentEmpresa;
    }


    public DataModel getCurrentEmpresaMovimientos() {
        if (currentEmpresaMovimientos == null){
            currentEmpresaMovimientos = new ListDataModel();
        }
        return currentEmpresaMovimientos;
    }

    public BigDecimal getTotalDebitos() {
        return totalDebitos;
    }

    public BigDecimal getTotalCreditos() {
        return totalCreditos;
    }
    
    public BigDecimal getSaldo(){
        return totalDebitos.subtract(totalCreditos);
    }

    public String getTipo_debito(){
        return TIPO_DEBITO;
    }
    
    public String getTipo_credito(){
        return TIPO_CREDITO;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getUrlReporteXEmpresa() {
        return urlReporteXEmpresa;
    }

    public String getUrlReporteXEmpresaYEmbarque() {
        return urlReporteXEmpresaYEmbarque;
    }
    
    
}