/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.jsf.miscelaneos;

import com.orco.graneles.domain.carga.TurnoEmbarque;
import com.orco.graneles.model.carga.TrabajadoresTurnoEmbarqueFacade;
import com.orco.graneles.model.carga.TurnoEmbarqueFacade;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author orco
 */
@ManagedBean(name = "procesosAdminController")
@SessionScoped
public class ProcesosAdminController implements Serializable {
    
    @EJB
    private TurnoEmbarqueFacade turnoEmbarqueF;
    @EJB 
    private TrabajadoresTurnoEmbarqueFacade trabajadoresTurnoEmbarqueF;
    
    public void actualizarValoresTurnoEmbarque(){
        
        for (TurnoEmbarque te : turnoEmbarqueF.findAll()){
            turnoEmbarqueF.persist(te);
        }
        
    }
    
    public void actualizarValoresTrabajadoresTurno(){
        trabajadoresTurnoEmbarqueF.recalcularSueldos();
    }
}
