/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.personal;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.personal.Accidentado;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.domain.salario.SalarioBasico;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.Moneda;
import com.orco.graneles.model.carga.TrabajadoresTurnoEmbarqueFacade;
import com.orco.graneles.model.salario.ConceptoReciboFacade;
import com.orco.graneles.model.salario.SalarioBasicoFacade;
import com.orco.graneles.vo.AccidentadoVO;
import com.orco.graneles.vo.SueldoAccidentadoVO;
import com.orco.graneles.vo.TrabajadorTurnoEmbarqueVO;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
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
    @EJB
    private SalarioBasicoFacade salarioBasicoF;

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
    public List<Accidentado> getAccidentadosPeriodo(Date desde, Date hasta){
        return getEntityManager().createNamedQuery("Accidentado.findByPeriodo", Accidentado.class)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList();
    }

    /**
     * Metodo que devuelve todos los accidentados del periodo del personal en cuestion
     * @param periodo
     * @return 
     */
    public List<Accidentado> getAccidentadosPeriodoYPersonal(Date desde, Date hasta, Personal personal){
        return getEntityManager().createNamedQuery("Accidentado.findByPeriodoYPersonal", Accidentado.class)
                .setParameter("personal", personal)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList();
    }
    
    /**
     * Metodo que calcula los valores del accidentado de acuerdo al personal accidentado
     * @param personal
     * @return 
     */
    public AccidentadoVO completarAccidentado(Accidentado accidentado){
        
        if (accidentado.getTrabajoRealizado() == null){
            accidentado.setTrabajoRealizado(trabTurnoF.getUltimoTrabajoRealizado(accidentado.getPersonal(), accidentado.getDesde()));
           
             //Completo de acuerdo al ultimo turno los elementos del accidentado automaticamente
            if (accidentado.getTrabajoRealizado() != null){
                accidentado.setCategoria(accidentado.getTrabajoRealizado().getCategoria());
                accidentado.setTarea(accidentado.getTrabajoRealizado().getTarea());
            }
        }
        
        AccidentadoVO accVO = new AccidentadoVO(accidentado);
        
        if (accidentado.getTrabajoRealizado() != null){
            
            //Ahora busco todos los salarios que van a existir
            accVO.setSueldos(new ArrayList<SueldoAccidentadoVO>());
            
            for (SalarioBasico sb : salarioBasicoF.obtenerSalarios(accVO.getAccidentado().getTarea(), accVO.getAccidentado().getCategoria(), accidentado.getDesde(), accidentado.getHasta())){
                SueldoAccidentadoVO saVO = new SueldoAccidentadoVO(); 
                
                saVO.setBrutoSinAdicionales(conceptoReciboF.calcularDiaCompletoTTTE(sb, accidentado.getTrabajoRealizado(), false).getValorBruto());
                saVO.setBrutoConAdicionales(conceptoReciboF.calcularDiaCompletoTTTE(sb, accidentado.getTrabajoRealizado(), true).getValorBruto());
                
                if (sb.getDesde().before(accidentado.getDesde())){
                    saVO.setDesde(accidentado.getDesde());
                } else {
                    saVO.setDesde(sb.getDesde());
                }
                
                if (accidentado.getHasta() != null){
                    if (sb.getHasta() != null){
                        if (sb.getHasta().before(accidentado.getHasta())){
                            saVO.setHasta(sb.getHasta());
                        } else {
                            saVO.setHasta(accidentado.getHasta());
                        }
                    } else {
                        saVO.setHasta(accidentado.getHasta());
                    }
                } else {
                    saVO.setHasta(sb.getHasta());
                }
                
                accVO.getSueldos().add(saVO);
            }
        }
        return accVO;
    }
    
    
    
    
}
