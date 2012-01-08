package com.orco.graneles.jsf.salario;

import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.domain.salario.Sueldo;
import com.orco.graneles.fileExport.LibroSueldosAFIP;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.salario.PeriodoFacade;
import com.orco.graneles.reports.LibroSueldoReport;
import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

@ManagedBean(name = "periodoController")
@SessionScoped
public class PeriodoController implements Serializable {

    private Periodo current;
    private DataModel items = null;
    @EJB
    private PeriodoFacade ejbFacade;
    private int selectedItemIndex;

    // Datos para el formulario de Periodo (Libro de Sueldos
    private int mes;
    private int anio;
    private UploadedFile fileAltas;
    private UploadedFile filePlanillas;
    private UploadedFile fileCargaReg;
    private UploadedFile filePagoFeri;
    private StreamedContent fileLibro;
    private StreamedContent fileExportAfip;
    private String urlArchivoPDF;
    private String urlArchivoTxt;
    
    public PeriodoController() {
        
    }

    public void init() {
        Calendar calHoy = new GregorianCalendar();
        this.anio = calHoy.get(Calendar.YEAR);
        this.mes = calHoy.get(Calendar.MONTH) + 1;
        selectedItemIndex = -1;
    }

    public Periodo getSelected() {
        if (current == null) {
            current = new Periodo();
            selectedItemIndex = -1;
        }
        return current;
    }
    
    public boolean getVerPeriodoSeleccionado(){
        return selectedItemIndex > 0;
    }

    /**
     * Genera el libro de sueldos del perido actual
     */
    public void generarLibroPeriodo(){
        //Verifico que si el periodo ya tiene sueldos cargados, entonces genero el pdf y el archivo de AFIP
        if (current.getSueldoCollection() != null && current.getSueldoCollection().size() > 0){
            LibroSueldoReport libroSueldo = new LibroSueldoReport(current);
            urlArchivoPDF = libroSueldo.obtenerReportePDF();
            
        } else {
            urlArchivoPDF = null;
        }
    }
    
/**
     * Genera el libro de sueldos del perido actual
     */
    public void generarArchivoAfipPeriodo(){
        //Verifico que si el periodo ya tiene sueldos cargados, entonces genero el pdf y el archivo de AFIP
        if (current.getSueldoCollection() != null && current.getSueldoCollection().size() > 0){
            LibroSueldosAFIP libroSueldoAFIP = new LibroSueldosAFIP(new ArrayList<Sueldo>(current.getSueldoCollection()));
            urlArchivoTxt = libroSueldoAFIP.generarArchivo(current.getDescripcion());
        } else {
            urlArchivoTxt = null;
        }
    }
    

    private PeriodoFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Periodo) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Periodo();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("PeriodoCreated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleSalario").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Periodo) getItems().getRowData();
        selectedItemIndex = getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("PeriodoUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleSalario").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Periodo) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleSalario").getString("PeriodoDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleSalario").getString("PersistenceErrorOccured"));
        }
    }
    
    public void buscarPeriodo(){
        current = getFacade().verPeriodo(this.anio, this.mes);
        urlArchivoPDF = null;
        urlArchivoTxt = null;
        selectedItemIndex = 1;
    }
    
    public void procesarPeriodoConDatosSistema(){
        if (current != null && current.getDescripcion() != null && current.getDescripcion().length() > 0){
            try {
                getFacade().generarSueldosPeriodo(current);
                
                //Una vez subido todo, genero nuevamente el pdf
                urlArchivoPDF = null;
                urlArchivoTxt = null;

                JsfUtil.addSuccessMessage("Se ha guardado el periodo:" + current.getDescripcion() + " correctamente");
            } catch (Exception e) {
                JsfUtil.addErrorMessage(e.getMessage());
            }
        } else {
            JsfUtil.addErrorMessage("Seleccione un periodo con anterioridad");
        }
    }
    
    public void subirArchivosYProcesarPeriodo(){
        if (current != null && current.getDescripcion() != null && current.getDescripcion().length() > 0){
            try {

                //Si tengo datos, entonces los subo, sino solamente actualizo los campos
                if (fileAltas.getFileName() != null && fileAltas.getFileName().length() > 3
                    && filePlanillas.getFileName() != null && filePlanillas.getFileName().length() > 3
                    && fileCargaReg.getFileName() != null && fileCargaReg.getFileName().length() > 3
                    && filePagoFeri.getFileName() != null && filePagoFeri.getFileName().length() > 3){ 
                    //Llamo a completar el periodo
                    getFacade().completarPeríodo(fileAltas.getInputstream(),
                                            filePlanillas.getInputstream(), 
                                            fileCargaReg.getInputstream(), 
                                            filePagoFeri.getInputstream(),
                                            current);
                } else {
                    getFacade().edit(current);
                }

                //Una vez subido todo, genero nuevamente el pdf
                urlArchivoPDF = null;
                urlArchivoTxt = null;

                JsfUtil.addSuccessMessage("Se ha guardado el periodo:" + current.getDescripcion() + " correctamente");
            } catch (IOException e) {
                JsfUtil.addErrorMessage(e.getMessage());
            }
        } else {
            JsfUtil.addErrorMessage("Seleccione un periodo con anterioridad");
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
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    @FacesConverter(forClass = Periodo.class)
    public static class PeriodoControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PeriodoController controller = (PeriodoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "periodoController");
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
            if (object instanceof Periodo) {
                Periodo o = (Periodo) object;
                return getStringKey(o.getId());
            } else {
                return null;
            }
        }
    }
    
    

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }
    
    public UploadedFile getFileAltas() {
        return fileAltas;
    }

    public void setFileAltas(UploadedFile fileAltas) {
        this.fileAltas = fileAltas;
    }

    public UploadedFile getFileCargaReg() {
        return fileCargaReg;
    }

    public void setFileCargaReg(UploadedFile fileCargaReg) {
        this.fileCargaReg = fileCargaReg;
    }

    public UploadedFile getFilePagoFeri() {
        return filePagoFeri;
    }

    public void setFilePagoFeri(UploadedFile filePagoFeri) {
        this.filePagoFeri = filePagoFeri;
    }

    public UploadedFile getFilePlanillas() {
        return filePlanillas;
    }

    public void setFilePlanillas(UploadedFile filePlanillas) {
        this.filePlanillas = filePlanillas;
    }
    
    public StreamedContent getFileLibro() {
        return fileLibro;
    }

    public void setFileLibro(StreamedContent fileLibro) {
        this.fileLibro = fileLibro;
    }
     
    public StreamedContent getFileExportAfip() {
        return fileExportAfip;
    }

    public void setFileExportAfip(StreamedContent fileExportAfip) {
        this.fileExportAfip = fileExportAfip;
    }
    
    public String getUrlArchivoPDF() {
        return urlArchivoPDF;
    }

    public void setUrlArchivoPDF(String urlArchivoPDF) {
        this.urlArchivoPDF = urlArchivoPDF;
    }

    public String getUrlArchivoTxt() {
        return urlArchivoTxt;
    }

    public void setUrlArchivoTxt(String urlArchivoTxt) {
        this.urlArchivoTxt = urlArchivoTxt;
    }

    public Periodo getCurrent() {
        return current;
    }
}