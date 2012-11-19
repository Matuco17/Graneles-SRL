package com.orco.graneles.jsf.miscelaneos;

import com.orco.graneles.domain.miscelaneos.*;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.miscelaneos.FixedListFacade;

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

@ManagedBean (name="fixedListController")
@SessionScoped
public class FixedListController implements Serializable {

    private FixedList current;
    private DataModel items = null;

    @EJB 
    private FixedListFacade ejbFacade;

    private int selectedItemIndex;

    public FixedListController() {
    }

    public void init(){
        recreateModel();
        
        JsfUtil.minimoRolRequerido(Grupo.ROL_ADMINISTRADOR);
    }

    public FixedList getSelected() {
        if (current == null) {
            current = new FixedList();
            selectedItemIndex = -1;
        }
        return current;
    }

    private FixedListFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (FixedList)getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new FixedList();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleMiscelaneos").getString("FixedListCreated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleMiscelaneos").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (FixedList)getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleMiscelaneos").getString("FixedListUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleMiscelaneos").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (FixedList)getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleMiscelaneos").getString("FixedListDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleMiscelaneos").getString("PersistenceErrorOccured"));
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
    
      public SelectItem[] getEstadoCivilSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(EstadoCivil.ID_LISTA), true);
    }
    public SelectItem[] getEstadoPersonalSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(EstadoPersonal.ID_LISTA), true);
    }
    public SelectItem[] getTipoBuqueSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(TipoBuque.ID_LISTA), true);
    }
    public SelectItem[] getTipoCargaSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(TipoCarga.ID_LISTA), true);
    }
    public SelectItem[] getTipoConceptoReciboSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(TipoConceptoRecibo.ID_LISTA), true);
    }
    public SelectItem[] getTipoDocumentoSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(TipoDocumento.ID_LISTA), true);
    }
    public SelectItem[] getTipoEmpresaSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(TipoEmpresa.ID_LISTA), true);
    }
    public SelectItem[] getTipoFeriadoSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(TipoFeriado.ID_LISTA), true);
    }
    public SelectItem[] getTipoReciboSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(TipoRecibo.ID_LISTA), true);
    }
    public SelectItem[] getTipoTapaBodegaSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(TipoTapaBodega.ID_LISTA), true);
    }
    public SelectItem[] getTipoValorConceptoSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(TipoValorConcepto.ID_LISTA), true);
    }
    public SelectItem[] getTurnoSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(Turno.ID_LISTA), true);
    }
    public SelectItem[] getTipoJornalSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(TipoJornal.ID_LISTA), true);
    }
    
    public SelectItem[] getLugarSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(Lugar.ID_LISTA), true);
    }
    
    public SelectItem[] getItemsAvailableSelectMany(int idLista) {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(idLista), false);
    }

    public SelectItem[] getItemsAvailableSelectOne(int idLista) {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(idLista), true);
    }

    public SelectItem[] getTipoTurnoFacturadoSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(TipoTurnoFactura.ID_LISTA), true);
    }

    public SelectItem[] getGrupoFacturacionSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findByLista(GrupoFacturacion.ID_LISTA), true);
    }


    @FacesConverter(forClass=FixedList.class)
    public static class FixedListControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            FixedListController controller = (FixedListController)facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "fixedListController");
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
            if (object instanceof FixedList) {
                FixedList o = (FixedList) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }

    }

}