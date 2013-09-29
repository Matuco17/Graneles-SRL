package com.orco.graneles.jsf.carga;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.fileExport.JornalesXLS;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.carga.TrabajadoresTurnoEmbarqueFacade;
import com.orco.graneles.vo.JornalVO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

@ManagedBean(name = "trabajadoresTurnoEmbarqueController")
@SessionScoped
public class TrabajadoresTurnoEmbarqueController implements Serializable {

    private TrabajadoresTurnoEmbarque current;
    private DataModel items = null;
    
    @EJB
    private TrabajadoresTurnoEmbarqueFacade ejbFacade;
    
    //datos del filtro
    private Categoria categoriaFilter;
    private Personal personalFilter;
    private Date desdeFilter;
    private Date hastaFilter;
    private Boolean incluirFeriadosFilter;
    
    private DataModel jornales;
    private BigDecimal totalJornalesBruto;
    private Integer totalHoras;
    
    private String urlJornalesXLS;

    public TrabajadoresTurnoEmbarqueController() {
    }

    public void init() {
        recreateModel();
       
        
        JsfUtil.minimoRolRequerido(Grupo.ROL_CONTADOR);
    }

    public void seleccionarPersonal(ValueChangeEvent e){
        personalFilter = (Personal) e.getNewValue();
    }
    
    public void buscarJornales(){
        List<JornalVO> jornalesVO = ejbFacade.getJornales(categoriaFilter, personalFilter, desdeFilter, hastaFilter, incluirFeriadosFilter);
        
        totalJornalesBruto = BigDecimal.ZERO;
        totalHoras = 0;
        for (JornalVO jornal : jornalesVO){
            totalJornalesBruto = totalJornalesBruto.add(jornal.getBruto());
            totalHoras += jornal.getHoras();
        }
        
        Collections.sort(jornalesVO);
        
        jornales = new ListDataModel(jornalesVO);
        
        urlJornalesXLS = (new JornalesXLS(categoriaFilter, personalFilter, desdeFilter, hastaFilter, incluirFeriadosFilter, totalHoras, totalJornalesBruto, jornalesVO))
                            .generarArchivo("Jornales_" + (new Date()).toString().replaceAll(" ", "_"));
    }
    
    
    public TrabajadoresTurnoEmbarque getSelected() {
        if (current == null) {
            current = new TrabajadoresTurnoEmbarque();
        }
        return current;
    }

    private TrabajadoresTurnoEmbarqueFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (TrabajadoresTurnoEmbarque) getItems().getRowData();
        return "View";
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

    @FacesConverter(forClass = TrabajadoresTurnoEmbarque.class)
    public static class TrabajadoresTurnoEmbarqueControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TrabajadoresTurnoEmbarqueController controller = (TrabajadoresTurnoEmbarqueController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "trabajadoresTurnoEmbarqueController");
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
            if (object instanceof TrabajadoresTurnoEmbarque) {
                TrabajadoresTurnoEmbarque o = (TrabajadoresTurnoEmbarque) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }

    public Categoria getCategoriaFilter() {
        return categoriaFilter;
    }

    public void setCategoriaFilter(Categoria categoriaFilter) {
        this.categoriaFilter = categoriaFilter;
    }

    public Personal getPersonalFilter() {
        return personalFilter;
    }

    public void setPersonalFilter(Personal personalFilter) {
        this.personalFilter = personalFilter;
    }

    public Date getDesdeFilter() {
        return desdeFilter;
    }

    public void setDesdeFilter(Date desdeFilter) {
        this.desdeFilter = desdeFilter;
    }

    public Date getHastaFilter() {
        return hastaFilter;
    }

    public void setHastaFilter(Date hastaFilter) {
        this.hastaFilter = hastaFilter;
    }

    public DataModel getJornales() {
        return jornales;
    }

    public BigDecimal getTotalJornalesBruto() {
        return totalJornalesBruto;
    }

    public Integer getTotalHoras() {
        return totalHoras;
    }

    public String getUrlJornalesXLS() {
        return urlJornalesXLS;
    }

    public Boolean getIncluirFeriadosFilter() {
        return incluirFeriadosFilter;
    }

    public void setIncluirFeriadosFilter(Boolean incluirFeriadosFilter) {
        this.incluirFeriadosFilter = incluirFeriadosFilter;
    }
      
}