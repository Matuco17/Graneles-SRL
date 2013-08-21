package com.orco.graneles.jsf.salario;

import com.orco.graneles.domain.miscelaneos.TipoConceptoRecibo;
import com.orco.graneles.domain.miscelaneos.TipoRecibo;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.ConceptoRecibo;
import com.orco.graneles.domain.salario.ItemsSueldo;
import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.domain.salario.Sueldo;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.fileExport.ResumenRemuneracionesXLS;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import com.orco.graneles.model.salario.ConceptoReciboFacade;
import com.orco.graneles.model.salario.SueldoFacade;
import com.orco.graneles.vo.ItemSueldoVO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
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
    public static final int MAX_CONCEPTOS_REMUNERATIVOS = 20;

    private Sueldo current;
    private DataModel items = null;
    @EJB
    private SueldoFacade ejbFacade;
    
    @EJB
    private ConceptoReciboFacade conceptoReciboF;
    @EJB
    private FixedListFacade fixedListF;
    
    private int selectedItemIndex;
    
    private Periodo periodoDesdeFilter;
    private Periodo periodoHastaFilter;
    private Personal personalFilter;
    
    private BigDecimal totalRemunerativo;
    private BigDecimal totalDeductivo;
    private Integer totalHoras;
    
    private String[] conceptosRemunerativosDescripcion;
    private BigDecimal[] totalesConceptos;
    
    private String urlResumenRemuneracionesXLS;

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
            if (personalFilter != null){
                List<Sueldo> sueldos = getFacade().obtenerSueldos(personalFilter, periodoDesdeFilter, periodoHastaFilter);
                Collections.sort(sueldos);

                totalesConceptos = new BigDecimal[getConceptosRemunerativosDescripcion().length];
                Arrays.fill(totalesConceptos, BigDecimal.ZERO);
                totalRemunerativo = BigDecimal.ZERO;
                totalDeductivo = BigDecimal.ZERO;
                totalHoras = 0;
                
                for (Sueldo s: sueldos){
                   for (ItemsSueldo is : s.getItemsSueldoCollection()){
                       if (is.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.REMUNERATIVO)){
                            int indiceConcepto = Arrays.binarySearch(getConceptosRemunerativosDescripcion(), is.getConceptoRecibo().getConcepto().toUpperCase());
                            totalesConceptos[indiceConcepto] = totalesConceptos[indiceConcepto].add(is.getValorCalculado());
                            totalRemunerativo = totalRemunerativo.add(is.getValorCalculado());
                       } if (is.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.DEDUCTIVO)){
                            totalDeductivo = totalDeductivo.add(is.getValorCalculado());
                       }
                   } 
                   totalHoras += s.getCantidadHorasSueldo();
                }
                
                urlResumenRemuneracionesXLS = (new ResumenRemuneracionesXLS(personalFilter, periodoDesdeFilter, periodoHastaFilter, sueldos, conceptosRemunerativosDescripcion, totalesConceptos, totalHoras)).generarArchivo("ResumenRemuneraciones" + personalFilter.getId());
              
                items = new ListDataModel(sueldos);
            }
        }
        return items;
    }

    public void recreateModel() {
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

    public String[] getConceptosRemunerativosDescripcion() {
        if (conceptosRemunerativosDescripcion == null){
            Set<String> agrupacionDescripcion = new HashSet<String>();
            
            for (ConceptoRecibo cr : conceptoReciboF.obtenerConceptos(fixedListF.find(TipoRecibo.HORAS), fixedListF.find(TipoConceptoRecibo.REMUNERATIVO))){
                if (!agrupacionDescripcion.contains(cr.getConcepto().toUpperCase())){
                    agrupacionDescripcion.add(cr.getConcepto().toUpperCase());
                }
            }
            
            conceptosRemunerativosDescripcion = new String[agrupacionDescripcion.size()];
            
            int i = 0;
            for (String concepto : agrupacionDescripcion){
                conceptosRemunerativosDescripcion[i] = concepto;
                i++;
            }
            
            Arrays.sort(conceptosRemunerativosDescripcion);
        }
        return conceptosRemunerativosDescripcion;
    }

    public BigDecimal[] getTotalesConceptos() {
        return totalesConceptos;
    }

    public String getUrlResumenRemuneracionesXLS() {
        return urlResumenRemuneracionesXLS;
    }

    public Integer getTotalHoras() {
        return totalHoras;
    }

    public void setTotalHoras(Integer totalHoras) {
        this.totalHoras = totalHoras;
    }

    
    
    
    
}