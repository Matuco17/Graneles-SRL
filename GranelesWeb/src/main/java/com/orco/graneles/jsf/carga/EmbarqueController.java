package com.orco.graneles.jsf.carga;

import com.orco.graneles.domain.carga.*;
import com.orco.graneles.domain.facturacion.Empresa;
import com.orco.graneles.domain.miscelaneos.FixedList;
import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.personal.Tarea;
import com.orco.graneles.domain.salario.TipoJornal;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.carga.CargaPreviaFacade;
import com.orco.graneles.model.carga.CargaTurnoFacade;
import com.orco.graneles.model.carga.EmbarqueCargadorFacade;
import com.orco.graneles.model.carga.EmbarqueFacade;
import com.orco.graneles.model.carga.TurnoEmbarqueFacade;
import com.orco.graneles.model.personal.PersonalFacade;
import com.orco.graneles.model.salario.ConceptoReciboFacade;
import com.orco.graneles.reports.EmbarquePlanoCarga;
import com.orco.graneles.reports.PlanillaTrabajadoresTurno;
import com.orco.graneles.reports.ResumenCargasPorCoordinador;
import com.orco.graneles.reports.ResumenCargasPorTurno;
import com.orco.graneles.vo.TrabajadorTurnoEmbarqueVO;
import java.io.IOException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean(name = "embarqueController")
@SessionScoped
public class EmbarqueController implements Serializable {

    private Embarque current;
    private DataModel items = null;
    @EJB
    private EmbarqueFacade ejbFacade;
    @EJB
    private CargaPreviaFacade cargaPreviaF;
    @EJB
    private CargaTurnoFacade cargaTurnoF;
    @EJB
    private PersonalFacade personalF;
    @EJB
    private TurnoEmbarqueFacade turnoEmbarqueF;
    @EJB
    private ConceptoReciboFacade conceptoReciboF;
    @EJB
    private EmbarqueCargadorFacade embarqueCargadorF;
    
    
    
    private int selectedItemIndex;
    private List<CargaPrevia> cargasPrevias;
    private DataModel cargasPreviasModel;
    private DataModel listaTurnos;

    //Variables de turno
    private List<CargaTurno> cargas;
    private DataModel cargasModel;
    private List<Personal> trabajadores;
    private TrabajadorTurnoEmbarqueVO currentTTE;
    private List<TrabajadorTurnoEmbarqueVO> trabajadoresTurno;
    private DataModel trabajadoresTurnoModel;
    private TrabajadorTurnoEmbarqueVO selectedTTE;
    private TurnoEmbarque currentTE;
    private boolean editarTurno;

    //Variables de Archivo
    private DataModel archivosModel;
    private List<ArchivoEmbarque> listaArchivos;
    private UploadedFile currentFile;
    
    //Reportes Embarque
    private String urlReportePlano;
    private String urlReporteResumenCargasTurnos;
    private String urlReporteResumenCargasCoordinador;
    //Reportes Turno
    private String urlReportePlanillaTrabajadores;
    
    //Cargadores del Embarque
    private EmbarqueCargador currentEC;
    private List<EmbarqueCargador> cargadores;
    private DataModel cargadoresModel;
    private List<Empresa> itemsCargadoresSelectOne;
    
    
    public EmbarqueController() {
    }

    private void recreateModel() {
        items = null;
    }
    
    private void recreateModelEmbarqueIndividual(){
        cargasPrevias = null;
        archivosModel = null;
        cargas = null;
        cargasModel = null;
        cargasPreviasModel = null;
        trabajadores = null;
        currentTTE = null;
        listaTurnos = null;
        trabajadoresTurno = null;
        trabajadoresTurnoModel = null;
        selectedTTE = null;
        currentTE = null;
        currentFile = null;
        listaArchivos = null;
        urlReportePlano = null;
        urlReporteResumenCargasTurnos = null;
        urlReporteResumenCargasCoordinador = null;
        currentEC = null;
        cargadores = null;
        cargadoresModel = null;
        itemsCargadoresSelectOne = null;
    }
    
