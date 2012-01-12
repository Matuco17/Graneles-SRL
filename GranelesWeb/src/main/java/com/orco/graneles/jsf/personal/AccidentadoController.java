package com.orco.graneles.jsf.personal;

import com.orco.graneles.domain.personal.Accidentado;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.personal.AccidentadoFacade;
import com.orco.graneles.vo.NuevoAccidentadoVO;

import java.io.Serializable;
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

@ManagedBean(name = "accidentadoController")
@SessionScoped
public class AccidentadoController implements Serializable {

    private Accidentado current;
    private Personal currentPersonal;
    private NuevoAccidentadoVO currentV0;
    private DataModel items = null;
    @EJB
    private AccidentadoFacade ejbFacade;
    private int selectedItemIndex;

    public AccidentadoController() {
    }

    public void init() {
        recreateModel();
    }

    public Accidentado getSelected() {
        if (current == null) {
            current = new Accidentado();
            selectedItemIndex = -1;
        }
        return current;
    }
    
    public void setSelected(Accidentado selected){
        current = selected;
    }
    
    public NuevoAccidentadoVO getSelectedVO(){
        if (currentV0 == null) {
            currentV0 = new NuevoAccidentadoVO(null, getSelected());
        }
        return currentV0;
    }

    public void crearDatosAccidentado(){
        if (currentPersonal != null){
            currentV0.getAccidentado().setPersonal(currentPersonal);
            currentV0 = ejbFacade.calcularNuevoAccidentado(currentV0.getAccidentado());
        }
    }
    
    private AccidentadoFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        //current = (Accidentado) getItems().getRowData();
        //selectedItemIndex = getItems().getRowIndex();
        if (current != null){
        return "View";
        } else {
            return null;
        }
    }

    public String prepareCreate() {
        currentV0 = null;
        selectedItemIndex = -1;
        return "Create";
    }
    
    public String prepareEdit() {
        if (current != null){
            //current = (Accidentado) getItems().getRowData();
            currentV0 = ejbFacade.calcularNuevoAccidentado(current);
            //selectedItemIndex = getItems().getRowIndex();
            return "Edit";
        } else {
            return null;
        }
    }
    
    public boolean validarCreateUpdate(){
        if (getSelectedVO().getAccidentado().getPersonal() == null){
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundlePersonal").getString("AccidentadoRequiredMessage_personal"));
            return false;
        }
        if (getSelectedVO().getAccidentado().getTarea() == null){
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundlePersonal").getString("AccidentadoRequiredMessage_tarea"));
            return false;
        }
        if (getSelectedVO().getAccidentado().getCategoria() == null){
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundlePersonal").getString("AccidentadoRequiredMessage_categoria"));
            return false;
        }
        if (getSelectedVO().getAccidentado().getBruto() == null){
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundlePersonal").getString("AccidentadoRequiredMessage_bruto"));
            return false;
        }
        //Paso todas la validaciones
        return true;
    }
    
    public String create() {
        try {
            if (validarCreateUpdate()){
                getFacade().create(currentV0.getAccidentado());
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundlePersonal").getString("AccidentadoCreated"));
                return "View";
            } else {
                return null;
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundlePersonal").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String update() {
        try {
            if (validarCreateUpdate()){
                getFacade().edit(currentV0.getAccidentado());
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundlePersonal").getString("AccidentadoUpdated"));
                return "View";
            } else {
                return null;
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundlePersonal").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        if (current != null){
            //current = (Accidentado) getItems().getRowData();
            //selectedItemIndex = getItems().getRowIndex();
            performDestroy();
            recreateModel();
            return "List";
        } else {
            return null;
        }
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundlePersonal").getString("AccidentadoDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundlePersonal").getString("PersistenceErrorOccured"));
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
        currentPersonal = null;
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    @FacesConverter(forClass = Accidentado.class)
    public static class AccidentadoControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AccidentadoController controller = (AccidentadoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "accidentadoController");
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
            if (object instanceof Accidentado) {
                Accidentado o = (Accidentado) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }

    public Personal getCurrentPersonal() {
        return currentPersonal;
    }

    public void setCurrentPersonal(Personal currentPersonal) {
        this.currentPersonal = currentPersonal;
    }
    
    
}