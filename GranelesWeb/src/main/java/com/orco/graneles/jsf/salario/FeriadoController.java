package com.orco.graneles.jsf.salario;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.miscelaneos.TipoRecibo;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.ConceptoRecibo;
import com.orco.graneles.domain.salario.Feriado;
import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.domain.salario.Sueldo;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import com.orco.graneles.model.salario.ConceptoReciboFacade;
import com.orco.graneles.model.salario.FeriadoFacade;
import com.orco.graneles.model.salario.PeriodoFacade;
import com.orco.graneles.model.salario.SueldoFacade;
import com.orco.graneles.reports.RecibosSueldoFeriados;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import org.codehaus.groovy.control.io.URLReaderSource;
import org.joda.time.DateTime;

@ManagedBean(name = "feriadoController")
@SessionScoped
public class FeriadoController implements Serializable {

    private Feriado current;
    private DataModel items = null;
    @EJB
    private FeriadoFacade ejbFacade;
    @EJB
    private SueldoFacade sueldoF;
    @EJB
    private ConceptoReciboFacade conceptoReciboF;
    @EJB
    private PeriodoFacade periodoF;
    @EJB
    private FixedListFacade fixedListF;
    
    private int selectedItemIndex;
    
    private DataModel trabajadoresFeriadoModel;
    private List<Personal> trabajadoresFeriado;

    private String urlLinkRecibos;
    
    public FeriadoController() {
    }

    public void init() {
        recreateModel();
        
        JsfUtil.minimoRolRequerido(Grupo.ROL_CONTADOR);
    }

    public void obtenerTrabajadoresFeriado(){
        trabajadoresFeriadoModel = null;
        trabajadoresFeriado = null;
        if (current != null){
            Map<Personal, TrabajadoresTurnoEmbarque> trabajadoresIncluidos = ejbFacade.obtenerTrabajadoresIncluidos(current.getFecha());
            trabajadoresFeriado = new ArrayList<Personal>(trabajadoresIncluidos.keySet());
            Collections.sort(trabajadoresFeriado);
            trabajadoresFeriadoModel = new ListDataModel(trabajadoresFeriado);
            
            DateTime fechaFeriadoDT = new DateTime(current.getFecha());
            List<Sueldo> sueldosFeriado = new ArrayList<Sueldo>();
            Map<Integer, List<ConceptoRecibo>> conceptosHoras = conceptoReciboF.obtenerConceptosXTipoRecibo(fixedListF.find(TipoRecibo.HORAS));
            Periodo periodo = periodoF.verPeriodo(fechaFeriadoDT.getYear(), fechaFeriadoDT.getMonthOfYear());
            
            for (TrabajadoresTurnoEmbarque tteFeriado : trabajadoresIncluidos.values()){
                
                Sueldo sueldoFeriado = sueldoF.calcularSueldoFeriado(tteFeriado, periodo, conceptosHoras);
                                
                sueldosFeriado.add(sueldoFeriado);
            }
         
            urlLinkRecibos = (new RecibosSueldoFeriados(periodo, sueldosFeriado, current)).obtenerReportePDF();
        }
    }
    
    public Feriado getSelected() {
        if (current == null) {
            current = new Feriado();
            selectedItemIndex = -1;
        }
        return current;
    }

    private FeriadoFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Feriado) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        obtenerTrabajadoresFeriado();
        return "View";
    }

    public String prepareCreate() {
        current = new Feriado();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            obtenerTrabajadoresFeriado();
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("FeriadoCreated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleSalario").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Feriado) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("FeriadoUpdated"));
            obtenerTrabajadoresFeriado();
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleSalario").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Feriado) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("FeriadoDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleSalario").getString("PersistenceErrorOccured"));
        }
    }
    
    public DataModel getItems() {
        if (items == null) {
            List<Feriado> feriados = getFacade().findAll();
            Collections.sort(feriados);
            items = new ListDataModel(feriados);
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

    @FacesConverter(forClass = Feriado.class)
    public static class FeriadoControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            FeriadoController controller = (FeriadoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "feriadoController");
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
            if (object instanceof Feriado) {
                Feriado o = (Feriado) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }

    public DataModel getTrabajadoresFeriadoModel() {
        return trabajadoresFeriadoModel;
    }

    public List<Personal> getTrabajadoresFeriado() {
        return trabajadoresFeriado;
    }

    public String getUrlLinkRecibos() {
        return urlLinkRecibos;
    }
    
    
}