package com.orco.graneles.jsf.salario;

import com.orco.graneles.domain.miscelaneos.FixedList;
import com.orco.graneles.domain.miscelaneos.TipoValorConcepto;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.Adelanto;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import com.orco.graneles.model.personal.PersonalFacade;
import com.orco.graneles.model.salario.AdelantoFacade;
import com.orco.graneles.reports.ReciboAdelanto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
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

@ManagedBean(name = "adelantoController")
@SessionScoped
public class AdelantoController implements Serializable {

    private Adelanto current;
    private DataModel items = null;
    private Personal currentPersonal;
    @EJB
    private AdelantoFacade ejbFacade;
    @EJB
    private FixedListFacade fixedListF;
    @EJB
    private PersonalFacade personalF;
    
    private int selectedItemIndex;
    private ListDataModel adelantosPersonalModel;
    private List<FixedList> opcionesAdelanto;
    private BigDecimal valorMaximoCalculado;
    private BigDecimal valorTotalAdelantos;
    private String urlRecibo;
    

    public AdelantoController() {
    }

    public void init() {
        recreateModel();
    }

    public void recreateModelCreate() {
        currentPersonal = null;
        adelantosPersonalModel = null;
    }
    
    public void seleccionarPersonal(){
        if (currentPersonal != null){
            //No realizo la asignacion directa ya que puede ser que tengan datos desactualizados la lista de personal por el cache
            getSelected().setPersonal(personalF.find(currentPersonal.getId()));
            valorMaximoCalculado = ejbFacade.calcularTotalAdelantoAcumulado(currentPersonal);
            
            List<Adelanto> adelantos = ejbFacade.obtenerAdelantosPeriodo(currentPersonal);
            Collections.sort(adelantos, new ComparadorAdelanto());
            adelantosPersonalModel = new ListDataModel(adelantos);

            valorTotalAdelantos = BigDecimal.ZERO;
            for (Adelanto a : adelantos){
                valorTotalAdelantos = valorTotalAdelantos.add(a.getValor());
            }
            
            getSelected().setValor(valorMaximoCalculado.subtract(valorTotalAdelantos));
            getSelected().setFecha(new Date());
        }
    }
   
    public Adelanto getSelected() {
        if (current == null) {
            current = new Adelanto();
            selectedItemIndex = -1;
        }
        return current;
    }
    
    public void setSelected(Adelanto selected){
        current = selected;
    }

    private AdelantoFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        //current = (Adelanto) getItems().getRowData();
        //selectedItemIndex = getItems().getRowIndex();
        if (current != null) {
            
            ReciboAdelanto recibo = new ReciboAdelanto(current);
            urlRecibo = recibo.obtenerReportePDF();
            
            return "View";
        } else {
            return null;
        }
    }

    public String prepareCreate() {
        recreateModelCreate();
        current = new Adelanto();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            recreateModelCreate();
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("AdelantoCreated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleSalario").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        //current = (Adelanto) getItems().getRowData();
        //selectedItemIndex = getItems().getRowIndex();
        if (current != null){
            return "Edit";
        } else {
            return null;
        }
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("AdelantoUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleSalario").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        //current = (Adelanto) getItems().getRowData();
        //selectedItemIndex = getItems().getRowIndex();
        if (current != null){
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("AdelantoDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleSalario").getString("PersistenceErrorOccured"));
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
        recreateModelCreate();
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    @FacesConverter(forClass = Adelanto.class)
    public static class AdelantoControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AdelantoController controller = (AdelantoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "adelantoController");
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
            if (object instanceof Adelanto) {
                Adelanto o = (Adelanto) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }

    public Personal getCurrentPersonal() {
        return currentPersonal;
    }

    public void setCurrentPersonal(Personal currentPersonal) {
        this.currentPersonal = currentPersonal;
    }

    public ListDataModel getAdelantosPersonalModel() {
        return adelantosPersonalModel;
    }

    public BigDecimal getValorMaximoCalculado() {
        return valorMaximoCalculado;
    }

    public void setValorMaximoCalculado(BigDecimal valorMaximoCalculado) {
        this.valorMaximoCalculado = valorMaximoCalculado;
    }

    public BigDecimal getValorTotalAdelantos() {
        return valorTotalAdelantos;
    }

    public void setValorTotalAdelantos(BigDecimal valorTotalAdelantos) {
        this.valorTotalAdelantos = valorTotalAdelantos;
    }

    public String getUrlRecibo() {
        return urlRecibo;
    }

    /**
     * Clse comparadora del adelanto por fecha desdcendiente
     */
    private class ComparadorAdelanto implements Comparator<Adelanto>{

        @Override
        public int compare(Adelanto o1, Adelanto o2) {
            return o1.getFecha().compareTo(o2.getFecha());
        }
        
    }
}