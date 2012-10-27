package com.orco.graneles.jsf.carga;

import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.carga.CargaTurnoFacade;
import java.io.IOException;

import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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

@ManagedBean(name = "cargaTurnoController")
@SessionScoped
public class CargaTurnoController implements Serializable {

    private CargaTurno current;
    private DataModel items = null;
    @EJB
    private CargaTurnoFacade ejbFacade;
    private int selectedItemIndex;

    public CargaTurnoController() {
    }

    public void init() {
        recreateModel();
        
        JsfUtil.minimoRolRequerido(null);
    }

    public CargaTurno getSelected() {
        if (current == null) {
            current = new CargaTurno();
            selectedItemIndex = -1;
        }
        return current;
    }

    private CargaTurnoFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (CargaTurno) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new CargaTurno();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("CargaTurnoCreated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleCarga").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (CargaTurno) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("CargaTurnoUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleCarga").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (CargaTurno) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("CargaTurnoDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleCarga").getString("PersistenceErrorOccured"));
        }
    }
    /*
    private void updateCurrentItem() {
    int count = getFacade().count();
    if (selectedItemIndex >= count) {
    // selected index cannot be bigger than number of items:
    selectedItemIndex = count-1;
    // go to previous page if last page disappeared:
    if (pagination.getPageFirstItem() >= count) {
    pagination.previousPage();
    }
    }
    if (selectedItemIndex >= 0) {
    current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex+1}).get(0);
    }
    }
     */

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

    @FacesConverter(forClass = CargaTurno.class)
    public static class CargaTurnoControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CargaTurnoController controller = (CargaTurnoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "cargaTurnoController");
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
            if (object instanceof CargaTurno) {
                CargaTurno o = (CargaTurno) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }
    
    
    public CargaTurnoControllerConverter getStaticConverter(){
        return new CargaTurnoControllerConverter();
    }
}