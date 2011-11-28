/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.carga;

import com.orco.graneles.domain.carga.*;
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
    
    private List<CargaTurno> cargarNuevaPorBuque(TurnoEmbarque tembarque){
        List<CargaTurno> cargas = new ArrayList<CargaTurno>();
        
        for (CargaPrevia cargaOriginal : tembarque.getEmbarque().getCargaPreviaCollection()){
            CargaTurno carga = new CargaTurno();
            carga.setCargaOriginalBodega(cargaOriginal);
            carga.setTurnoEmbarque(tembarque);
            carga.setCarga(BigDecimal.ZERO);
            cargas.add(carga);
        }
        Collections.sort(cargas);                
        return cargas;
    }
    
    
    public List<CargaTurno> obtenerCargas(TurnoEmbarque tembarque){
         List<CargaTurno> cargas = null;
        if (tembarque.getId() != null && tembarque.getCargaTurnoCollection() != null && tembarque.getCargaTurnoCollection().size() > 0){
            cargas = new ArrayList<CargaTurno>(tembarque.getCargaTurnoCollection());
        } else {
            cargas = cargarNuevaPorBuque(tembarque);
        }
        Collections.sort(cargas);
        return cargas;
    }
    
}
