package com.orco.graneles.jsf.salario;

import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.domain.salario.Sueldo;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.fileExport.LibroSueldosAFIP;
import com.orco.graneles.fileExport.LibroSueldosAFIPv34;
import com.orco.graneles.fileExport.ProyeccionSACyVacacionesXLS;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.salario.LibroExcelFacade;
import com.orco.graneles.model.salario.PeriodoFacade;
import com.orco.graneles.reports.CierreMesReport;
import com.orco.graneles.reports.LibroSueldoReport;
import com.orco.graneles.reports.RecibosSueldoSacYVac;
import com.orco.graneles.reports.RecibosSueldosAccidentados;
import com.orco.graneles.vo.DescompisicionMoneda;
import com.orco.graneles.vo.ProyeccionSacVacYAdelantosVO;
import java.io.IOException;

import java.io.Serializable;
import java.math.BigDecimal;
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
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

@ManagedBean(name = "periodoController")
@SessionScoped
public class PeriodoController implements Serializable {

    private Periodo current;
    private DataModel items = null;
    @EJB
    private PeriodoFacade ejbFacade;
    @EJB
    private LibroExcelFacade libroExcelF;
    
    private int selectedItemIndex;

    // Datos para el formulario de Periodo (Libro de Sueldos
    private int mes;
    private int anio;
    private int semestre;
    private UploadedFile fileAltas;
    private UploadedFile filePlanillas;
    private UploadedFile fileCargaReg;
    private UploadedFile filePagoFeri;
    private StreamedContent fileLibro;
    private StreamedContent fileExportAfip;
    private String urlArchivoPDF;
    private String urlArchivoTxt;
    private String urlArchivo34Txt;
    private String urlArchivoCierreMes;
    private String urlArchivoRecibosSacYVac;
    private String urlArchivoRecibosAccidentados;
    private String urlArchivoRecibosSavYVacOficiales;
    private String urlArchivoRecibosAccidentadosOficiales;
    
    private List<ProyeccionSacVacYAdelantosVO> proyeccionesSacYVacaciones;
    private BigDecimal totalBruto;
    private BigDecimal totalNeto;
    private BigDecimal totalAdelantos;
    private BigDecimal totalNetoConAdelantos;
    private String urlArchivoProyeccionSacYVac;
    private DescompisicionMoneda descomposicionMonedaTotalConAdelantos;
    
    public PeriodoController() {
        
    }

