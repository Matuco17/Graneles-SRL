/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.personal;

import com.orco.graneles.domain.personal.Accidentado;
import com.orco.graneles.domain.salario.Periodo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author orco
 */
@Stateless
public class AccidentadoFacade extends AbstractFacade<Accidentado> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public AccidentadoFacade() {
        super(Accidentado.class);
    }
    
    /**
     * Metodo que devuelve todos los accidentados del periodo
     * @param periodo
     * @return 
     */
    public List<Accidentado> getAccidentadosPeriodo(Periodo periodo){
        List<Accidentado> accidentados = new ArrayList<Accidentado>();
        
        //Tengo que hacer todas las busquedas ya que es complicado, asi que parto de los accidentados que no tienen libro
        for(Accidentado acc : getEntityManager().createNamedQuery("Accidentado.findSinLibroSueldo", Accidentado.class).getResultList()){
            //Tengo que ver que la fecha desde sea menor a la fecha final del periodo
            if (acc.getDesde().before(periodo.getHasta())){
                //Veo si no tiene hasta o si tiene hasta y es mayor a la fecha desde del periodo
                if (acc.getHasta() == null || acc.getHasta().after(periodo.getDesde())){
                    accidentados.add(acc);
                }
            }
        }
        
        return accidentados;
    }
    
    
}
