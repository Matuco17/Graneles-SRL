package com.orco.graneles.jsf.carga;

import com.orco.graneles.domain.carga.*;
import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.personal.Tarea;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.carga.CargaPreviaFacade;
import com.orco.graneles.model.carga.CargaTurnoFacade;
import com.orco.graneles.model.carga.EmbarqueFacade;
import com.orco.graneles.model.carga.TurnoEmbarqueFacade;
import com.orco.graneles.model.personal.PersonalFacade;
import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.UploadedFile;

@ManagedBean(name = "embarqueController")
@SessionScoped
public class EmbarqueController implements Serializable {

    private Embarque current;
    private DataModel items = null;
    @EJB
    private EmbarqueFacade ejbFacade;
    @EJB
    private CargaPreviaFacade cargaPreviaFacade;
    @EJB
    private CargaTurnoFacade cargaTurnoFacade;
    @EJB
    private PersonalFacade personalFacade;
    @EJB
    private TurnoEmbarqueFacade turnoEmbarqueFacade;
    
    
    
    private int selectedItemIndex;
    private List<CargaPrevia> cargasPrevias;
    private DataModel cargasPreviasModel;
    private Mercaderia mercaderiaPrevia;
    private DataModel listaTurnos;

    //Variables de turno
    private List<CargaTurno> cargas;
    private DataModel cargasModel;
    private List<Personal> trabajadores;
    private TrabajadoresTurnoEmbarque currentTTE;
    private List<TrabajadoresTurnoEmbarque> trabajadoresTurno;
    private TrabajadoresTurnoModel trabajadoresTurnoModel;
    private TrabajadoresTurnoEmbarque selectedTTE;
    private TurnoEmbarque currentTE;
    private boolean editarTurno;

    //Variables de Archivo
    private DataModel archivosModel;
    private List<ArchivoEmbarque> listaArchivos;
    private UploadedFile currentFile;
    
    public EmbarqueController() {
    }

    private void tratarDeLevantarCarga(){
        if (getSelected().getBuque() != null && this.getMercaderiaPrevia() != null){
            this.cargasPrevias = cargaPreviaFacade.obtenerCargasPrevias(getSelected().getBuque(), this.getMercaderiaPrevia(), getSelected());
            cargasPreviasModel = null;
        } else {
            cargasPreviasModel = null;
            cargasPrevias = null;
        }
    }

        
    public void seleccionarBuque(ValueChangeEvent e){
            current.setBuque((Buque) e.getNewValue());
            tratarDeLevantarCarga();
    }
    
    public void seleccionarMercaderiaPrevia(ValueChangeEvent e){
            mercaderiaPrevia = (Mercaderia) e.getNewValue();
            tratarDeLevantarCarga();
    }
 
        
    public void init() {
        recreateModel();
    }

    public Embarque getSelected() {
        if (current == null) {
            current = ejbFacade.crearNuevoEmbarque();
            selectedItemIndex = -1;
        }
        return current;
    }

