package com.orco.graneles.jsf.salario;

import com.orco.graneles.domain.salario.MinimoVitalMovilHora;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.NegocioException;
import com.orco.graneles.model.salario.MinimoVitalMovilHoraFacade;

import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@ManagedBean(name = "minimoVitalMovilHoraController")
@SessionScoped
public class MinimoViltaMovilHoraController implements Serializable {

    private MinimoVitalMovilHora current;
    private DataModel items = null;
    @EJB
    private MinimoVitalMovilHoraFacade ejbFacade;
    private int selectedItemIndex;

    public MinimoViltaMovilHoraController() {
    }

    public void init() {
        recreateModel();
        
        JsfUtil.minimoRolRequerido(Grupo.ROL_GERENTE);
    }

    public MinimoVitalMovilHora getSelected() {
        if (current == null) {
            current = new MinimoVitalMovilHora();
            selectedItemIndex = -1;
        }
        return current;
    }
    
    public void setSelected(MinimoVitalMovilHora selected){
        current = selected;
    }

    private MinimoVitalMovilHoraFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        if (current != null){
            //current = (MinimoVitalMovilHora) getItems().getRowData();
            //selectedItemIndex = getItems().getRowIndex();
            return "View";
        } else {
            return null;
        }
    }

    public String prepareCreate() {
        current = new MinimoVitalMovilHora();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("MinimoVitalMovilHoraCreated"));
            return "View";
        } catch (EJBException e) {
            JsfUtil.addErrorMessage(e, e.getCause().getMessage());
            return null;        
        } 
    }

    public String prepareEdit() {
        if (current != null){
            //current = (MinimoVitalMovilHora) getItems().getRowData();
            //selectedItemIndex = getItems().getRowIndex();
            return "Edit";
        } else {
            return null;
        }
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("MinimoVitalMovilHoraUpdated"));
            return "View";
        } catch (EJBException e) {
            JsfUtil.addErrorMessage(e, e.getCause().getMessage());
            return null;        
        } 
    }

    public String destroy() {
        if (current != null){
        //current = (MinimoVitalMovilHora) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("MinimoVitalMovilHoraDeleted"));
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

    @FacesConverter(forClass = MinimoVitalMovilHora.class)
    public static class MinimoVitalMovilHoraControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MinimoViltaMovilHoraController controller = (MinimoViltaMovilHoraController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "MinimoVitalMovilHoraController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value);
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof MinimoVitalMovilHora) {
                MinimoVitalMovilHora o = (MinimoVitalMovilHora) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }
}