/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.carga;

import com.orco.graneles.domain.carga.*;
import com.orco.graneles.domain.facturacion.Empresa;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 *
 * @author orco
 */
@Stateless
public class CargaTurnoFacade extends AbstractFacade<CargaTurno> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public CargaTurnoFacade() {
        super(CargaTurno.class);
    }
    
    /**
     * Metodo que crea una carga nueva para un buque seleccioando de acuerdo al coordinador
     * @param tembarque
     * @param coordinador
     * @return 
     */
    public CargaTurno cargarNuevaPorBuque(TurnoEmbarque tembarque){
        
        CargaTurno cargaTurno = new CargaTurno();
        cargaTurno.setTurnoEmbarque(tembarque);
        cargaTurno.setCargador(tembarque.getEmbarque().getCoordinador());
        cargaTurno.setCargasCollection(new ArrayList<CargaTurnoCargas>());
        
        for (CargaPrevia cargaOriginal : tembarque.getEmbarque().getCargaPreviaCollection()){
            CargaTurnoCargas cargaTC = new CargaTurnoCargas();
            cargaTC.setCargaOriginalBodega(cargaOriginal);
            cargaTC.setCarga(BigDecimal.ZERO);
            cargaTC.setCargaTurno(cargaTurno);
            cargaTurno.getCargasCollection().add(cargaTC);
        }                
        
        return cargaTurno;
    }
    
        
    public List<CargaTurno> obtenerCargas(TurnoEmbarque tembarque){
         List<CargaTurno> cargas = null;
        if (tembarque.getId() != null && tembarque.getCargaTurnoCollection() != null && tembarque.getCargaTurnoCollection().size() > 0){
            cargas = new ArrayList<CargaTurno>(tembarque.getCargaTurnoCollection());
        } else {
            cargas = new ArrayList<CargaTurno>();
            cargas.add(cargarNuevaPorBuque(tembarque));
        }
        Collections.sort(cargas);
        return cargas;
    }
    
}
