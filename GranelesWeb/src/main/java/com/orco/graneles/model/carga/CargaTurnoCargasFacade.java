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
public class CargaTurnoCargasFacade extends AbstractFacade<CargaTurnoCargas> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public CargaTurnoCargasFacade() {
        super(CargaTurnoCargas.class);
    }
    
    /**
     * Metodo que devuelve todas las cargas realizadas por el cargador en el embarque en cuestion
     * @param cargador
     * @param embarque
     * @return 
     */
    public List<CargaTurnoCargas> obtenerCargas(Empresa cargador, Embarque embarque){
        return getEntityManager().createNamedQuery("CargaTurnoCargas.findByEmbarqueYExportador", CargaTurnoCargas.class)
                .setParameter("cargador", cargador)
                .setParameter("embarque", embarque)
                .getResultList();
    }
    
}
