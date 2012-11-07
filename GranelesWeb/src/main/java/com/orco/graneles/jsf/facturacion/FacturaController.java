package com.orco.graneles.jsf.facturacion;

import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.facturacion.Factura;
import com.orco.graneles.domain.facturacion.LineaFactura;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.carga.CargaTurnoFacade;
import com.orco.graneles.model.facturacion.FacturaFacade;
import com.orco.graneles.model.facturacion.LineaFacturaFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
import org.primefaces.event.FlowEvent;
import org.primefaces.model.DualListModel;

@ManagedBean(name = "facturaController")
@SessionScoped
public class FacturaController implements Serializable {

    private Factura current;
    private DataModel items = null;
    @EJB
    private FacturaFacade ejbFacade;
    @EJB
    private CargaTurnoFacade cargaTurnoF;
    @EJB
    private LineaFacturaFacade lineaFacturaF;
    
    private Boolean tabBusquedaAbierto;
    private Boolean tabSeleccionPlanillasAbrierto;
    private Boolean tabCalculoAbierto;
    private Boolean tabCalculadoraAbierto;
    
    private int selectedItemIndex;
    
    private DualListModel<CargaTurno> turnosASeleccionarModel;
    private DataModel lineasFacturaModel;
    private List<LineaFactura> lineasFactura;

    public FacturaController() {
    }

    public void init() {
        recreateModel();
        
        JsfUtil.minimoRolRequerido(Grupo.ROL_USUARIO);
    }
    
    public static final String STEP_SELECCION_EMBARQUE = "seleccionEmbarqueStep";
    public static final String STEP_SELECCION_TURNOS = "seleccionTurnosStep";
    public static final String STEP_SETEO_VALORES = "seteoValoresStep";
    public static final String STEP_CONFIRMAR = "confirmarStep";
    
     public String onFlowProcess(FlowEvent event) {  
        
        if (event.getOldStep().equals(STEP_SELECCION_EMBARQUE) && event.getNewStep().equals(STEP_SELECCION_TURNOS)){
            seleccionarEmbarqueYProveedor();
        } else if (event.getOldStep().equals(STEP_SELECCION_TURNOS) && event.getNewStep().equals(STEP_SETEO_VALORES)){
            seleccionarCargaTurnos();
        } else if (event.getOldStep().equals(STEP_SETEO_VALORES) && event.getNewStep().equals(STEP_CONFIRMAR)){
            //POR AHORA NO HAGO NADA
            current.setExportador(getSelected().getExportador());
            current.setEmbarque(getSelected().getEmbarque());
        }
        
        return event.getNewStep();  
    }  

    public void seleccionarEmbarqueYProveedor(){
        if (getSelected().getExportador() == null){
            JsfUtil.addErrorMessage("Seleccione un Exportador");
        }
        
        if (getSelected().getEmbarque() == null){
            JsfUtil.addErrorMessage("Seleccione un Embarque");
        }
        
        if (getSelected().getExportador() != null && getSelected().getEmbarque() != null){
            turnosASeleccionarModel = new DualListModel<CargaTurno>();
            List<CargaTurno> cargaTurnosDelExportador = cargaTurnoF.obtenerCargas(getSelected().getEmbarque(), getSelected().getExportador());
            Collections.sort(cargaTurnosDelExportador);
            turnosASeleccionarModel.setSource(new ArrayList<CargaTurno>(cargaTurnosDelExportador));
            turnosASeleccionarModel.setTarget(new ArrayList<CargaTurno>());
            
            
            lineasFacturaModel = null;
            lineasFactura = null;
        }        
    }
    
    public void seleccionarCargaTurnos(){
        if (turnosASeleccionarModel != null){
            lineasFactura = lineaFacturaF.crearLineas(turnosASeleccionarModel.getTarget(), getSelected());
            lineasFacturaModel = new ListDataModel(lineasFactura);
        }
    }
    
    public void actualizarLineas(){
        lineaFacturaF.actualizarLineas(lineasFactura);
    }
    
    public Factura getSelected() {
        if (current == null) {
            current = new Factura();
            selectedItemIndex = -1;
        }
        return current;
    }
    
    private FacturaFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Factura) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Factura();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleFacturacion").getString("FacturaCreated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleFacturacion").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Factura) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleFacturacion").getString("FacturaUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleFacturacion").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Factura) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        performDestroy();
        recreateModel();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleFacturacion").getString("FacturaDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleFacturacion").getString("PersistenceErrorOccured"));
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = new ListDataModel(getFacade().findAll());;
        }
        return items;
    }

    private void recreateModel() {
        items = null;
        turnosASeleccionarModel = null;
        lineasFacturaModel = null;
        lineasFactura = null;
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    @FacesConverter(forClass = Factura.class)
    public static class FacturaControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            FacturaController controller = (FacturaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "facturaController");
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
            if (object instanceof Factura) {
                Factura o = (Factura) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }

    public DualListModel<CargaTurno> getTurnosASeleccionarModel() {
        return turnosASeleccionarModel;
    }

    public void setTurnosASeleccionarModel(DualListModel<CargaTurno> turnosASeleccionarModel) {
        this.turnosASeleccionarModel = turnosASeleccionarModel;
    }
    
    public DataModel getLineasFacturaModel() {
        return lineasFacturaModel;
    }

    public Boolean getTabBusquedaAbierto() {
        return tabBusquedaAbierto;
    }

    public void setTabBusquedaAbierto(Boolean tabBusquedaAbierto) {
        this.tabBusquedaAbierto = tabBusquedaAbierto;
    }

    public Boolean getTabSeleccionPlanillasAbrierto() {
        return tabSeleccionPlanillasAbrierto;
    }

    public void setTabSeleccionPlanillasAbrierto(Boolean tabSeleccionPlanillasAbrierto) {
        this.tabSeleccionPlanillasAbrierto = tabSeleccionPlanillasAbrierto;
    }

    public Boolean getTabCalculoAbierto() {
        return tabCalculoAbierto;
    }

    public void setTabCalculoAbierto(Boolean tabCalculoAbierto) {
        this.tabCalculoAbierto = tabCalculoAbierto;
    }

    public Boolean getTabCalculadoraAbierto() {
        return tabCalculadoraAbierto;
    }

    public void setTabCalculadoraAbierto(Boolean tabCalculadoraAbierto) {
        this.tabCalculadoraAbierto = tabCalculadoraAbierto;
    }

    public String getSTEP_SELECCION_EMBARQUE() {
        return STEP_SELECCION_EMBARQUE;
    }

    public String getSTEP_SELECCION_TURNOS() {
        return STEP_SELECCION_TURNOS;
    }

    public String getSTEP_SETEO_VALORES() {
        return STEP_SETEO_VALORES;
    }

    public String getSTEP_CONFIRMAR() {
        return STEP_CONFIRMAR;
    }
    
    
}