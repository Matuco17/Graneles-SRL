/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.carga;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.domain.salario.SalarioBasico;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.salario.ConceptoReciboFacade;
import com.orco.graneles.vo.TrabajadorTurnoEmbarqueVO;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
/**
 *
 * @author orco
 */
@Stateless
public class TrabajadoresTurnoEmbarqueFacade extends AbstractFacade<TrabajadoresTurnoEmbarque> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    
    @EJB
    private ConceptoReciboFacade conceptoReciboF;

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
    
    public List<TrabajadoresTurnoEmbarque> getTrabajadoresPeriodo(Date desde, Date hasta){
      return getEntityManager().createNamedQuery("TrabajadoresTurnoEmbarque.findXPeriodo", TrabajadoresTurnoEmbarque.class)
                  .setParameter("desde", desde)
                  .setParameter("hasta", hasta)              
                  .getResultList();
    }   
    
    public List<TrabajadoresTurnoEmbarque> getTrabajadoresPeriodo(Personal personal, Date desde, Date hasta){
      return getEntityManager().createNamedQuery("TrabajadoresTurnoEmbarque.findXPeriodoYPersonal", TrabajadoresTurnoEmbarque.class)
                  .setParameter("personal", personal)
                  .setParameter("desde", desde)
                  .setParameter("hasta", hasta)
                  .getResultList();
    }    
    
    
    
    /**
     * Metodo que devuelve el último trabajo realizado por el personal correpondiente
     * @param personal
     * @param fecha fecha límite para tener en cuenta el trabajo realizado
     * @return 
     */
    public TrabajadoresTurnoEmbarque getUltimoTrabajoRealizado(Personal personal, Date fecha){
        List<TrabajadoresTurnoEmbarque> listaTrabajos = getEntityManager().createNamedQuery("TrabajadoresTurnoEmbarque.findXPersonalFechaDesc", TrabajadoresTurnoEmbarque.class)
                                                    .setParameter("personal", personal)
                                                    .setParameter("fecha", fecha)
                                                    .getResultList();
        
        if (listaTrabajos != null && listaTrabajos.size() > 0){
            return listaTrabajos.get(0);
        } else {
            return null;
        }
        
    }
    
    /**
     * Busca los tte de acuerdo a los parametros seleccionados, los primeros 2 son opcionales
     * @param categora
     * @param personal
     * @param desde
     * @param hasta
     * @return 
     */
    public List<TrabajadoresTurnoEmbarque> getTrabajadores(Categoria categoria, Personal personal, Date desde, Date hasta){
        return getEntityManager().createNamedQuery("TrabajadoresTurnoEmbarque.findXFechasCatPers", TrabajadoresTurnoEmbarque.class)
                .setParameter("personal", personal)
                .setParameter("categoria", categoria)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList();
    }
    
    /**
     * Metodo que recalcula todos los sueldos en el sistema
     */
    public void recalcularSueldos(){
        List<TrabajadoresTurnoEmbarque> ttes = findAll();
        
        for (TrabajadoresTurnoEmbarque tte : ttes){
            TrabajadorTurnoEmbarqueVO tteVO = conceptoReciboF.calcularDiaTTE(tte, true);
            
            tte.setBruto(tteVO.getValorBruto());
            tte.setNeto(tteVO.getValorTurno());
            
            edit(tte);
        }
    }
    
    /**
     * Metodo que recalcula los sueldos de acuerdo al salario basico
     * @param sb 
     */
    public void recalcularSueldos(SalarioBasico sb){
        List<TrabajadoresTurnoEmbarque> ttes = 
                getEntityManager().createNamedQuery("TrabajadoresTurnoEmbarque.findXSalarioBasico", TrabajadoresTurnoEmbarque.class)
                    .setParameter("desde", sb.getDesde())
                    .setParameter("hasta", sb.getHasta())
                    .setParameter("tarea", sb.getTarea())
                    .setParameter("categoria", sb.getCategoria())
                    .getResultList();
                
        
        for (TrabajadoresTurnoEmbarque tte : ttes){
            TrabajadorTurnoEmbarqueVO tteVO = conceptoReciboF.calcularDiaTTE(tte, true);
            
            tte.setBruto(tteVO.getValorBruto());
            tte.setNeto(tteVO.getValorTurno());
            
            edit(tte);
        }
    }
    
}
