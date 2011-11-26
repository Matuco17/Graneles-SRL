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
            /*
            //Agrego la imagen de Logo Reducido por si el reporte lo necesita
            try {
                    params.put("logoreducido_img", new FileInputStream(request.getSession().getServletContext().getRealPath("/images/" + "logoReducido.jpg")));
            } catch (FileNotFoundException e) {
                    e.printStackTrace();
            }
            //Agrego la imagen de Membrete por si el reporte lo necesita
            try {
                    params.put("membrete_img", new FileInputStream(request.getSession().getServletContext().getRealPath("/images/" + "membrete.jpg")));
            } catch (FileNotFoundException e) {
                    e.printStackTrace();
            }
             */
            
            String pathBaseReportes = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reports/") + "/";

            String pathTemplate = pathBaseReportes + archivosJasper + ".jasper";
            
            JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(pathTemplate); 
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, ds);
                    
            
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
