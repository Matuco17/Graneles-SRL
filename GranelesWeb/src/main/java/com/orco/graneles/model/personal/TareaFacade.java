/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.personal;

import com.orco.graneles.domain.personal.Tarea;
import com.orco.graneles.domain.salario.SalarioBasico;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.salario.SalarioBasicoFacade;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
/**
 *
 * @author orco
 */
@Stateless
public class TareaFacade extends AbstractFacade<Tarea> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @EJB
    SalarioBasicoFacade salarioBasicoF;
    
    protected EntityManager getEntityManager() {
        return em;
    }

    public TareaFacade() {
        super(Tarea.class);
    }
    
    /**
     * Metodo que devuelve las teas agrupadas por categoria, de acuerdo a la relación de Salario Básico Activo
     * @return 
     */
    public Map<Integer, List<Tarea>> obtenerTareasXCategoria(Date fecha){
        List<SalarioBasico> salarios = salarioBasicoF.obtenerSalariosActivos(fecha);
        Map<Integer, List<Tarea>> result = new HashMap<Integer, List<Tarea>>();
        
        for (SalarioBasico sb : salarios){
            if (result.get(sb.getCategoria().getId()) == null){
                result.put(sb.getCategoria().getId(), new ArrayList<Tarea>());
            }
            result.get(sb.getCategoria().getId()).add(sb.getTarea());
        }        
        
        return result;
    }
    
}
