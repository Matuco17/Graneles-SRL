package com.orco.graneles.jsf.carga;

import com.orco.graneles.domain.carga.Bodega;
import com.orco.graneles.domain.carga.Buque;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.carga.BuqueFacade;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
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

@ManagedBean(name = "buqueController")
@SessionScoped
public class BuqueController implements Serializable {

    private Buque current;
    private DataModel items = null;
    @EJB
    private BuqueFacade ejbFacade;
    private int selectedItemIndex;
    private List<Bodega> bodegas;
    private DataModel bodegasModel;
    private static final int CANTIDAD_BODEGAS = 9;
    
    
    public BuqueController() {
    }

    public void init() {
        recreateModel();
    }

    public Buque getSelected() {
        if (current == null) {
            current = new Buque();
            selectedItemIndex = -1;
        }
        return current;
    }

    public void setSelected(Buque selected){
        current = selected;
    }
    
    private BuqueFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        if (current != null){
            //current = (Buque) getItems().getRowData();
            //selectedItemIndex = getItems().getRowIndex();
            return "View";
        } else {
            return null;
        }
    }

    public String prepareCreate() {
        current = new Buque();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            //asignarBodegas();
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("BuqueCreated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleCarga").getString("PersistenceErrorOccured"));
            return null;
        }
    }
/*
    private void asignarBodegas(){
        //Asigno las bodegas
        for (Bodega bodega : getBodegas()){
            bodega.setBuque(current);
        }
        current.setBodegaCollection(bodegas);
    }
*/    
    public String prepareEdit() {
        if (current != null){
            //current = (Buque) getItems().getRowData();
            //selectedItemIndex = getItems().getRowIndex();
            return "Edit";
        } else {
            return null;
        }
    }

    public String update() {
        try {
            //asignarBodegas();
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("BuqueUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleCarga").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        if (current != null){
            //current = (Buque) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("BuqueDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleCarga").getString("PersistenceErrorOccured"));
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

    @FacesConverter(forClass = Buque.class)
    public static class BuqueControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            BuqueController controller = (BuqueController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "buqueController");
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
            if (object instanceof Buque) {
                Buque o = (Buque) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }

    
    public List<Bodega> getBodegas() {
        //Obtiene la lista de bodegas, si esta vacia devuelve una lista de CANTIDAD_BODEGAS, sino lo deja asi
        if (bodegas == null && current != null){
            if (current.getBodegaCollection() != null && current.getBodegaCollection().size() > 0){
                bodegas = new ArrayList<Bodega>(current.getBodegaCollection());
            } else {
                bodegas = new ArrayList<Bodega>();
                for (int i = 1; i <= CANTIDAD_BODEGAS; i++){
                    Bodega bod = new Bodega();
                    bod.setNro(i);
                    bod.setCapacidadPiesCubicos(BigDecimal.ZERO);
                    bod.setBuque(current);
                    bodegas.add(bod);
                }
                current.setBodegaCollection(bodegas);
            }
            Collections.sort(bodegas);
        }
        return bodegas;
    }

    public void setBodegas(List<Bodega> bodegas) {
        this.bodegas = bodegas;
    }

    public DataModel getBodegasModel() {
        if (bodegasModel == null)
            bodegasModel = new ListDataModel(getBodegas());
        return bodegasModel;
    }

 
    
    
}