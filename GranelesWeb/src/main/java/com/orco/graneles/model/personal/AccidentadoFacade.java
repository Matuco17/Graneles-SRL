/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.personal;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.personal.Accidentado;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.Periodo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.carga.TrabajadoresTurnoEmbarqueFacade;
import com.orco.graneles.model.salario.ConceptoReciboFacade;
import com.orco.graneles.vo.NuevoAccidentadoVO;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
/**
 *
 * @author orco
 */
@Stateless
public class AccidentadoFacade extends AbstractFacade<Accidentado> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    
    @EJB
    private TrabajadoresTurnoEmbarqueFacade trabTurnoF;
    @EJB
    private ConceptoReciboFacade conceptoReciboF;

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
    
    /**
     * Metodo que calcula los valores del accidentado de acuerdo al personal accidentado
     * @param personal
     * @return 
     */
    public NuevoAccidentadoVO calcularNuevoAccidentado(Accidentado accidentado){
        TrabajadoresTurnoEmbarque ultimoTTE = trabTurnoF.getUltimoTrabajoRealizado(accidentado.getPersonal());
        
        NuevoAccidentadoVO accVO = new NuevoAccidentadoVO(ultimoTTE, accidentado);
        
        if (accVO.getUltimoTurnoTrabajado() != null){
            //Completo de acuerdo al ultimo turno los elementos del accidentado automaticamente
            accVO.getAccidentado().setCategoria(accVO.getUltimoTurnoTrabajado().getCategoria());
            accVO.getAccidentado().setTarea(accVO.getUltimoTurnoTrabajado().getTarea());
            
            accVO.setSueldoDiaConAdicionales(new BigDecimal(conceptoReciboF.calcularDiaTrabajadoTTE(ultimoTTE, true)));
            accVO.setSueldoDiaSinAdicionales(new BigDecimal(conceptoReciboF.calcularDiaTrabajadoTTE(ultimoTTE, false)));
        }
        return accVO;
    }
    
    
    
    
}
