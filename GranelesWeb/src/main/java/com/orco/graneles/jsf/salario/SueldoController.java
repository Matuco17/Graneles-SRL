package com.orco.graneles.jsf.salario;

import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.domain.salario.Sueldo;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.salario.SueldoFacade;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
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

@ManagedBean(name = "sueldoController")
@SessionScoped
public class SueldoController implements Serializable {

    private Sueldo current;
    private DataModel items = null;
    @EJB
    private SueldoFacade ejbFacade;
    private int selectedItemIndex;
    
    private Periodo periodoDesdeFilter;
    private Periodo periodoHastaFilter;
    private Personal personalFilter;
    
    private BigDecimal totalRemunerativo;
    private BigDecimal totalDeductivo;
    

    public SueldoController() {
    }

    public void init() {
        recreateModel();
        
        JsfUtil.minimoRolRequerido(Grupo.ROL_USUARIO);
    }

    public void seleccionarPersonal(ValueChangeEvent e){
        personalFilter = (Personal) e.getNewValue();
    }
   
    private SueldoFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public DataModel getItems() {
        if (items == null) {
            if (personalFilter != null || periodoDesdeFilter != null || periodoHastaFilter != null){
                List<Sueldo> sueldos = getFacade().obtenerSueldos(personalFilter, periodoDesdeFilter, periodoHastaFilter);
                Collections.sort(sueldos);

                totalDeductivo = BigDecimal.ZERO;
                totalRemunerativo = BigDecimal.ZERO;
                for (Sueldo s: sueldos){
                    totalDeductivo = totalDeductivo.add(s.getTotalDeductivo(true));
                    totalRemunerativo = totalRemunerativo.add(s.getTotalRemunerativo(true));
                }

                items = new ListDataModel(sueldos);
            }
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

    @FacesConverter(forClass = Sueldo.class)
    public static class SueldoControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SueldoController controller = (SueldoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "sueldoController");
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
            if (object instanceof Sueldo) {
                Sueldo o = (Sueldo) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }

    public Periodo getPeriodoDesdeFilter() {
        return periodoDesdeFilter;
    }

    public void setPeriodoDesdeFilter(Periodo periodoDesdeFilter) {
        this.periodoDesdeFilter = periodoDesdeFilter;
    }

    public Periodo getPeriodoHastaFilter() {
        return periodoHastaFilter;
    }

    public void setPeriodoHastaFilter(Periodo periodoHastaFilter) {
        this.periodoHastaFilter = periodoHastaFilter;
    }

    public Personal getPersonalFilter() {
        return personalFilter;
    }

    public void setPersonalFilter(Personal personalFilter) {
        this.personalFilter = personalFilter;
    }

    public BigDecimal getTotalRemunerativo() {
        return totalRemunerativo;
    }

    public void setTotalRemunerativo(BigDecimal totalRemunerativo) {
        this.totalRemunerativo = totalRemunerativo;
    }

    public BigDecimal getTotalDeductivo() {
        return totalDeductivo;
    }

    public void setTotalDeductivo(BigDecimal totalDeductivo) {
        this.totalDeductivo = totalDeductivo;
    }

    
    
}