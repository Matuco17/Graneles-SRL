/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.jsf;

import com.orco.graneles.domain.carga.Embarque;
import com.orco.graneles.domain.carga.TurnoEmbarque;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.carga.EmbarqueFacade;
import com.orco.graneles.model.carga.TurnoEmbarqueFacade;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 *
 * @author orco
 */
@ManagedBean(name = "indexController")
@RequestScoped
public class IndexController {
    
    @EJB
    EmbarqueFacade embarqueF;
    @EJB
    TurnoEmbarqueFacade turnoEmbarqueF;
    
    private DataModel etaVencidos;
    private DataModel etaNoVencidos;
    private DataModel ultimasPlanillas;
    private DataModel embarquesNoFacturados;
    
    /** Creates a new instance of MenuController */
    public IndexController() {
    }

    public void init() {
        //calculos de los embarques
        List<Embarque> vencidos = new ArrayList<Embarque>();
        List<Embarque> noVencidos = new ArrayList<Embarque>();
        
        Date hoy = new Date();        
        for (Embarque e : embarqueF.findByConsolidado(Boolean.FALSE)){
            if (e.getEta().compareTo(hoy) > 0){
                noVencidos.add(e);
            } else {
                vencidos.add(e);
            }
        }
        Collections.sort(vencidos);
        Collections.sort(noVencidos);
        
        etaVencidos = new ListDataModel(vencidos);
        etaNoVencidos = new ListDataModel(noVencidos);
        
        
        //Calculos de los ultimas planillas
        ultimasPlanillas = new ListDataModel(turnoEmbarqueF.getRecientes(5));
        
        embarquesNoFacturados = new ListDataModel(embarqueF.findByFacturado(false));
    }
    
    public DataModel getEtaVencidos() {
        return etaVencidos;
    }

    public void setEtaVencidos(DataModel etaVencidos) {
        this.etaVencidos = etaVencidos;
    }

    public DataModel getEtaNoVencidos() {
        return etaNoVencidos;
    }

    public void setEtaNoVencidos(DataModel etaNoVencidos) {
        this.etaNoVencidos = etaNoVencidos;
    }

    public DataModel getUltimasPlanillas() {
        return ultimasPlanillas;
    }

    public void setUltimasPlanillas(DataModel ultimasPlanillas) {
        this.ultimasPlanillas = ultimasPlanillas;
    }

    public DataModel getEmbarquesNoFacturados() {
        return embarquesNoFacturados;
    }

    public void setEmbarquesNoFacturados(DataModel embarquesNoFacturados) {
        this.embarquesNoFacturados = embarquesNoFacturados;
    }

    
    
    
}
