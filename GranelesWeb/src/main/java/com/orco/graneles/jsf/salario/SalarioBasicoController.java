package com.orco.graneles.jsf.salario;

import com.orco.graneles.domain.salario.SalarioBasico;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.salario.SalarioBasicoFacade;

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

@ManagedBean(name = "salarioBasicoController")
@SessionScoped
public class SalarioBasicoController implements Serializable {

    private SalarioBasico current;
    private DataModel items = null;
    @EJB
    private SalarioBasicoFacade ejbFacade;
    private int selectedItemIndex;

    public SalarioBasicoController() {
    }

    public void init() {
        recreateModel();
    }

    public SalarioBasico getSelected() {
        if (current == null) {
            current = new SalarioBasico();
            selectedItemIndex = -1;
        }
        return current;
    }
    
    public void setSelected(SalarioBasico selected){
        current = selected;
    }

    private SalarioBasicoFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        if (current != null){
            //current = (SalarioBasico) getItems().getRowData();
            //selectedItemIndex = getItems().getRowIndex();
            return "View";
        } else {
            return null;
        }
    }

    public String prepareCreate() {
        current = new SalarioBasico();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("SalarioBasicoCreated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleSalario").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        if (current != null){
            //current = (SalarioBasico) getItems().getRowData();
            //selectedItemIndex = getItems().getRowIndex();
            return "Edit";
        } else {
            return null;
        }
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("SalarioBasicoUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleSalario").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        if (current != null){
        //current = (SalarioBasico) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("SalarioBasicoDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleSalario").getString("PersistenceErrorOccured"));
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

    @FacesConverter(forClass = SalarioBasico.class)
    public static class SalarioBasicoControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SalarioBasicoController controller = (SalarioBasicoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "salarioBasicoController");
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
            if (object instanceof SalarioBasico) {
                SalarioBasico o = (SalarioBasico) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }
}