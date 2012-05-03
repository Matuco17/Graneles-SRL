package com.orco.graneles.jsf.personal;

import com.orco.graneles.domain.personal.Accidentado;
import com.orco.graneles.domain.personal.JornalCaido;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.personal.AccidentadoFacade;
import com.orco.graneles.model.personal.JornalCaidoFacade;
import com.orco.graneles.reports.ReciboJornalCaído;
import com.orco.graneles.vo.AccidentadoVO;

import java.io.Serializable;
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

@ManagedBean(name = "accidentadoController")
@SessionScoped
public class AccidentadoController implements Serializable {

    private Accidentado current;
    private Personal currentPersonal;
    private AccidentadoVO currentV0;
    private DataModel items = null;
    
    @EJB
    private AccidentadoFacade ejbFacade;
    
    @EJB
    private JornalCaidoFacade jornalCaidoF;
    
    private int selectedItemIndex;

    private List<JornalCaido> jornalesCaidos;
    private DataModel jornalesCaidosModel;
    private JornalCaido currentJC;
    
    
    public AccidentadoController() {
    }

    public void init() {
        recreateModel();
    }

    public Accidentado getSelected() {
        if (current == null) {
            current = new Accidentado();
            selectedItemIndex = -1;
        }
        return current;
    }
    
    public void setSelected(Accidentado selected){
        current = selected;
    }
    
    public AccidentadoVO getSelectedVO(){
        if (currentV0 == null) {
            currentV0 = new AccidentadoVO(getSelected());
        }
        return currentV0;
    }

    public void crearDatosAccidentado(){
        if (currentPersonal != null){
            currentV0.getAccidentado().setPersonal(currentPersonal);
            currentV0 = ejbFacade.completarAccidentado(currentV0.getAccidentado());
        }
    }
    
    private AccidentadoFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }
    
    public void recalcularAccidentado(){
        currentV0 = ejbFacade.completarAccidentado(currentV0.getAccidentado());
    }

    public String prepareView() {
        //current = (Accidentado) getItems().getRowData();
        //selectedItemIndex = getItems().getRowIndex();
        if (current != null){
            
            //Genero cada uno de los recibos
            for (JornalCaido jc : current.getJornalesCaidosCollection()){
                ReciboJornalCaído recibo = new ReciboJornalCaído(jc);
                jc.setUrlRecibo(recibo.obtenerReportePDF());
            }        
            
            currentV0 = ejbFacade.completarAccidentado(current);
            
            return "View";
        } else {
            return null;
        }
    }

    public String prepareCreate() {
        currentV0 = null;
        selectedItemIndex = -1;
        return "Create";
    }
    
    public String prepareEdit() {
        if (current != null){
            //current = (Accidentado) getItems().getRowData();
            currentV0 = ejbFacade.completarAccidentado(current);
            //selectedItemIndex = getItems().getRowIndex();
            return "Edit";
        } else {
            return null;
        }
    }
    
    public boolean validarCreateUpdate(){
        if (getSelectedVO().getAccidentado().getPersonal() == null){
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundlePersonal").getString("AccidentadoRequiredMessage_personal"));
            return false;
        }
        if (getSelectedVO().getAccidentado().getTarea() == null){
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundlePersonal").getString("AccidentadoRequiredMessage_tarea"));
            return false;
        }
        if (getSelectedVO().getAccidentado().getCategoria() == null){
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundlePersonal").getString("AccidentadoRequiredMessage_categoria"));
            return false;
        }
        if (getSelectedVO().getAccidentado().getBruto() == null){
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundlePersonal").getString("AccidentadoRequiredMessage_bruto"));
            return false;
        }
        //Paso todas la validaciones
        return true;
    }
    
    public String create() {
        try {
            if (validarCreateUpdate()){
                getFacade().create(currentV0.getAccidentado());
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundlePersonal").getString("AccidentadoCreated"));
                return "View";
            } else {
                return null;
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundlePersonal").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String update() {
        try {
            if (validarCreateUpdate()){
                //currentV0.getAccidentado().setJornalesCaidosCollection(jornalesCaidos);
                getFacade().edit(currentV0.getAccidentado());
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundlePersonal").getString("AccidentadoUpdated"));
                return "View";
            } else {
                return null;
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundlePersonal").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        if (current != null){
            //current = (Accidentado) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundlePersonal").getString("AccidentadoDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundlePersonal").getString("PersistenceErrorOccured"));
        }
    }


    public DataModel getItems() {
        if (items == null) {
            List<Accidentado> accidentados = getFacade().findAll();
            Collections.sort(accidentados, new ComparadorAccidentado());
            items = new ListDataModel(accidentados);
        }
        return items;
    }

    private void recreateModel() {
        items = null;
        currentPersonal = null;
        currentJC = null;
        jornalesCaidos = null;
        jornalesCaidosModel = null;
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    @FacesConverter(forClass = Accidentado.class)
    public static class AccidentadoControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AccidentadoController controller = (AccidentadoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "accidentadoController");
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
            if (object instanceof Accidentado) {
                Accidentado o = (Accidentado) object;
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

    /*
     * JORNALES CAIDOS
     */
    
    public void agregarJornalCaido(){
        if ((currentJC.getDesde() != null) 
            && (currentJC.getHasta() != null)
            && (currentJC.getDiaPago() != null)){
        
            jornalCaidoF.completarValor(currentJC);
            
            current.getJornalesCaidosCollection().add(currentJC);
            currentJC = null;
            jornalesCaidos = null;
            jornalesCaidosModel = null;
        }
    }
    
    public void borrarJornalCaido(){
        int rowindex = jornalesCaidosModel.getRowIndex();
        jornalesCaidos.remove(rowindex);
        jornalesCaidosModel = null;
    }
    
    public JornalCaido getCurrentJC() {
        if (currentJC == null){
            currentJC = new JornalCaido();
            currentJC.setAccidentado(current);
            currentJC.setDiaPago(new Date());
            currentJC.setDesde(current.getDesde());
            currentJC.setHasta(current.getHasta());
        }
        return currentJC;
    }

    public void setCurrentJC(JornalCaido currentJC) {
        this.currentJC = currentJC;
    }

    public List<JornalCaido> getJornalesCaidos() {
        if (jornalesCaidos == null && current != null && current.getJornalesCaidosCollection() != null){
            jornalesCaidos = new ArrayList<JornalCaido>(current.getJornalesCaidosCollection());
            Collections.sort(jornalesCaidos);
            Collections.reverseOrder();
        }
        return jornalesCaidos;
    }

    public DataModel getJornalesCaidosModel() {
        if (jornalesCaidosModel == null){
            jornalesCaidosModel = new ListDataModel(getJornalesCaidos());
        }
        return jornalesCaidosModel;
    }

    
    private class ComparadorAccidentado implements Comparator<Accidentado>{

        @Override
        public int compare(Accidentado o1, Accidentado o2) {
            if (o1.getHasta() != null && o2.getHasta() != null){
                return o2.getHasta().compareTo(o1.getHasta());
            } else if (o1.getHasta() == null && o2.getHasta() != null){
                return -1;
            } else if (o1.getHasta() != null && o2.getHasta() == null){
                return 1;
            } else {
                return o2.getDesde().compareTo(o1.getDesde());
            }
        }
    }     
    
}