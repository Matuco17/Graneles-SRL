/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.personal;

import com.orco.graneles.domain.miscelaneos.TipoRecibo;
import com.orco.graneles.domain.personal.JornalCaido;
import com.orco.graneles.domain.salario.ConceptoRecibo;
import com.orco.graneles.domain.salario.Sueldo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import com.orco.graneles.model.salario.ConceptoReciboFacade;
import com.orco.graneles.model.salario.SueldoFacade;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

/*
 * @author orco
 */
@Stateless
public class JornalCaidoFacade extends AbstractFacade<JornalCaido> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    
    @EJB
    ConceptoReciboFacade conceptoReciboF;
    
    @EJB
    FixedListFacade fixedListF;
    
    @EJB
    SueldoFacade sueldoF;
    
    protected EntityManager getEntityManager() {
        return em;
    }

    public JornalCaidoFacade() {
        super(JornalCaido.class);
    }

    public void completarValor(JornalCaido jc){
        Map<Integer, List<ConceptoRecibo>> conceptosHoras = conceptoReciboF.obtenerConceptosXTipoRecibo(fixedListF.find(TipoRecibo.HORAS));
        
        jc.setValor(sueldoF.calcularSueldoAccidentado(jc.getDesde(), jc.getHasta(), jc.getAccidentado(), conceptosHoras));
    }    
    
}
