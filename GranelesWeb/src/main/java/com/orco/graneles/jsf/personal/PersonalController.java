package com.orco.graneles.jsf.personal;

import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.personal.PersonalFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

@ManagedBean(name = "personalController")
@SessionScoped
public class PersonalController implements Serializable {

    private Personal current;
    private DataModel items = null;
    @EJB
    private PersonalFacade ejbFacade;
    private int selectedItemIndex;
    
    private List<Personal> todoElPersonal;

    public PersonalController() {
    }

    public void init() {
        recreateModel();
    }

    public Personal getSelected() {
        if (current == null) {
            current = new Personal();
            selectedItemIndex = -1;
        }
        return current;
    }

    private PersonalFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Personal) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Personal();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundlePersonal").getString("PersonalCreated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundlePersonal").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Personal) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundlePersonal").getString("PersonalUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundlePersonal").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Personal) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundlePersonal").getString("PersonalDeleted"));
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
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public List<Personal> getPersonalOrdenadoXCuil(){
        List<Personal> lista = ejbFacade.findAll();
        Collections.sort(lista, new ComparadorPersonalXCuil());
        return lista;
    }
    
    public List<Personal> completePersonal(String query) {  
        List<Personal> suggestions = new ArrayList<Personal>();  
          
        for(Personal p : getTodoElPersonal()) {  
            if(p.getCuil().contains(query) || p.getApellido().contains(query))  
                suggestions.add(p);  
        }  
          
        return suggestions;  
    }  
    
    
    @FacesConverter(forClass = Personal.class)
    public static class PersonalControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PersonalController controller = (PersonalController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "personalController");
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
            if (object instanceof Personal) {
                Personal o = (Personal) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }

    public List<Personal> getTodoElPersonal() {
        if (todoElPersonal == null){
            todoElPersonal = ejbFacade.findAll();
            Collections.sort(todoElPersonal, new ComparadorPersonalXCuil());
        }
        return todoElPersonal;
    }
    
    
    
    private class ComparadorPersonalXCuil implements Comparator<Personal>{

        @Override
        public int compare(Personal o1, Personal o2) {
            return o1.getCuil().compareTo(o2.getCuil());
        }
        
    }
}