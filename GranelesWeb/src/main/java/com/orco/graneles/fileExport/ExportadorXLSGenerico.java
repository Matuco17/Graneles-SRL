/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.fileExport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author orco
 */
public abstract class ExportadorXLSGenerico <T>{
    
protected List<T> datosAExportar;
    private Map<String, Object> beans;
    
    public ExportadorXLSGenerico(List<T> datos){
        datosAExportar = datos;
        beans = new HashMap<String, Object>();
    }
    
    public void addBean(String key, Object value){
        beans.put(key, value);
    }
    
    
    /**
     * Metodo que se utiliza para convertir el objecto en una linea de string para que se pueda agregar al archivo
     * @param elemento
     * @return 
     */
    protected abstract String getTemplate();
    
    public String generarArchivo(String nombreArchivo){
        
        String pathBaseArchivos = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/xlss/") + "/";
        String pathTemplate = pathBaseArchivos + getTemplate() + ".xls";
	String pathArchivo = pathBaseArchivos + nombreArchivo + ".xls";
        String urlArchivo = "~/../../../resources/xlss/" + nombreArchivo + ".xls";
    
        
        try {
            //Genero el archivo de excel
            XLSTransformer transformer = new XLSTransformer();
            transformer.transformXLS(pathTemplate, beans, pathArchivo);
	    
            return urlArchivo;
        } catch (ParsePropertyException ex) {
            Logger.getLogger(ExportadorXLSGenerico.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidFormatException ex) {
            Logger.getLogger(ExportadorXLSGenerico.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExportadorXLSGenerico.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExportadorXLSGenerico.class.getName()).log(Level.SEVERE, null, ex);
        }    
        return null;
    }
}