    private void tratarDeLevantarCarga(){
        if (getSelected().getBuque() != null && getSelected().getMercaderia() != null){
            cargasPrevias = cargaPreviaF.obtenerCargasPrevias(getSelected().getBuque(), getSelected().getMercaderia(), getSelected());
            cargasPreviasModel = null;
        } else {
            cargasPreviasModel = null;
            cargasPrevias = null;
        }
    }
    
   
    public void seleccionarBuque(ValueChangeEvent e){
        current.setBuque((Buque) e.getNewValue());
        tratarDeLevantarCarga();
    }
    
    public void seleccionarMercaderia(ValueChangeEvent e){
        current.setMercaderia((Mercaderia) e.getNewValue());
        tratarDeLevantarCarga();
    }
 
        
    public void init() {
        recreateModel();
    }

    public Embarque getSelected() {
        if (current == null) {
            current = ejbFacade.crearNuevoEmbarque();
            selectedItemIndex = -1;
        }
        return current;
    }
    
    public void setSelected(Embarque selected){
        current = selected;
    }
    
    public boolean validarCampos(){
        boolean validar = true;
        if (getSelected().getCodigo() == null){
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundleCarga").getString("EmbarqueRequiredMessage_codigo"));
            validar = false;
        }
        
        if (getSelected().getBuque() == null){
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundleCarga").getString("EmbarqueRequiredMessage_buque"));
            validar = false;
        }
        
        if (getSelected().getMuelle() == null){
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundleCarga").getString("EmbarqueRequiredMessage_muelle"));
            validar = false;
        }
        
        return validar;
    }