    public void init() {
        JsfUtil.minimoRolRequerido(Grupo.ROL_USUARIO);
        
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

    public void seleccionarPeriodoSemestral(){
        proyeccionesSacYVacaciones = ejbFacade.obtenerProyecciones(semestre, anio);
        totalBruto = BigDecimal.ZERO;
        totalNeto = BigDecimal.ZERO;
        totalAdelantos = BigDecimal.ZERO;
        totalNetoConAdelantos = BigDecimal.ZERO;
                
        for (ProyeccionSacVacYAdelantosVO p : proyeccionesSacYVacaciones){
            totalBruto = totalBruto.add(p.getProyeccionBruto());
            totalNeto = totalNeto.add(p.getProyeccionNeto());
            totalAdelantos = totalAdelantos.add(p.getTotalAdelantos());
            totalNetoConAdelantos = totalNetoConAdelantos.add(p.getProyeccionNetoConAdelantos());
        }
        
        //Completo el reporte xls
        ProyeccionSACyVacacionesXLS reporteXLS = new ProyeccionSACyVacacionesXLS(semestre, anio, proyeccionesSacYVacaciones);
        reporteXLS.addBean("totalBruto", totalBruto);
        reporteXLS.addBean("totalNeto", totalNeto);
        reporteXLS.addBean("totalAdelantos", totalAdelantos);
        reporteXLS.addBean("totalNetoConAdelantos", totalNetoConAdelantos);
        urlArchivoProyeccionSacYVac = reporteXLS.generarArchivo("ProyeccionSacYVac-" + String.valueOf(semestre) + "-" + String.valueOf(anio));
        
        //Genero la descomposicion de monedas
        descomposicionMonedaTotalConAdelantos = new DescompisicionMoneda(BigDecimal.ZERO);
        for (ProyeccionSacVacYAdelantosVO p : proyeccionesSacYVacaciones){
            DescompisicionMoneda dm = new DescompisicionMoneda(p.getProyeccionNetoConAdelantos());
            
            descomposicionMonedaTotalConAdelantos.agregarDescomposicion(dm);
        }
    }
    
    
    /**
     * Genera el libro de sueldos del perido actual
     */
    public void generarLibroPeriodo(){
        //Verifico que si el periodo ya tiene sueldos cargados, entonces genero el pdf y el archivo de AFIP
        if (current.getSueldoCollection() != null && current.getSueldoCollection().size() > 0){
            if (StringUtils.isBlank(current.getFolioLibro()) || current.getNroPrimeraHoja() == null){
                JsfUtil.addErrorMessage("Los campos " +  ResourceBundle.getBundle("/BundleSalario").getString("PeriodoTitle_folioLibro") 
                                       + " y " +  ResourceBundle.getBundle("/BundleSalario").getString("PeriodoTitle_nroPrimeraHoja") + " son obligatorios");    
            } else {
                ejbFacade.persist(current);
                
                LibroSueldoReport libroSueldo = new LibroSueldoReport(current);
                urlArchivoPDF = libroSueldo.obtenerReportePDF();
            }
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
            urlArchivoTxt = libroSueldoAFIP.generarArchivo(current.getDescripcion() + "v36");
            
            LibroSueldosAFIPv34 libroSueldoAFIP34 = new LibroSueldosAFIPv34(new ArrayList<Sueldo>(current.getSueldoCollection()));
            urlArchivo34Txt = libroSueldoAFIP34.generarArchivo(current.getDescripcion() + "v34");
                    
        } else {
            urlArchivoTxt = null;
            urlArchivo34Txt = null;
        }
    }
    
    
    /**
     * Genera el listado de cierre de mes para el periodo actual
     */
    public void generarListadoCierreMes(){
        //Verifico que si el periodo ya tiene sueldos cargados, entonces genero el pdf y el archivo de AFIP
        if (current.getSueldoCollection() != null && current.getSueldoCollection().size() > 0){
                
            CierreMesReport reporte = new CierreMesReport(current);
            urlArchivoCierreMes = reporte.obtenerReportePDF();
            
        } else {
            urlArchivoCierreMes = null;
        }
    }
    
    /**
     * Genera los recibos de sueldos de SAC y Vacaciones
     */
    public void generarRecibosSacYVac(){
        if (current != null){
            List<Sueldo> sueldosSacYVac = ejbFacade.obtenerSueldosSacYVac(current);
            
            RecibosSueldoSacYVac reporte = new RecibosSueldoSacYVac(current, sueldosSacYVac, false, ejbFacade.obtenerFechaInicioPeriodoSemestral(current.getDesde()));
            urlArchivoRecibosSacYVac = reporte.obtenerReportePDF();
        } else {
            urlArchivoRecibosSacYVac = null;
        }
    }
    
    public void generarRecibosSacYVacOficiales(){
        if (current != null){
            List<Sueldo> sueldosSacYVac = ejbFacade.obtenerSueldosSacYVac(current);
            
            RecibosSueldoSacYVac reporte = new RecibosSueldoSacYVac(current, sueldosSacYVac, true, ejbFacade.obtenerFechaInicioPeriodoSemestral(current.getDesde()));
            urlArchivoRecibosSavYVacOficiales = reporte.obtenerReportePDF();
        } else {
            urlArchivoRecibosSavYVacOficiales = null;
        }
    }
    
    public void generarRecibosAcc(){
        if (current != null){
            List<Sueldo> sueldosAcc = ejbFacade.obtenerSueldosAccidentados(current);
            
            RecibosSueldosAccidentados reporte = new RecibosSueldosAccidentados(current, sueldosAcc, false);
            urlArchivoRecibosAccidentados = reporte.obtenerReportePDF();
        } else {
            urlArchivoRecibosAccidentados = null;
        }
    }
    
    public void generarRecibosAccOficiales(){
        if (current != null){
            List<Sueldo> sueldosAcc = ejbFacade.obtenerSueldosAccidentados(current);
            
            RecibosSueldosAccidentados reporte = new RecibosSueldosAccidentados(current, sueldosAcc, true);
            urlArchivoRecibosAccidentadosOficiales = reporte.obtenerReportePDF();
        } else {
            urlArchivoRecibosAccidentadosOficiales = null;
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
        urlArchivo34Txt = null;
        urlArchivoCierreMes = null;
        urlArchivoRecibosSacYVac = null;
        urlArchivoRecibosAccidentados = null;
        urlArchivoRecibosSavYVacOficiales = null;
        urlArchivoRecibosAccidentadosOficiales = null;
        selectedItemIndex = 1;
    }
    
    public void procesarPeriodoConDatosSistema(){
        if (current != null && current.getDescripcion() != null && current.getDescripcion().length() > 0){
            try {
                getFacade().generarSueldosPeriodo(current);
                
                //Una vez subido todo, genero nuevamente el pdf
                urlArchivoPDF = null;
                urlArchivoTxt = null;
                urlArchivo34Txt = null;
                urlArchivoCierreMes = null;
                urlArchivoRecibosSacYVac = null;
                urlArchivoRecibosAccidentados = null;
                urlArchivoRecibosSavYVacOficiales = null;
                urlArchivoRecibosAccidentadosOficiales = null;

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
                    libroExcelF.completarPeríodo(fileAltas.getInputstream(),
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
                urlArchivo34Txt = null;

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

    public int getSemestre() {
        return semestre;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
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

    public String getUrlArchivo34Txt() {
        return urlArchivo34Txt;
    }

    public void setUrlArchivo34Txt(String urlArchivo34Txt) {
        this.urlArchivo34Txt = urlArchivo34Txt;
    }

    
    public String getUrlArchivoCierreMes() {
        return urlArchivoCierreMes;
    }

    public String getUrlArchivoRecibosSacYVac() {
        return urlArchivoRecibosSacYVac;
    }

    public void setUrlArchivoRecibosSacYVac(String urlArchivoRecibosSacYVac) {
        this.urlArchivoRecibosSacYVac = urlArchivoRecibosSacYVac;
    }

    public String getUrlArchivoRecibosAccidentados() {
        return urlArchivoRecibosAccidentados;
    }

    public void setUrlArchivoRecibosAccidentados(String urlArchivoRecibosAccidentados) {
        this.urlArchivoRecibosAccidentados = urlArchivoRecibosAccidentados;
    }

    public String getUrlArchivoRecibosAccidentadosOficiales() {
        return urlArchivoRecibosAccidentadosOficiales;
    }

    public void setUrlArchivoRecibosAccidentadosOficiales(String urlArchivoRecibosAccidentadosOficiales) {
        this.urlArchivoRecibosAccidentadosOficiales = urlArchivoRecibosAccidentadosOficiales;
    }

    public String getUrlArchivoRecibosSavYVacOficiales() {
        return urlArchivoRecibosSavYVacOficiales;
    }

    public void setUrlArchivoRecibosSavYVacOficiales(String urlArchivoRecibosSavYVacOficiales) {
        this.urlArchivoRecibosSavYVacOficiales = urlArchivoRecibosSavYVacOficiales;
    }
        
    public Periodo getCurrent() {
        return current;
    }

    public List<ProyeccionSacVacYAdelantosVO> getProyeccionesSacYVacaciones() {
        return proyeccionesSacYVacaciones;
    }

    public BigDecimal getTotalAdelantos() {
        return totalAdelantos;
    }

    public BigDecimal getTotalBruto() {
        return totalBruto;
    }

    public BigDecimal getTotalNeto() {
        return totalNeto;
    }

    public BigDecimal getTotalNetoConAdelantos() {
        return totalNetoConAdelantos;
    }

    public String getUrlArchivoProyeccionSacYVac() {
        return urlArchivoProyeccionSacYVac;
    }

    public DescompisicionMoneda getDescomposicionMonedaTotalConAdelantos() {
        return descomposicionMonedaTotalConAdelantos;
    }
    
    
}