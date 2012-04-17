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
import java.util.Arrays;
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
            CargaPrevia carga = nuevaCargaPrevia(bodega, mercaderia, embarque);
            cargas.add(carga);
        }
        Collections.sort(cargas);                
        return cargas;
    }

    private CargaPrevia nuevaCargaPrevia(Bodega bodega, Mercaderia mercaderia, Embarque embarque) {
        CargaPrevia carga = new CargaPrevia();
        carga.setBodega(bodega);
        carga.setMercaderia(mercaderia);
        carga.setEmbarque(embarque);
        return carga;
    }
    
    
    public List<CargaPrevia> obtenerCargasPrevias(Buque buque, Mercaderia mercaderia, Embarque embarque){
        CargaPrevia[] cargasPrevias = new CargaPrevia[buque.getBodegaCollection().size()];

        //Asigno por defecto la mercaderia a las cargas previas que no tengan mercaderia asignada
        //Y los pongo en el arreglo para agregar las cp que falten o pisar las que sobren
        if (embarque.getCargaPreviaCollection() != null){ 
            for (CargaPrevia cp : embarque.getCargaPreviaCollection()){
                if (cp.getMercaderia() == null){
                    cp.setMercaderia(mercaderia);    
                }                
                cargasPrevias[cp.getBodega().getNro() - 1] = cp;
            }
        }

        //Si no encuentro una cp para cada bodega del buque, le creo una nueva
        for (Bodega bod : buque.getBodegaCollection()){
            if (cargasPrevias[bod.getNro() - 1 ] == null){
                cargasPrevias[bod.getNro() - 1] = nuevaCargaPrevia(bod, mercaderia, embarque);
            }
        }
            
        Arrays.sort(cargasPrevias);
        return Arrays.asList(cargasPrevias);
    }
    
}
