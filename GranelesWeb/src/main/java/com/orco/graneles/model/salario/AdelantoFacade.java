/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.Adelanto;
import com.orco.graneles.domain.salario.Sueldo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.Moneda;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
/**
 *
 * @author orco
 */
@Stateless
public class AdelantoFacade extends AbstractFacade<Adelanto> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    
    @EJB
    private PeriodoFacade periodoF;
    @EJB
    private ConceptoReciboFacade conceptoReciboF;

    protected EntityManager getEntityManager() {
        return em;
    }

    public AdelantoFacade() {
        super(Adelanto.class);
    }
    
    /**
     * Metodo que devuelve el total del SAC y las Vacaciones acumuladas para poder obtener un adelanto
     * sin tener en cuenta el valor de los adelantos obtenidos dentro del periodo.
     * @param personal
     * @return 
     */
    public BigDecimal calcularTotalAdelantoAcumulado(Personal personal){
        Date fechaInicio = periodoF.obtenerFechaInicioPeriodoSemestralActual();
        Date fechaFin = new Date();
        
        double acumulado = 0;
        acumulado += conceptoReciboF.calcularValorSAC(personal, fechaInicio, fechaFin, null, true, false);
        acumulado += conceptoReciboF.calcularValorVacaciones(personal, fechaInicio, fechaFin, null, true, false);
        
        double acumuladoNeto = conceptoReciboF.calcularNeto(personal, acumulado);
        
        return new Moneda(acumuladoNeto);
    }
    
    /**
     * Metodo que busca todos los adelantos del personal del periodo actual
     * @param personal
     * @return 
     */
    public List<Adelanto> obtenerAdelantosPeriodo(Personal personal){
        return getEntityManager().createNamedQuery("Adelanto.findByFechaPersonalDesdeHasta", Adelanto.class)
                .setParameter("personal", personal)
                .setParameter("desde", periodoF.obtenerFechaInicioPeriodoSemestralActual())
                .setParameter("hasta", new Date())
                .getResultList();
    }
    
     /**
     * Metodo que busca todos los adelantos sobre las fechas
     * @return 
     */
    public List<Adelanto> obtenerAdelantos(Date desde, Date hasta){
        return getEntityManager().createNamedQuery("Adelanto.findByFechaDesdeHasta", Adelanto.class)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList();
    }
    
    
    /**
     * Busca todos los adelantos correspondientes al sueldo en cuestion.
     * @param s
     * @return 
     */
    public List<Adelanto> obtenerAdelantosSueldo(Sueldo s){
        return getEntityManager().createNamedQuery("Adelanto.findByFechaPersonalDesdeHasta", Adelanto.class)
                .setParameter("personal", s.getPersonal())
                .setParameter("desde", periodoF.obtenerFechaInicioPeriodoSemestral(s.getPeriodo().getDesde()))
                .setParameter("hasta", periodoF.obtenerFechaFinPeriodoSemestral(s.getPeriodo().getHasta()))
                .getResultList();
    }
    
}
