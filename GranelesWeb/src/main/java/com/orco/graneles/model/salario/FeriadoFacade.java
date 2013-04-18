/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.Feriado;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.carga.TrabajadoresTurnoEmbarqueFacade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import org.joda.time.DateTime;
/**
 *
 * @author orco
 */
@Stateless
public class FeriadoFacade extends AbstractFacade<Feriado> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @EJB
    private TrabajadoresTurnoEmbarqueFacade tteF;
    
    
    protected EntityManager getEntityManager() {
        return em;
    }

    public FeriadoFacade() {
        super(Feriado.class);
    }
    
    /**
     * Metodo que calcula los trabajadores que corresponden cobrar el feriado
     * @param fechaFeriado
     * @return 
     */
    public List<Personal> obtenerTrabajadoresIncluidos(Date fechaFeriado){
        Map<Personal, Integer> trabajadoresDiaAnterior = new HashMap<Personal, Integer>();
        Map<Personal, Integer> trabajadoresXDiasPosteriores = new HashMap<Personal, Integer>();
        Map<Personal, Integer> trabajadoresXDiasAnteriores = new HashMap<Personal, Integer>();
        Set<Personal> trabajadoresIncluidos = new HashSet<Personal>();
        
        Date diaAnterior = (new DateTime(fechaFeriado)).minusDays(1).toDate();
        Date diaInicioAnteriores = (new DateTime(fechaFeriado)).minusDays(10).toDate();
        Date diaFinPosteriores = (new DateTime(fechaFeriado)).plusDays(5).toDate();
        
        for (TrabajadoresTurnoEmbarque tte : tteF.getTrabajadoresPeriodo(diaInicioAnteriores, diaFinPosteriores)){
            if (tte.getPlanilla().getFecha().equals(diaAnterior)){
                agregarTTE(trabajadoresDiaAnterior, tte);
            }
            if (tte.getPlanilla().getFecha().before(fechaFeriado)){
                agregarTTE(trabajadoresXDiasAnteriores, tte);
            }
            if (tte.getPlanilla().getFecha().after(fechaFeriado)){
                agregarTTE(trabajadoresXDiasPosteriores, tte);
            }
        }
        
        //Evaluación de casos
        //Caso 1: Si un estibador trabaja en cualquiera de los 1 turno habil el día habil anterior al día del feriado y mete 1 jornal habil en los 5 días habiles posteriores al feriado
        for (Personal p : trabajadoresDiaAnterior.keySet()){
            if (trabajadoresXDiasPosteriores.get(p) >= 6){
                trabajadoresIncluidos.add(p);
            }
        }
        
        //Caso 2: si el estibador en los 10 días hábiles previos al feriado tiene trabajadas 36 hs hábiles,
        for (Map.Entry<Personal, Integer> horasPersonal : trabajadoresXDiasPosteriores.entrySet()){
            if (horasPersonal.getValue() >= 36){
                trabajadoresIncluidos.add(horasPersonal.getKey());
            }
        }
        
        List<Personal> trabajadoresIncluidosList = new ArrayList<Personal>(trabajadoresIncluidos);
        Collections.sort(trabajadoresIncluidosList);
        return trabajadoresIncluidosList;
    }
    
    private void agregarTTE(Map<Personal, Integer> horasTrabajador, TrabajadoresTurnoEmbarque tte){
        if (horasTrabajador.containsKey(tte.getPersonal())){
            horasTrabajador.put(tte.getPersonal(), horasTrabajador.get(tte.getPersonal()) + tte.getHoras());
        } else {
            horasTrabajador.put(tte.getPersonal(), tte.getHoras());
        }
    }
    
    
    
}
