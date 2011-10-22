package com.orco.graneles.jsf.personal;

import com.orco.graneles.domain.personal.Accidentado;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.personal.AccidentadoFacade;

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

    private AccidentadoFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Accidentado) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Accidentado();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundlePersonal").getString("AccidentadoCreated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundlePersonal").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Accidentado) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundlePersonal").getString("AccidentadoUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundlePersonal").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Accidentado) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundlePersonal").getString("AccidentadoDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundlePersonal").getString("PersistenceErrorOccured"));
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
}