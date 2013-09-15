package com.orco.graneles.jsf.salario;

import com.orco.graneles.domain.miscelaneos.TipoConceptoRecibo;
import com.orco.graneles.domain.miscelaneos.TipoRecibo;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.ConceptoRecibo;
import com.orco.graneles.domain.salario.ItemsReciboManual;
import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.domain.salario.ReciboManual;
import com.orco.graneles.domain.salario.TipoJornal;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import com.orco.graneles.model.salario.ConceptoReciboFacade;
import com.orco.graneles.model.salario.PeriodoFacade;
import com.orco.graneles.model.salario.ReciboManualFacade;
import com.orco.graneles.model.salario.SueldoFacade;
import com.orco.graneles.model.salario.TipoJornalFacade;
import com.orco.graneles.reports.RecibosSueldoReport;

import java.io.Serializable;
import java.util.ArrayList;
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
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "reciboManualController")
@SessionScoped
public class ReciboManualController implements Serializable {

    private ReciboManual current;
    private DataModel items = null;
    @EJB
    private ReciboManualFacade ejbFacade;
    @EJB
    private ConceptoReciboFacade conceptoReciboF;
    @EJB
    private FixedListFacade fixedListF;
    @EJB
    private PeriodoFacade periodoF;
    @EJB
    private SueldoFacade sueldoF;
    
    private Integer selectedItemIndex;
    
    private Integer mes;
    private Integer anio;
    private List<ItemsReciboManual> itemsRecibo;
    private DataModel itemsReciboModel;
    private ItemsReciboManual newItemRecibo;
    
    private List<ConceptoRecibo> conceptosPersonal;
    
    private String urlRecibo;

    public ReciboManualController() {
    }

    public void init() {
        recreateModel();
        
        JsfUtil.minimoRolRequerido(Grupo.ROL_USUARIO);
    }

    public ReciboManual getSelected() {
        if (current == null) {
            current = new ReciboManual();
            selectedItemIndex = -1;
        }
        return current;
    }

    private ReciboManualFacade getFacade() {
        return ejbFacade;
    }

    public void seleccionarPersonal() {  
        Personal currentPersonal = current.getPersonal();
        
        if (currentPersonal == null){
            conceptosPersonal = null;
        } else {
            conceptosPersonal = conceptoReciboF.obtenerConceptos(currentPersonal.getTipoRecibo(), fixedListF.find(TipoConceptoRecibo.REMUNERATIVO));
            conceptosPersonal.addAll(conceptoReciboF.obtenerConceptos(currentPersonal.getTipoRecibo(), fixedListF.find(TipoConceptoRecibo.NO_REMUNERATIVO)));
        }
        itemsRecibo = null;
        itemsReciboModel = null;
    }  
    
    public void agregarItemRecibo(){
        if (newItemRecibo.getConceptoRecibo() != null && newItemRecibo.getValor() != null){
            newItemRecibo.setRecibo(current);
            getItemsRecibo().add(newItemRecibo);
            
            itemsReciboModel = null;
            newItemRecibo = null;
        }
    }
    
    public void borrarItemRecibo() {
        getItemsRecibo().remove(getItemsReciboModel().getRowIndex());
        itemsReciboModel = null;
    }
    
    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        reciboSeleccionado();
        generarRecibo();
        
        return "View";
    }

    public String prepareCreate() {
        current = new ReciboManual();
        itemsRecibo = null;
        itemsReciboModel = null;
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            Periodo currentPeriodo = periodoF.verPeriodo(anio, mes);
            if (currentPeriodo.getId() == null){
                periodoF.persist(currentPeriodo);
            }
            
            current.setPeriodo(currentPeriodo);
            
            current.setItemsReciboManualCollection(itemsRecibo);
            
            getFacade().create(current);
            
            generarRecibo();
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("ReciboManualCreated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleSalario").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        reciboSeleccionado();
        return "Edit";
    }
    
    private void reciboSeleccionado(){
        current = (ReciboManual) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        
        Personal currentPersonal = current.getPersonal();
        conceptosPersonal = conceptoReciboF.obtenerConceptos(currentPersonal.getTipoRecibo(), fixedListF.find(TipoConceptoRecibo.REMUNERATIVO));
        conceptosPersonal.addAll(conceptoReciboF.obtenerConceptos(currentPersonal.getTipoRecibo(), fixedListF.find(TipoConceptoRecibo.NO_REMUNERATIVO)));
     
        itemsRecibo = new ArrayList<ItemsReciboManual>(current.getItemsReciboManualCollection());
        itemsReciboModel = null;
    }

    public String update() {
        try {
            current.setItemsReciboManualCollection(itemsRecibo);
            
            getFacade().edit(current);
            
            generarRecibo();
            
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("ReciboManualUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleSalario").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (ReciboManual) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("ReciboManualDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleSalario").getString("PersistenceErrorOccured"));
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
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    private void generarRecibo() {
        if (current.getItemsReciboManualCollection().size() > 0){
            urlRecibo = (new RecibosSueldoReport(
                    current.getPeriodo(), 
                    sueldoF.sueldoManual(current, null), 
                    true)).obtenerReportePDF();
        } else {
            urlRecibo = null;
        }
    }
  

    @FacesConverter(forClass = ReciboManual.class)
    public static class ReciboManualControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ReciboManualController controller = (ReciboManualController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "reciboManualController");
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
            if (object instanceof ReciboManual) {
                ReciboManual o = (ReciboManual) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }

    public Integer getMes() {
        if (mes == null){
            mes = (new DateTime()).getMonthOfYear(); 
        }
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAnio() {
        if (anio == null){
            anio = (new DateTime()).getYear();
        }
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public List<ItemsReciboManual> getItemsRecibo() {
        if (itemsRecibo == null){
            itemsRecibo = new ArrayList<ItemsReciboManual>();
        }
        return itemsRecibo;
    }

    public DataModel getItemsReciboModel() {
        if (itemsReciboModel == null){
            itemsReciboModel = new ListDataModel(getItemsRecibo());
        }
        return itemsReciboModel;
    }

    public ItemsReciboManual getNewItemRecibo() {
        if (newItemRecibo == null){
            newItemRecibo = new ItemsReciboManual();
        }
        return newItemRecibo;
    }

    public List<ConceptoRecibo> getConceptosPersonal() {
        if (current.getPersonal() == null){
            conceptosPersonal = new ArrayList<ConceptoRecibo>();
        } 
        return conceptosPersonal;
    }

    public String getUrlRecibo() {
        return urlRecibo;
    }
    
    
}