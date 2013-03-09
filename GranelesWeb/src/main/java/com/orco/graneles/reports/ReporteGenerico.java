/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.reports;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * Clase Base para todos los reportes
 * @author orco
 */
public abstract class ReporteGenerico {
    
    public abstract String obtenerReportePDF();
    
    protected String[] getUrlImagenes(){
        return null;
    };
    
    protected Map<String, Object> params = new HashMap<String, Object>();
    
    /**
     * Metodo generico para la realizacion del reporte
     * @param ds Data source con la lista de los objetos del reporte
     * @param archivosJasper nombre del archivo .jasper sin extension
     * @param nombreArchivoPDF nombre del archivo .pdf sin extension
     * @return el stream de lectura del archivo generado
     */
    protected String printGenerico(JRBeanCollectionDataSource ds, String archivosJasper, String nombreArchivoPDF) {
        try
        {
            
            //Cargo las imagenes del reporte
            if (getUrlImagenes() != null){
                for (int i = 0; i < getUrlImagenes().length; i++){
                    try {
                        FileInputStream imagen = new FileInputStream(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/images/" + getUrlImagenes()[i]));
                        params.put(getUrlImagenes()[i], imagen);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }  catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
                        
            
            String pathBaseReportes = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reports/") + "/";
         
            String pathTemplate = pathBaseReportes + archivosJasper + ".jasper";
            
            params.put("SUBREPORT_DIR", pathBaseReportes);
            params.put("REPORT_LOCALE", new java.util.Locale("es_AR"));
    
            JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(pathTemplate); 
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, ds);
                    
            
            String pathPdfGenerado = pathBaseReportes + nombreArchivoPDF + ".pdf";
            String urlArchivoGenerado = "~/../../../resources/reports/" + nombreArchivoPDF + ".pdf";
            //Genero el pdf para bajar
            JasperExportManager.exportReportToPdfFile(jasperPrint, pathPdfGenerado);
            
            return urlArchivoGenerado;
    /*    
      } catch (FileNotFoundException ex) {
        ex.printStackTrace();
            //Logger.getLogger(ReporteGenerico.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
        Logger.getLogger(ReporteGenerico.class.getName()).log(Level.SEVERE, null, ex);
     
     */
      } catch (JRException e) {
        e.printStackTrace();
      }
      return null;
    }
    
}
