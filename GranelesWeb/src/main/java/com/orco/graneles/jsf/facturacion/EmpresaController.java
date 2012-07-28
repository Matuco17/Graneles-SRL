package com.orco.graneles.jsf.facturacion;

import com.orco.graneles.domain.facturacion.Empresa;
import com.orco.graneles.domain.miscelaneos.TipoEmpresa;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.facturacion.EmpresaFacade;
import com.orco.graneles.model.miscelaneos.FixedListFacade;

import java.io.*;
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
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.UploadedFile;

@ManagedBean(name = "empresaController")
@SessionScoped
public class EmpresaController implements Serializable {

    private Empresa current;
    private DataModel items = null;
    @EJB
    private EmpresaFacade ejbFacade;
    @EJB
    private FixedListFacade fixedListF;
    private int selectedItemIndex;

    private UploadedFile logoFile;
    
    private void saveLogo(){        
        if (StringUtils.isNotEmpty(logoFile.getFileName())){
            FileOutputStream fos = null;
            try {
                String pathBaseArchivos = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
                
                fos = new FileOutputStream(pathBaseArchivos + "resources/uploadedFiles/logosEmpresas/" + getSelected().getId());
                fos.write(logoFile.getContents());
                
            } catch (IOException ex) {
                Logger.getLogger(EmpresaController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fos.close();
                } catch (IOException ex) {
                    Logger.getLogger(EmpresaController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }        
    }
    
    
    public EmpresaController() {
    }

    public void init() {
        recreateModel();
        
        JsfUtil.minimoRolRequerido(Grupo.ROL_USUARIO);
    }

    public Empresa getSelected() {
        if (current == null) {
            current = new Empresa();
            selectedItemIndex = -1;
        }
        return current;
    }
    
    public void setSelected(Empresa selected){
        current = selected;
    }

    private EmpresaFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        if (current != null){
            //current = (Empresa) getItems().getRowData();
            //selectedItemIndex = getItems().getRowIndex();
            return "View";
        } else {
            return null;
        }
    }

    public String prepareCreate() {
        current = new Empresa();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            saveLogo();
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleFacturacion").getString("EmpresaCreated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleFacturacion").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        if (current != null){
            //current = (Empresa) getItems().getRowData();
            //selectedItemIndex = getItems().getRowIndex();
            return "Edit";
        } else {
            return null;
        }
    }

    public String update() {
        try {
            getFacade().edit(current);
            saveLogo();
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleFacturacion").getString("EmpresaUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleFacturacion").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        if (current != null){
            //current = (Empresa) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleFacturacion").getString("EmpresaDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleFacturacion").getString("PersistenceErrorOccured"));
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
    
    public SelectItem[] getItemsAvailableExportadoresSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByTipoEmpresa(fixedListF.find(TipoEmpresa.EXPORTADOR)), true);
    }
    
    public SelectItem[] getItemsAvailableFumigadoresSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByTipoEmpresa(fixedListF.find(TipoEmpresa.FUMIGACION)), true);
    }
    
    
    public SelectItem[] getItemsAvailableControladoresSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByTipoEmpresa(fixedListF.find(TipoEmpresa.CONTROL)), true);
    }
    
    @FacesConverter(forClass = Empresa.class)
    public static class EmpresaControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EmpresaController controller = (EmpresaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "empresaController");
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
            if (object instanceof Empresa) {
                Empresa o = (Empresa) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }

    public UploadedFile getLogoFile() {
        return logoFile;
    }

    public void setLogoFile(UploadedFile logoFile) {
        this.logoFile = logoFile;
    }
    
    
}