    private EmbarqueFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        if (current != null){
            recreateModelEmbarqueIndividual();
            //current = (Embarque) getItems().getRowData();
            //selectedItemIndex = getItems().getRowIndex();
        return "View";
        } else {
            return null;
        }
    }

    public String prepareCreate() {
        recreateModelEmbarqueIndividual();
        current = ejbFacade.crearNuevoEmbarque();
        selectedItemIndex = -1;
        return "Create";
    } 
        
    public String create() {
        try {
            if (validarCampos()){
                current.setCargaPreviaCollection(cargasPrevias);
                getFacade().create(current);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("EmbarqueCreated"));
                return "View";
            } else {
                return null;
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleCarga").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        if (current != null){
            recreateModelEmbarqueIndividual();
            //current = (Embarque) getItems().getRowData();
            //selectedItemIndex = getItems().getRowIndex();
            return "Edit";
        } else {
            return null;
        }
    }

    public String update() {
        try {
            if (validarCampos()){
                current.setCargaPreviaCollection(cargasPrevias);
                current.setEmbarqueCargadoresCollection(cargadores);
                getFacade().edit(current);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("EmbarqueUpdated"));
                return "View";
            } else {
                return null;
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleCarga").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        if (current != null){
            //current = (Embarque) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("EmbarqueDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleCarga").getString("PersistenceErrorOccured"));
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = new ListDataModel(getFacade().findAll());;
        }
        return items;
    }
    
    public void generarReportePlano(){
        urlReportePlano = (new EmbarquePlanoCarga(current)).obtenerReportePDF();
    }
    
    public void generarReporteResumenCargasTurnos(){
        urlReporteResumenCargasTurnos = (new ResumenCargasPorTurno(current)).obtenerReportePDF();
    }
    
    public void generarReporteResumenCargasCoordinador(){
        urlReporteResumenCargasCoordinador = (new ResumenCargasPorCoordinador(current)).obtenerReportePDF();
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    @FacesConverter(forClass = Embarque.class)
    public static class EmbarqueControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EmbarqueController controller = (EmbarqueController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "embarqueController");
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
            if (object instanceof Embarque) {
                Embarque o = (Embarque) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }

    
    public List<CargaPrevia> getCargasPrevias() {
        if (cargasPrevias == null){
            tratarDeLevantarCarga();
        }
        return cargasPrevias;
    }

    public DataModel getCargasPreviasModel() {
        if (cargasPreviasModel == null)
            cargasPreviasModel = new ListDataModel(getCargasPrevias());
        return cargasPreviasModel;
    }

    public DataModel getListaTurnos() {
        if (listaTurnos == null){
            List<TurnoEmbarque> turnos = new ArrayList<TurnoEmbarque>(getSelected().getTurnoEmbarqueCollection());
            Collections.sort(turnos);
            listaTurnos = new ListDataModel(turnos);
        }
        return listaTurnos;
    }

    public void setListaTurnos(DataModel listaTurnos) {
        this.listaTurnos = listaTurnos;
    }

    public String getUrlReportePlano() {
        return urlReportePlano;
    }

    public String getUrlReporteResumenCargasTurnos() {
        return urlReporteResumenCargasTurnos;
    }

    public String getUrlReporteResumenCargasCoordinador(){
        return urlReporteResumenCargasCoordinador;
    }
    
    
    
    /*
     * Funcionalidades de turno Embarque dentro
     */
    public void recreateModelTurno() {
        this.trabajadoresTurno = null;
        this.trabajadoresTurnoModel = null;
        this.currentTTE = null;
        this.selectedTTE = null;
        this.cargas = null;
        this.cargasModel = null;
        this.urlReportePlanillaTrabajadores = null;
    }
    
    public boolean validarTurno(){
        boolean validar = true;
        
        if (currentTE.getTurno() == null){
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundleCarga").getString("TurnoEmbarqueRequiredMessage_turno"));
            validar = false;
        }

        if (currentTE.getTipo() == null){
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundleCarga").getString("TurnoEmbarqueRequiredMessage_tipo"));
            validar = false;
        }
        
        if (currentTE.getFecha() == null){
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundleCarga").getString("TurnoEmbarqueRequiredMessage_fecha"));
            validar = false;
        }
        
        return validar;
    }
        
    public String prepareEditTE() {
        currentTE = (TurnoEmbarque) getListaTurnos().getRowData();
        recreateModelTurno();
        editarTurno = true;
        return "CreateEditTurnoEmbarque";
    }
    
    public String prepareViewTE() {
        currentTE = (TurnoEmbarque) getListaTurnos().getRowData();
        recreateModelTurno();
        editarTurno = false;
        return "CreateEditTurnoEmbarque";
    }
    
    public String prepareCreateTE() {
        currentTE = turnoEmbarqueF.crearNuevoTurnoEmbarque(current);
        recreateModelTurno();
        editarTurno = true;
        return "CreateEditTurnoEmbarque";
    }
    
    public void destroyTE(){
        currentTE = (TurnoEmbarque) getListaTurnos().getRowData();
        try {
            turnoEmbarqueF.remove(currentTE);
            current.getTurnoEmbarqueCollection().remove(currentTE);
            listaTurnos = null;
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleCarga").getString("TurnoEmbarqueDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleCarga").getString("PersistenceErrorOccured"));
        }
    }
    
    public String saveTE(){
        if (editarTurno){
            //realizo la validacion de los datos obligatorios
            if (currentTE.getTipo() == null){
                  JsfUtil.addErrorMessage("El campo tipo de Jornal es obligatorio");
                  return null;
            }
            
            currentTE.setCargaTurnoCollection(cargas);
            
            List<TrabajadoresTurnoEmbarque> trabajadores = new ArrayList<TrabajadoresTurnoEmbarque>();
            for (TrabajadorTurnoEmbarqueVO tteVO : trabajadoresTurno){
                trabajadores.add(tteVO.getTte());
            }
            
            
            for (TrabajadoresTurnoEmbarque tte : trabajadores){
                tte.setPlanilla(currentTE);
            }
            currentTE.setTrabajadoresTurnoEmbarqueCollection(trabajadores);
            
            if (currentTE.getId() == null)
                current.getTurnoEmbarqueCollection().add(currentTE);
            
            turnoEmbarqueF.persist(currentTE);
            
            listaTurnos = null;            
        }
        return "Edit";
    }
    
    public TurnoEmbarque getCurrentTE() {
        return currentTE;
    }

    public void setCurrentTE(TurnoEmbarque currentTE) {
        this.currentTE = currentTE;
        recreateModelTurno();
    }
    
    public TrabajadorTurnoEmbarqueVO getCurrentTTE() {
        if (currentTTE == null){
            currentTTE = new TrabajadorTurnoEmbarqueVO(new TrabajadoresTurnoEmbarque(), BigDecimal.ZERO);
            if (currentTE.getTurno() != null){
                //Por ahora seteo las horas por defecto asi, capaz que despues se lo pongo mejor
                currentTTE.getTte().setDesde(new Integer(currentTE.getTurno().getDescripcion().substring(0, 2)));
                currentTTE.getTte().setHasta(new Integer(currentTE.getTurno().getDescripcion().substring(3)));
            }
            currentTTE.getTte().setPlanilla(currentTE);
        }
        return currentTTE;
    }

    public void setCurrentTTE(TrabajadorTurnoEmbarqueVO currentTTE) {
        this.currentTTE = currentTTE;
    }

    public void seleccionarTurno(ValueChangeEvent e){
        getCurrentTE().setTurno((FixedList) e.getNewValue());
    }
    
    public void seleccionarTipoJornal(ValueChangeEvent e){
        getCurrentTE().setTipo((TipoJornal) e.getNewValue());
    }
    
    public void seleccionarFecha(DateSelectEvent event) {  
        getCurrentTE().setFecha(event.getDate());  
    }  
    
    public void seleccionarPersonal(ValueChangeEvent e){
        Personal personalSeleccionado = (Personal) e.getNewValue();
        getCurrentTTE().getTte().setPersonal(personalSeleccionado);
        getCurrentTTE().getTte().setPlanilla(currentTE);
        if (personalSeleccionado.getCategoriaPrincipal() != null)
            getCurrentTTE().getTte().setCategoria(personalSeleccionado.getCategoriaPrincipal());
        
        //TODO: ASIGNAR DINAMICAMENTE LAS TAREAS PERMITIDAS DE ACUERDO AL SALARIO BASICO CARGADO:
        if (currentTE.getTurno() != null){
            //Por ahora seteo las horas por defecto asi, capaz que despues se lo pongo mejor
            currentTTE.getTte().setDesde(new Integer(currentTE.getTurno().getDescripcion().substring(0, 2)));
            currentTTE.getTte().setHasta(new Integer(currentTE.getTurno().getDescripcion().substring(3)));
        }
          
        actualizarSueldoTTE(getCurrentTTE());
    }
    
    public void seleccionarTarea(ValueChangeEvent e){
        getCurrentTTE().getTte().setTarea((Tarea) e.getNewValue());
        actualizarSueldoTTE(getCurrentTTE());
    }
    
    public void seleccionarCategoria(ValueChangeEvent e){
        getCurrentTTE().getTte().setCategoria((Categoria) e.getNewValue());
        actualizarSueldoTTE(getCurrentTTE());
    }
        
    public void seleccionarDesde(ValueChangeEvent e){
        getCurrentTTE().getTte().setDesde((Integer) e.getNewValue());
        actualizarSueldoTTE(getCurrentTTE());
    }
    
    public void seleccionarHasta(ValueChangeEvent e){
        getCurrentTTE().getTte().setHasta((Integer) e.getNewValue());
        actualizarSueldoTTE(getCurrentTTE());
    }
    
    private void actualizarSueldoTTE(TrabajadorTurnoEmbarqueVO tteVO){
        try {
            tteVO.setValorTurno(new BigDecimal(conceptoReciboF.calcularDiaTrabajadoTTE(tteVO.getTte(), true)));
        } catch (Exception e) {
            //Por ahora no hago nada ya que creo que no es necesario
        }
    }
    
    public void agregarTrabajador(){
        if (currentTTE.getTte().getCategoria() != null &&
            currentTTE.getTte().getHoras() != null &&
            currentTTE.getTte().getPersonal() != null &&
            currentTTE.getTte().getTarea() != null){
        
            getTrabajadoresTurno().add(0, currentTTE);
            currentTTE = null;
            trabajadoresTurnoModel = null;
        } else {
            //TODO: enviar mensaje de que falta algun que otro campo
        }
    }
        
    public void eliminarTrabajador() {
        selectedItemIndex = getTrabajadoresTurnoModel().getRowIndex();
        getTrabajadoresTurnoModel().setRowIndex(-1);
        if (selectedItemIndex >= 0){
            getTrabajadoresTurno().remove(selectedItemIndex);

            trabajadoresTurnoModel = null;
            currentTTE = null;
            selectedTTE = null;
        }
    }
    
    public void agregarCargaTurno(){
        cargas.add(cargaTurnoF.cargarNuevaPorBuque(currentTE));
        cargasModel = null;
    }
    
    public void seleccionarTTE(){
        selectedTTE = (TrabajadorTurnoEmbarqueVO) getTrabajadoresTurnoModel().getRowData();
    }
    
    public void generarPlanillaTrabajdores(){
        urlReportePlanillaTrabajadores = 
                (new PlanillaTrabajadoresTurno(currentTE,
                 turnoEmbarqueF.obtenerTteVos(currentTE)))
                 .obtenerReportePDF();
    }
    
    public List<Personal> getTrabajadores(){
        if (trabajadores == null){
            trabajadores = personalF.findAll();
            Collections.sort(trabajadores, new ComparadorPersonal());
        }
        return trabajadores;
    }
    

    public DataModel getCargasModel() {
        if (cargasModel == null)
            cargasModel = new ListDataModel(getCargas());
        return cargasModel;
    }

    public List<TrabajadorTurnoEmbarqueVO> getTrabajadoresTurno() {
        if (trabajadoresTurno == null){
            if (getCurrentTE().getTrabajadoresTurnoEmbarqueCollection() != null){
                trabajadoresTurno = new ArrayList<TrabajadorTurnoEmbarqueVO>();
                for (TrabajadoresTurnoEmbarque tte : getCurrentTE().getTrabajadoresTurnoEmbarqueCollection()){
                    trabajadoresTurno.add(new TrabajadorTurnoEmbarqueVO(tte, 
                            new BigDecimal(conceptoReciboF.calcularDiaTrabajadoTTE(tte, true))));
                }
            } else {
               trabajadoresTurno = new ArrayList<TrabajadorTurnoEmbarqueVO>();
            }            
        }
        return trabajadoresTurno;
    }

    public DataModel getTrabajadoresTurnoModel() {
        if (trabajadoresTurnoModel == null){
            trabajadoresTurnoModel = new ListDataModel(getTrabajadoresTurno());
        }
        return trabajadoresTurnoModel;
    }

    public TrabajadorTurnoEmbarqueVO getSelectedTTE() {
        return selectedTTE;
    }

    public void setSelectedTTE(TrabajadorTurnoEmbarqueVO selectedTTE) {
        this.selectedTTE = selectedTTE;
    }
    
    private class ComparadorPersonal implements Comparator<Personal>{
        @Override
        public int compare(Personal o1, Personal o2) {
            return o1.getCuil().compareTo(o2.getCuil());
        }
    }
    
    public boolean isEditarTurno() {
        return editarTurno;
    }
    
    public List<CargaTurno> getCargas() {
        if (cargas == null && currentTE != null){
            this.cargas = cargaTurnoF.obtenerCargas(getCurrentTE());
        }
        return cargas;
    }

    public String getUrlReportePlanillaTrabajadores() {
        return urlReportePlanillaTrabajadores;
    }
  
    
     /*
     * Fin Funcionalidades turno Embarque
     */

    /*
     * Comienzo de funcionalidades de Archivos del embarque
     */
    
    public void subirArchivo(){
        if (getCurrentFile() != null && StringUtils.isNotEmpty(getCurrentFile().getFileName())){
            try {
                ejbFacade.subirArchivo(currentFile.getInputstream(), currentFile.getFileName(), current);
            } catch (IOException ex) {
                Logger.getLogger(EmbarqueController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        listaArchivos = null;
        archivosModel = null;
    }
    
    public void eliminarArchivo(){
        if (archivosModel.getRowData() != null){
            getSelected().getArchivoEmbarqueCollection().remove((ArchivoEmbarque) archivosModel.getRowData());
            listaArchivos = null;
            archivosModel = null;
        }
    }    
        
    public List<ArchivoEmbarque> getListaArchivos() {
        if (listaArchivos == null){
            listaArchivos = new ArrayList<ArchivoEmbarque>(getSelected().getArchivoEmbarqueCollection());
            Collections.sort(listaArchivos);
        }
        return listaArchivos;
    }
    
    public DataModel getArchivosModel() {
        if (archivosModel == null){
            archivosModel = new ListDataModel(getListaArchivos());
        }
        return archivosModel;
    }

    public UploadedFile getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(UploadedFile currentFile) {
        this.currentFile = currentFile;
    }
    
    /*
     * Fin de las funcionalidades del Archivos del embarque
     */

 
  /*
   * Inicio de la carga de cargadores del embarque con los minimos y maximos
   */
    public void agregarCargador(){
        if (getCurrentEC().getCargador() != null){
            for (EmbarqueCargador ec : getListaCargadores()){
                if (getCurrentEC().getCargador().getId().equals(ec.getCargador().getId())){
                    return;
                }
            }
            
            cargadores.add(currentEC);
            currentEC = null;
            cargadoresModel = null;
        }
    }
    
    public void eliminarCargador(){
        if (cargadoresModel.getRowData() != null){
            cargadores.remove(cargadoresModel.getRowIndex());
            cargadoresModel = null;
            
        }
    }
    
    public List<EmbarqueCargador> getListaCargadores(){
        if (cargadores == null){
            if (getSelected().getEmbarqueCargadoresCollection() != null){
                cargadores = new ArrayList<EmbarqueCargador>(getSelected().getEmbarqueCargadoresCollection());
            } else {
                cargadores = new ArrayList<EmbarqueCargador>();
            }
            
        }
        return cargadores;
    }
  
    public DataModel getCargadoresModel(){
        if (cargadoresModel == null){
            cargadoresModel = new ListDataModel(getListaCargadores());
        }
        return cargadoresModel;
    }

    public EmbarqueCargador getCurrentEC() {
        if (currentEC == null){
            currentEC = new EmbarqueCargador();
            currentEC.setEmbarque(current);
        }
        return currentEC;
    }

    public void setCurrentEC(EmbarqueCargador currentEC) {
        this.currentEC = currentEC;
    }

    public List<Empresa> getItemsCargadoresSelectOne() {
        if (itemsCargadoresSelectOne == null){
            itemsCargadoresSelectOne = new ArrayList<Empresa>();
            for (EmbarqueCargador ec : getListaCargadores()){
                itemsCargadoresSelectOne.add(ec.getCargador());
            }
        }
        return itemsCargadoresSelectOne;
    }
    
    
  /**
   * Fin de la carga de cargadores
   */

}