package com.orco.graneles.jsf.carga;

import com.orco.graneles.domain.carga.ArchivoBuque;
import com.orco.graneles.domain.carga.ArchivoEmbarque;
import com.orco.graneles.domain.carga.Bodega;
import com.orco.graneles.domain.carga.Buque;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.carga.BuqueFacade;
import java.io.IOException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.UploadedFile;

@ManagedBean(name = "buqueController")
@SessionScoped
public class BuqueController implements Serializable {
    private static final double COEFICIENTE_M3_TO_P3 = 0.028316847;
    private static final double COEFICIENTE_P3_TO_M3 = 35.3146662126613;

    private Buque current;
    private DataModel items = null;
    @EJB
    private BuqueFacade ejbFacade;
    private int selectedItemIndex;
    private List<Bodega> bodegas;
    private DataModel bodegasModel;
    private static final int CANTIDAD_BODEGAS = 9;
    
    private Boolean capacidadPiesCubicos;
    
    
    //Variables de Archivo
    private DataModel archivosModel;
    private List<ArchivoBuque> listaArchivos;
    private UploadedFile currentFile;
    
    
    public BuqueController() {
    }

    public void init() {
        recreateModel();
        
        JsfUtil.minimoRolRequerido(Grupo.ROL_USUARIO);
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

    private void corregirCapacidad() {
        if (capacidadPiesCubicos){
            for (Bodega b : bodegas){
                if (b.getCapacidadPiesCubicos() == null){
                    b.setCapacidadMetrosCubicos(BigDecimal.ZERO);
                } else {
                    b.setCapacidadMetrosCubicos(b.getCapacidadPiesCubicos().multiply(new BigDecimal(COEFICIENTE_M3_TO_P3)));
                }
            }
        } else {
            for (Bodega b : bodegas){
                if (b.getCapacidadMetrosCubicos() == null){
                    b.setCapacidadPiesCubicos(BigDecimal.ZERO);
                } else {
                    b.setCapacidadPiesCubicos(b.getCapacidadMetrosCubicos().multiply(new BigDecimal(COEFICIENTE_P3_TO_M3)));
                }
            }
        }
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
            bodegas = null;
            
            bodegasModel = null;
            archivosModel = null;
            listaArchivos = null;
            //current = (Buque) getItems().getRowData();
            //selectedItemIndex = getItems().getRowIndex();
            return "View";
        } else {
            return null;
        }
    }

    public String prepareCreate() {
        current = new Buque();
        
         bodegas = null;
            
        bodegasModel = null;
        
          
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            //asignarBodegas();
            corregirCapacidad();
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
            
            bodegas = null;
            bodegasModel = null;
            listaArchivos = null;
            archivosModel = null;
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
            corregirCapacidad();            
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
    
    public void agregarBodega(){
        Bodega bod = new Bodega();
                    bod.setNro(getBodegas().size() + 1);
                    bod.setCapacidadPiesCubicos(BigDecimal.ZERO);
                    bod.setBuque(current);
                    getBodegas().add(bod);
        current.setBodegaCollection(getBodegas());
    }
    
    public void restarBodega(){
        if (getBodegas().size() > 0){
            getBodegas().remove(getBodegas().size() - 1);
            current.setBodegaCollection(getBodegas());
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
        capacidadPiesCubicos = Boolean.TRUE;
        archivosModel = null;
    
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
    

    public void seleccionarCapacidad(ValueChangeEvent e){
        capacidadPiesCubicos = (Boolean) e.getOldValue();
        corregirCapacidad();
        capacidadPiesCubicos = (Boolean) e.getNewValue();
    }
    
    public void setBodegas(List<Bodega> bodegas) {
        this.bodegas = bodegas;
    }

    public DataModel getBodegasModel() {
        if (bodegasModel == null)
            bodegasModel = new ListDataModel(getBodegas());
        return bodegasModel;
    }

    public Boolean getCapacidadPiesCubicos() {
        return capacidadPiesCubicos;
    }

    public void setCapacidadPiesCubicos(Boolean capacidadPiesCubicos) {
        this.capacidadPiesCubicos = capacidadPiesCubicos;
    }

    
    /*
     * Comienzo de funcionalidades de Archivos del embarque
     */
    
    public void subirArchivo(){
        if (getCurrentFile() != null && StringUtils.isNotEmpty(getCurrentFile().getFileName())){
            try {
                ejbFacade.subirArchivo(currentFile.getInputstream(), currentFile.getFileName(), current);
            } catch (IOException ex) {
                Logger.getLogger(BuqueController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        listaArchivos = null;
        archivosModel = null;
    }
    
    public void eliminarArchivo(){
        if (archivosModel.getRowData() != null){
            getSelected().getArchivoBuqueCollection().remove((ArchivoBuque) archivosModel.getRowData());
            listaArchivos = null;
            archivosModel = null;
        }
    }    
        
    public List<ArchivoBuque> getListaArchivos() {
        if (listaArchivos == null){
            listaArchivos = new ArrayList<ArchivoBuque>(getSelected().getArchivoBuqueCollection());
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