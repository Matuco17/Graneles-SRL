package com.orco.graneles.jsf.carga;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.carga.TrabajadoresTurnoEmbarqueFacade;

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

@ManagedBean(name = "trabajadoresTurnoEmbarqueController")
@SessionScoped
public class TrabajadoresTurnoEmbarqueController implements Serializable {

    private TrabajadoresTurnoEmbarque current;
    private DataModel items = null;
    @EJB
    private TrabajadoresTurnoEmbarqueFacade ejbFacade;
    private int selectedItemIndex;

    public TrabajadoresTurnoEmbarqueController() {
    }

    public void init() {
        recreateModel();
    }

    public TrabajadoresTurnoEmbarque getSelected() {
        if (current == null) {
            current = new TrabajadoresTurnoEmbarque();
            selectedItemIndex = -1;
        }
        return current;
    }

    private TrabajadoresTurnoEmbarqueFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (TrabajadoresTurnoEmbarque) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new TrabajadoresTurnoEmbarque();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("TrabajadoresTurnoEmbarqueCreated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleCarga").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (TrabajadoresTurnoEmbarque) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("TrabajadoresTurnoEmbarqueUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleCarga").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (TrabajadoresTurnoEmbarque) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("TrabajadoresTurnoEmbarqueDeleted"));
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

    @FacesConverter(forClass = TrabajadoresTurnoEmbarque.class)
    public static class TrabajadoresTurnoEmbarqueControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TrabajadoresTurnoEmbarqueController controller = (TrabajadoresTurnoEmbarqueController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "trabajadoresTurnoEmbarqueController");
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
            if (object instanceof TrabajadoresTurnoEmbarque) {
                TrabajadoresTurnoEmbarque o = (TrabajadoresTurnoEmbarque) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }
}