    private EmbarqueFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Embarque) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = ejbFacade.crearNuevoEmbarque();
        selectedItemIndex = -1;
        return "Create";
    }
        
    public String create() {
        try {
            current.setCargaPreviaCollection(cargasPrevias);
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("EmbarqueCreated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleCarga").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Embarque) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            current.setCargaPreviaCollection(cargasPrevias);
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("EmbarqueUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleCarga").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Embarque) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        performDestroy();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("EmbarqueDeleted"));
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

    @FacesConverter(forClass = Embarque.class)
    public static class EmbarqueControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EmbarqueController controller = (EmbarqueController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "embarqueController");
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
            if (object instanceof Embarque) {
                Embarque o = (Embarque) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }

     
    public Mercaderia getMercaderiaPrevia() {
        if (getSelected().getCargaPreviaCollection() != null && getSelected().getCargaPreviaCollection().size() > 0){
            mercaderiaPrevia = getSelected().getCargaPreviaCollection().iterator().next().getMercaderia();
        }
        return mercaderiaPrevia;
    }

    public void setMercaderiaPrevia(Mercaderia mercaderiaPrevia) {
        this.mercaderiaPrevia = mercaderiaPrevia;
    }
    
    public List<CargaPrevia> getCargasPrevias() {
        if (cargasPrevias == null){
            tratarDeLevantarCarga();
        }
        return cargasPrevias;
    }

    public DataModel getCargasPreviasModel() {
        if (cargasPreviasModel == null && getSelected().getCargaPreviaCollection() != null && getSelected().getCargaPreviaCollection().size() > 0)
            cargasPreviasModel = new ListDataModel(getCargasPrevias());
        return cargasPreviasModel;
    }

    public DataModel getListaTurnos() {
        if (listaTurnos == null){
            List<TurnoEmbarque> turnos = new ArrayList<TurnoEmbarque>(getSelected().getTurnoEmbarqueCollection());
            Collections.sort(turnos);
            listaTurnos = new ListDataModel(turnos);
        }
        return listaTurnos;
    }

    public void setListaTurnos(DataModel listaTurnos) {
        this.listaTurnos = listaTurnos;
    }


    
    
    
    /*
     * Funcionalidades de turno Embarque dentro
     */
    public void recreateModelTurno() {
        this.trabajadoresTurno = null;
        this.trabajadoresTurnoModel = null;
        this.currentTTE = null;
        this.selectedTTE = null;
        this.cargas = null;
        this.cargasModel = null;
    }
    
        
    public String prepareEditTE() {
        currentTE = (TurnoEmbarque) getListaTurnos().getRowData();
        recreateModelTurno();
        editarTurno = true;
        return "CreateEditTurnoEmbarque";
    }
    
    public String prepareViewTE() {
        currentTE = (TurnoEmbarque) getListaTurnos().getRowData();
        recreateModelTurno();
        editarTurno = false;
        return "CreateEditTurnoEmbarque";
    }
    
    public String prepareCreateTE() {
        currentTE = turnoEmbarqueFacade.crearNuevoTurnoEmbarque(current);
        recreateModelTurno();
        editarTurno = true;
        return "CreateEditTurnoEmbarque";
    }
    
    public void destroyTE(){
        currentTE = (TurnoEmbarque) getListaTurnos().getRowData();
        try {
            turnoEmbarqueFacade.remove(currentTE);
            current.getTurnoEmbarqueCollection().remove(currentTE);
            listaTurnos = null;
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("TurnoEmbarqueDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleCarga").getString("PersistenceErrorOccured"));
        }
    }
    
    public String saveTE(){
        if (editarTurno){
            currentTE.setCargaTurnoCollection(cargas);
            for (TrabajadoresTurnoEmbarque tte : trabajadoresTurno){
                tte.setPlanilla(currentTE);
            }
            currentTE.setTrabajadoresTurnoEmbarqueCollection(trabajadoresTurno);
            
            if (currentTE.getId() == null)
                current.getTurnoEmbarqueCollection().add(currentTE);
            
            turnoEmbarqueFacade.persist(currentTE);
            
            listaTurnos = null;            
        }
        return "Edit";
    }
    
    public TurnoEmbarque getCurrentTE() {
        return currentTE;
    }

    public void setCurrentTE(TurnoEmbarque currentTE) {
        this.currentTE = currentTE;
        recreateModelTurno();
    }
    
    public TrabajadoresTurnoEmbarque getCurrentTTE() {
        if (currentTTE == null){
            currentTTE = new TrabajadoresTurnoEmbarque();
            currentTTE.setHoras(6);
        }
        return currentTTE;
    }

    public void setCurrentTTE(TrabajadoresTurnoEmbarque currentTTE) {
        this.currentTTE = currentTTE;
    }
    
    public void seleccionarPersonal(ValueChangeEvent e){
        Personal personalSeleccionado = (Personal) e.getNewValue();
        getCurrentTTE().setPersonal(personalSeleccionado);
        if (personalSeleccionado.getCategoriaPrincipal() != null)
            getCurrentTTE().setCategoria(personalSeleccionado.getCategoriaPrincipal());
    }
    
    public void seleccionarTarea(ValueChangeEvent e){
        getCurrentTTE().setTarea((Tarea) e.getNewValue());
    }
    
    public void seleccionarCategoria(ValueChangeEvent e){
        getCurrentTTE().setCategoria((Categoria) e.getNewValue());
    }
    
    public void seleccionarHoras(ValueChangeEvent e){
        getCurrentTTE().setHoras((Integer) e.getNewValue());
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
        selectedItemIndex = getTrabajadoresTurnoModel().getRowIndex(getSelectedTTE());
        getTrabajadoresTurnoModel().setRowIndex(-1);
        if (selectedItemIndex >= 0){
            getTrabajadoresTurno().remove(selectedItemIndex);

            trabajadoresTurnoModel = null;
            currentTTE = null;
            selectedTTE = null;
        }
    }
    
    
    public void seleccionarTTE(){
        selectedTTE = getTrabajadoresTurnoModel().getRowData();
    }
    
    public List<Personal> getTrabajadores(){
        if (trabajadores == null){
            trabajadores = personalFacade.findAll();
            Collections.sort(trabajadores, new ComparadorPersonal());
        }
        return trabajadores;
    }
    

    public DataModel getCargasModel() {
        if (cargasModel == null)
            cargasModel = new ListDataModel(getCargas());
        return cargasModel;
    }

    public List<TrabajadoresTurnoEmbarque> getTrabajadoresTurno() {
        if (trabajadoresTurno == null){
            if (getCurrentTE().getTrabajadoresTurnoEmbarqueCollection() != null){
               trabajadoresTurno = new ArrayList<TrabajadoresTurnoEmbarque>(getCurrentTE().getTrabajadoresTurnoEmbarqueCollection());
            } else {
               trabajadoresTurno = new ArrayList<TrabajadoresTurnoEmbarque>();
            }            
        }
        return trabajadoresTurno;
    }

    public TrabajadoresTurnoModel getTrabajadoresTurnoModel() {
        if (trabajadoresTurnoModel == null){
            trabajadoresTurnoModel = new TrabajadoresTurnoModel(getTrabajadoresTurno());
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
    
    public boolean isEditarTurno() {
        return editarTurno;
    }
    
    public List<CargaTurno> getCargas() {
        if (cargas == null && currentTE != null){
            this.cargas = cargaTurnoFacade.obtenerCargas(getCurrentTE());
        }
        return cargas;
    }
  
     /*
     * Fin Funcionalidades turno Embarque
     */

    /*
     * Comienzo de funcionalidades de Archivos del embarque
     */
    
    public void subirArchivo(){
        if (getCurrentFile() != null && StringUtils.isNotEmpty(getCurrentFile().getFileName())){
            try {
                ejbFacade.subirArchivo(currentFile.getInputstream(), currentFile.getFileName(), current);
            } catch (IOException ex) {
                Logger.getLogger(EmbarqueController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        listaArchivos = null;
        archivosModel = null;
    }
    
    public void eliminarArchivo(){
        if (archivosModel.getRowData() != null){
            getSelected().getArchivoEmbarqueCollection().remove((ArchivoEmbarque) archivosModel.getRowData());
            listaArchivos = null;
            archivosModel = null;
        }
    }    
        
    public List<ArchivoEmbarque> getListaArchivos() {
        if (listaArchivos == null){
            listaArchivos = new ArrayList<ArchivoEmbarque>(getSelected().getArchivoEmbarqueCollection());
            Collections.sort(listaArchivos);
        }
        return listaArchivos;
    }
    
    public DataModel getArchivosModel() {
        if (archivosModel == null){
            archivosModel = new ListDataModel(getListaArchivos());
        }
        return archivosModel;
    }

    public UploadedFile getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(UploadedFile currentFile) {
        this.currentFile = currentFile;
    }
    
    /*
     * Fin de las funcionalidades del Archivos del embarque
     */

 
    

  
  

}