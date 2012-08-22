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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author orco
 */
public abstract class ExportadorGenerico <T>{
    
    protected List<T> datosAExportar;
    
    public ExportadorGenerico(List<T> datos){
        datosAExportar = datos;
    }
    
    /**
     * Metodo que se utiliza para convertir el objecto en una linea de string para que se pueda agregar al archivo
     * @param elemento
     * @return 
     */
    protected abstract String convertirElementoEnLinea(T elemento);
    
    public String generarArchivo(String nombreArchivo){
        
        String pathBaseArchivos = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/plaintext/") + "/";
        String pathArchivo = pathBaseArchivos + nombreArchivo + ".txt";
        String urlArchivo = "~/../../../resources/plaintext/" + nombreArchivo + ".txt";
    
        
        try {
            FileWriter fw = new FileWriter(pathArchivo);
            
            for (T elemento: datosAExportar){
                fw.write(convertirElementoEnLinea(elemento) + "\r\n");
            }
            
            fw.close();
        
            return urlArchivo;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExportadorGenerico.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExportadorGenerico.class.getName()).log(Level.SEVERE, null, ex);
        }    
        
        return null;
    }
    
    private DecimalFormat decFormat15 = new DecimalFormat("#0.00");
    
    protected String formatearImporte15(BigDecimal importe){
        return StringUtils.leftPad(decFormat15.format(
                    importe.doubleValue())
                        
                ,15 , " ");
    }
    
}
