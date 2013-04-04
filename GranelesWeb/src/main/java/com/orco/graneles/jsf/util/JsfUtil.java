package com.orco.graneles.jsf.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

public class JsfUtil {
    
    private static SelectItemComparator itemsComparator = new SelectItemComparator();

    public static SelectItem[] getSelectItems(List<?> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = selectOne ? 1 : 0;
        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        Arrays.sort(items, itemsComparator);
        if (selectOne) {items[0] = new SelectItem("", "---");}
        return items;
    }
    
    /**
     * Metodo que verifica que si no tenes el menor rol requerido, el sistema te manda un acceso denegado
     * @param rol si rol es nulo, se le niega todo el acceso
     */
    public static void minimoRolRequerido(String rol){
         try {
            if (rol == null || !FacesContext.getCurrentInstance().getExternalContext().isUserInRole(rol)){
                FacesContext.getCurrentInstance().getExternalContext().redirect("/GranelesWeb/faces/accessDenied.xhtml");
            }            
        } catch (IOException ex) {
            Logger.getLogger("SinClase").log(Level.SEVERE, null, ex);
        }
    }

    public static void addErrorMessage(Exception ex, String defaultMsg) {
        String msg = ex.getLocalizedMessage();
        if (msg != null && msg.length() > 0) {
            addErrorMessage(msg);
        } else {
            addErrorMessage(defaultMsg);
        }
    }
    
    public static void addErrorMessages(List<String> messages) {
        for (String message : messages) {
            addErrorMessage(message);
        }
    }

    public static void addErrorMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    public static void addSuccessMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
        FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
    }

    public static String getRequestParameter(String key) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
    }

    public static Object getObjectFromRequestParameter(String requestParameterName, Converter converter, UIComponent component) {
        String theId = JsfUtil.getRequestParameter(requestParameterName);
        return converter.getAsObject(FacesContext.getCurrentInstance(), component, theId);
    }
    
    private static class SelectItemComparator implements Comparator<SelectItem>{

        @Override
        public int compare(SelectItem o1, SelectItem o2) {
            if (o1 != null && o2 != null){
                return o1.getLabel().compareToIgnoreCase(o2.getLabel());
            } else if (o1 == null) {
                return -1;
            } else {
                return 1;
            }
        }
        
    }
    
}