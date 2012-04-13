/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.carga;

import com.orco.graneles.domain.carga.Bodega;
import com.orco.graneles.domain.carga.Buque;
import com.orco.graneles.domain.carga.CargaPrevia;
import com.orco.graneles.domain.carga.Embarque;
import com.orco.graneles.domain.carga.Mercaderia;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 *
 * @author orco
 */
@Stateless
public class CargaPreviaFacade extends AbstractFacade<CargaPrevia> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public CargaPreviaFacade() {
        super(CargaPrevia.class);
    }
    
    private List<CargaPrevia> cargarNuevaPorBuque(Buque buque, Mercaderia mercaderia, Embarque embarque){
        List<CargaPrevia> cargas = new ArrayList<CargaPrevia>();
        
        for (Bodega bodega : buque.getBodegaCollection()){
            CargaPrevia carga = new CargaPrevia();
            carga.setBodega(bodega);
            carga.setMercaderia(mercaderia);
            carga.setEmbarque(embarque);
            cargas.add(carga);
        }
        Collections.sort(cargas);                
        return cargas;
    }
    
    
    public List<CargaPrevia> obtenerCargasPrevias(Buque buque, Mercaderia mercaderia, Embarque embarque){
         List<CargaPrevia> cargas = null;
        if (embarque.getId() != null && embarque.getCargaPreviaCollection() != null && embarque.getCargaPreviaCollection().size() > 0){
            //Asigno por defecto la mercaderia a las cargas previas que no tengan mercaderia asignada
            for (CargaPrevia cp : embarque.getCargaPreviaCollection()){
                if (cp.getMercaderia() == null){
                    cp.setMercaderia(mercaderia);    
                }                
            }
            cargas = new ArrayList<CargaPrevia>(embarque.getCargaPreviaCollection());
        } else {
            cargas = cargarNuevaPorBuque(buque, mercaderia, embarque);
        }
        Collections.sort(cargas);
        return cargas;
    }
    
}
