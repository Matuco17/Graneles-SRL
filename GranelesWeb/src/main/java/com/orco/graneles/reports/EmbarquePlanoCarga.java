/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.carga.Embarque;
import com.orco.graneles.vo.PlanoEmbarqueVO;
import com.orco.graneles.vo.ResumenCargaEmbarqueVO;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.faces.context.FacesContext;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author orco
 */
public class EmbarquePlanoCarga extends ReporteGenerico {

    PlanoEmbarqueVO planoEmbarque;

    @Override
    protected String[] getUrlImagenes() {
        return new String[]{"logoGraneles.jpg"};
    }

    @Override
    protected Locale getReportLocale() {
        return new Locale("en");
    }
    
    public EmbarquePlanoCarga(Embarque embarque) {
        planoEmbarque = new PlanoEmbarqueVO(embarque);
    }
    
    @Override
    public String obtenerReportePDF() {
        List<PlanoEmbarqueVO> resumenes = new ArrayList<PlanoEmbarqueVO>();
        resumenes.add(planoEmbarque);
        
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(resumenes);
        
        params.put(JRParameter.REPORT_LOCALE, Locale.ENGLISH); 
        
        //Selecciono la imagen que debe cargarse de acuerdo a la cantidad de bodegas
        try {
            String imagenBuque = null;
            if (planoEmbarque.getCantidadBodegas() > 7){
                imagenBuque = "buque4.png";
            } else if (planoEmbarque.getCantidadBodegas() > 5){
                imagenBuque = "buque3.png";
            } else if (planoEmbarque.getCantidadBodegas() > 3){
                imagenBuque = "buque2.png";
            } else  {
                imagenBuque = "buque1.png";
            }
            
            FileInputStream imagen = new FileInputStream(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/images/" + imagenBuque));
            params.put("buque.png", imagen);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  catch (IOException e){
            e.printStackTrace();
        }
        
                
        return printGenerico(ds, "ResumenEmbarque", "ResumenEmbarque_"+ planoEmbarque.getEmbarqueCodigo());
    }
    
    
}
