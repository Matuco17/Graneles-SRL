/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.carga;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.Periodo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import java.util.Date;
import java.util.List;
/**
 *
 * @author orco
 */
@Stateless
public class TrabajadoresTurnoEmbarqueFacade extends AbstractFacade<TrabajadoresTurnoEmbarque> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TrabajadoresTurnoEmbarqueFacade() {
        super(TrabajadoresTurnoEmbarque.class);
    }
    
    public List<TrabajadoresTurnoEmbarque> getTrabajadoresPeriodo(Periodo periodo){
      return getEntityManager().createNamedQuery("TrabajadoresTurnoEmbarque.findXPeriodo", TrabajadoresTurnoEmbarque.class)
                  .setParameter("desde", periodo.getDesde())
                  .setParameter("hasta", periodo.getHasta())
              
                  .getResultList();
    }    
    
    public List<TrabajadoresTurnoEmbarque> getTTEPeriodo(Personal personal, Date desde, Date hasta){
      return getEntityManager().createNamedQuery("TrabajadoresTurnoEmbarque.findXPeriodoYPersonal", TrabajadoresTurnoEmbarque.class)
                  .setParameter("personal", personal)
                  .setParameter("desde", desde)
                  .setParameter("hasta", hasta)
                  .getResultList();
    }    
    
    
    
    /**
     * Metodo que devuelve el último trabajo realizado por el personal correpondiente
     * @param personal
     * @return 
     */
    public TrabajadoresTurnoEmbarque getUltimoTrabajoRealizado(Personal personal){
        List<TrabajadoresTurnoEmbarque> listaTrabajos = getEntityManager().createNamedQuery("TrabajadoresTurnoEmbarque.findXPersonalFechaDesc", TrabajadoresTurnoEmbarque.class)
                                                    .setParameter("personal", personal)
                                                    .getResultList();
        
        if (listaTrabajos != null && listaTrabajos.size() > 0){
            return listaTrabajos.get(0);
        } else {
            return null;
        }
        
    }
    
    
}
