package com.orco.graneles.jsf.carga;

import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.carga.Embarque;
import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.carga.TurnoEmbarque;
import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.personal.Tarea;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.carga.CargaTurnoFacade;
import com.orco.graneles.model.carga.TurnoEmbarqueFacade;
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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.primefaces.model.SelectableDataModel;


@ManagedBean(name = "turnoEmbarqueController")
@SessionScoped
public class TurnoEmbarqueController implements Serializable {

    private TurnoEmbarque current;
    private DataModel items = null;
    @EJB
    private TurnoEmbarqueFacade ejbFacade;
    @EJB
    private CargaTurnoFacade cargaTurnoFacade;
    @EJB
    private PersonalFacade personalFacade;
    
    private TrabajadoresTurnoEmbarque selectedTTE;
    
    private int selectedItemIndex;

    private List<CargaTurno> cargas;
    private DataModel cargasModel;
    private List<Personal> trabajadores;
    private TrabajadoresTurnoEmbarque currentTTE;
    private List<TrabajadoresTurnoEmbarque> trabajadoresTurno;
    private TrabajadoresTurnoModel trabajadoresTurnoModel;
    
    
    public void seleccionarEmbarque(ValueChangeEvent e){
            current.setEmbarque((Embarque) e.getNewValue());
            embarqueSeleccionado();
    }
    
    
    public void seleccionarPersonal(ValueChangeEvent e){
        getCurrentTTE().setPersonal((Personal) e.getNewValue());
    }
    
    public void seleccionarTarea(ValueChangeEvent e){
        getCurrentTTE().setTarea((Tarea) e.getNewValue());
    }
    
    public void seleccionarCategoria(ValueChangeEvent e){
        getCurrentTTE().setCategoria((Categoria) e.getNewValue());
    }
    
      
    private void embarqueSeleccionado(){
        if (getSelected().getEmbarque() != null){
            this.cargas = cargaTurnoFacade.obtenerCargas(getSelected());
            cargasModel = null;
        } else {
            cargasModel = null;
            cargas = null;
        }
    }
    
    public void agregarTrabajador(){
        if (currentTTE.getCategoria() != null &&
            currentTTE.getHoras() != null &&
            currentTTE.getPersonal() != null &&
            currentTTE.getTarea() != null){
        
            getTrabajadoresTurno().add(currentTTE);
            currentTTE = null;
            trabajadoresTurnoModel = null;
        } else {
            //TODO: enviar mensaje de que falta algun que otro campo
        }
    }
    
    public void eliminarTrabajador() {
        //selectedItemIndex = getTrabajadoresTurnoModel().getRowIndex(getSelectedTTE());
        if (selectedItemIndex >= 0){
            getTrabajadoresTurno().remove(selectedItemIndex);

            trabajadoresTurnoModel = null;
            currentTTE = null;
        }
    }
    
    
    public void seleccionarTTE(){
        //selectedTTE = getTrabajadoresTurnoModel().getRowData();
    }
    
    public List<Personal> getTrabajadores(){
        if (trabajadores == null){
            trabajadores = personalFacade.findAll();
            Collections.sort(trabajadores, new ComparadorPersonal());
        }
        return trabajadores;
    }
    
    public TurnoEmbarqueController() {
    }

    public void init() {
        recreateModel();
    }

    public TurnoEmbarque getSelected() {
        if (current == null) {
            current = new TurnoEmbarque();
            selectedItemIndex = -1;
        }
        return current;
    }

    private TurnoEmbarqueFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (TurnoEmbarque) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new TurnoEmbarque();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            asignarListados();
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("TurnoEmbarqueCreated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleCarga").getString("PersistenceErrorOccured"));
            return null;
        }
    }
    
    private void asignarListados(){
        current.setCargaTurnoCollection(cargas);
        current.setTrabajadoresTurnoEmbarqueCollection(trabajadoresTurno);
    }

    public String prepareEdit() {
        current = (TurnoEmbarque) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("TurnoEmbarqueUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleCarga").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (TurnoEmbarque) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("TurnoEmbarqueDeleted"));
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

    @FacesConverter(forClass = TurnoEmbarque.class)
    public static class TurnoEmbarqueControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TurnoEmbarqueController controller = (TurnoEmbarqueController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "turnoEmbarqueController");
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
            if (object instanceof TurnoEmbarque) {
                TurnoEmbarque o = (TurnoEmbarque) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }
    
    public List<CargaTurno> getCargas() {
        return cargas;
    }

    public TrabajadoresTurnoEmbarque getCurrentTTE() {
        if (currentTTE == null){
            currentTTE = new TrabajadoresTurnoEmbarque();
        }
        return currentTTE;
    }

    public void setCurrentTTE(TrabajadoresTurnoEmbarque currentTTE) {
        this.currentTTE = currentTTE;
    }

    

    public DataModel getCargasModel() {
        if (cargasModel == null && cargas != null)
            cargasModel = new ListDataModel(cargas);
        return cargasModel;
    }

    public List<TrabajadoresTurnoEmbarque> getTrabajadoresTurno() {
        if (trabajadoresTurno == null){
            trabajadoresTurno = new ArrayList<TrabajadoresTurnoEmbarque>();
        }
        return trabajadoresTurno;
    }

    public TrabajadoresTurnoModel getTrabajadoresTurnoModel() {
        if (trabajadoresTurnoModel == null){
           // trabajadoresTurnoModel = new TrabajadoresTurnoModel(trabajadoresTurno);
        }
        return trabajadoresTurnoModel;
    }

    public TrabajadoresTurnoEmbarque getSelectedTTE() {
        return selectedTTE;
    }

    public void setSelectedTTE(TrabajadoresTurnoEmbarque selectedTTE) {
        this.selectedTTE = selectedTTE;
    }
    
    
    
    
    private class ComparadorPersonal implements Comparator<Personal>{

        @Override
        public int compare(Personal o1, Personal o2) {
            return o1.getCuil().compareTo(o2.getCuil());
        }
        
    }
}