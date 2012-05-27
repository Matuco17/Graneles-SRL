package com.orco.graneles.jsf.seguridad;



import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.domain.seguridad.Usuario;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.seguridad.GrupoFacade;
import com.orco.graneles.model.seguridad.UsuarioFacade;
import java.io.Serializable;
import java.util.Collection;
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

@ManagedBean(name = "usuarioController")
@SessionScoped
public class UsuarioController implements Serializable {

    private Usuario current;
    private DataModel items = null;
    
    private Collection<Grupo> grupos = null; 
    
    @EJB
    private UsuarioFacade ejbFacade;
    
    @EJB
    private GrupoFacade grupoFacade;
    
    private int selectedItemIndex;
    
    private String passwordnew;
    private String passwordnewConfirmation;
    
    private Converter actualConverter;

    public UsuarioController() {
    }

    public void init(){
        recreateModel();
    }
    
    public Usuario getSelected() {
        if (current == null) {
            current = new Usuario();
            selectedItemIndex = -1;
        }
        return current;
    }

    private UsuarioFacade getFacade() {
        return ejbFacade;
    }


    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Usuario) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Usuario();
        passwordnew = null;
        passwordnewConfirmation = null;
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            if (confirmarYAsignarPassword()){
                getFacade().create(current);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleUsuario").getString("UsuarioCreated"));
                return "View";
            } else {
                return null;
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleUsuario").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Usuario) getItems().getRowData();
        passwordnew = null;
        passwordnewConfirmation = null;
        selectedItemIndex = getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            if (confirmarYAsignarPassword()){
                getFacade().edit(current);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleUsuario").getString("UsuarioUpdated"));
                return "View";
            } else {
                return null;
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleUsuario").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Usuario) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleUsuario").getString("UsuarioDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleUsuario").getString("PersistenceErrorOccured"));
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = new ListDataModel(ejbFacade.findAll());
        }
        return items;
    }

    public Collection<Grupo> getAllGrupos(){
        if (grupos == null){
            grupos = grupoFacade.findAll();
        }
        return grupos;
    }
    
    
    /**
     * Metodo interno que verifica que si se quiere cambiar los password, y si se quiere cambiar validan que sean iguales y asigna el password al current para su actualizacion
     * En cualquier caso blaquea las password ingresadas por el usuario
     * @return True si se realizó la asignación del password nuevo o si no se puedo nada y se mantiene el password anterior, False si hay algún error y tira cartel de mensaje tambien
     */
    private boolean confirmarYAsignarPassword(){
        boolean resultado = false;
        if (StringUtils.isEmpty(passwordnew) && StringUtils.isEmpty(passwordnewConfirmation)){
            //Si ambos son blancos, entonces no cambio el password y valido correctamente
            resultado = true;
        } else if (StringUtils.equalsIgnoreCase(passwordnew, passwordnewConfirmation)){
            //Si ambos son iguales, entonces actualizo el password y valido correctamente
            current.setPassword(passwordnew);
            resultado = true;
        } else {
            //Los password escritos no son iguales, tiro un cartel de error y no valido
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundleUsuario").getString("ConfirmationPasswordInvalid"));
            resultado = true;
        }
        
        //Blanqueo las password ingresadas
        passwordnew = null;
        passwordnewConfirmation = null;
        
        return resultado;
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

    @FacesConverter(forClass = Usuario.class)
    public static class UsuarioControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UsuarioController controller = (UsuarioController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "usuarioController");
            return controller.ejbFacade.find(value);
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
            if (object instanceof Usuario) {
                Usuario o = (Usuario) object;
                return o.getUsername();
            } else {
                return null;
                //throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + UsuarioController.class.getName());
            }
        }
    }

    public String getPasswordnew() {
        return passwordnew;
    }

    public void setPasswordnew(String passwordnew) {
        this.passwordnew = passwordnew;
    }

    public String getPasswordnewConfirmation() {
        return passwordnewConfirmation;
    }

    public void setPasswordnewConfirmation(String passwordnewConfirmation) {
        this.passwordnewConfirmation = passwordnewConfirmation;
    }
    
    public Converter getUsuarioConverter()
    {
        if(actualConverter == null){
           actualConverter = new UsuarioControllerConverter();
        }
        
        return actualConverter;
    }
    
    
}
