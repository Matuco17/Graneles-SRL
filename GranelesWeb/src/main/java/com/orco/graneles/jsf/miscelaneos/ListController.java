package com.orco.graneles.jsf.miscelaneos;

import com.orco.graneles.domain.miscelaneos.FixedList;
import com.orco.graneles.domain.miscelaneos.List;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import com.orco.graneles.model.miscelaneos.ListFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "listController")
@SessionScoped
public class ListController implements Serializable {

    private List current;
    private DataModel items = null;
    @EJB
    private ListFacade ejbFacade;
    @EJB
    private FixedListFacade fixedListF;
    private int selectedItemIndex;

    private Integer currentListId;
    private FixedList newListItem;
    private DataModel listItemsModel;
    private java.util.List<FixedList> listItems;
    
    
    public ListController() {
    }

    public void init() {
        recreateModel();
        
        JsfUtil.minimoRolRequerido(Grupo.ROL_GERENTE);
    }

    public List getSelected() {
        if (current == null) {
            if (currentListId != null){
                current = getFacade().find(currentListId);
            } else  {
                current = new List();
                selectedItemIndex = -1;
            }
            selectedItemIndex = -1;
        }
        return current;
    }

    private ListFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (List) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new List();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleMiscelaneos").getString("ListCreated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleMiscelaneos").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (List) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleMiscelaneos").getString("ListUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleMiscelaneos").getString("PersistenceErrorOccured"));
            return null;
        }
    }
    


    public String destroy() {
        current = (List) getItems().getRowData();
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

    public void agregarNuevo(){
        try {
            if (StringUtils.isNotEmpty(getNewListItem().getDescripcion())){
                fixedListF.create(getNewListItem());
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleMiscelaneos").getString("FixedListDeleted"));
                newListItem = null;
                listItemsModel = null;
                listItems = null;
            } else {
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundleMiscelaneos").getString("FixedListRequiredMessage_descripcion"));
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleMiscelaneos").getString("PersistenceErrorOccured"));
        }
    }
    
    public void borrarElemento(){
        try {
            FixedList fl = (FixedList) getListItemsModel().getRowData();
            fixedListF.remove(fl);
            listItemsModel = null;
            listItems = null;
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleMiscelaneos").getString("FixedListDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleMiscelaneos").getString("PersistenceErrorOccured"));
        }
    }
    
    public void actualizarValores(){
        try {
            for (FixedList fl : getListItems()){
                fixedListF.edit(fl);
            }
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleMiscelaneos").getString("FixedListUpdatedList"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleMiscelaneos").getString("PersistenceErrorOccured"));
        }
    }
    
    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleMiscelaneos").getString("ListDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleMiscelaneos").getString("PersistenceErrorOccured"));
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
        newListItem = null;
        listItems = null;
        listItemsModel = null;
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    @FacesConverter(forClass = List.class)
    public static class ListControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ListController controller = (ListController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "listController");
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
            if (object instanceof List) {
                List o = (List) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }

    public Integer getCurrentListId() {
        return currentListId;
    }

    public void setCurrentListId(Integer currentListId) {
        this.currentListId = currentListId;
    }

    public FixedList getNewListItem() {
        if (newListItem == null){
            newListItem = new FixedList();
            newListItem.setLista(getSelected());
        }
        return newListItem;
    }

    public void setNewListItems(FixedList newListItem) {
        this.newListItem = newListItem;
    }

    public DataModel getListItemsModel() {
        if (listItemsModel == null){
            listItemsModel = new ListDataModel(getListItems());
        }
        return listItemsModel;
    }

    public java.util.List<FixedList> getListItems() {
        if (listItems == null && current != null){
            listItems = new ArrayList<FixedList>(fixedListF.findByLista(current.getId()));
            Collections.sort(listItems);
        }        
        return listItems;
    }

   

    
    
}