/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.facturacion;

import com.orco.graneles.domain.facturacion.Tarifa;
import com.orco.graneles.model.salario.*;
import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Tarea;
import com.orco.graneles.domain.salario.TipoJornal;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.NegocioException;
import com.orco.graneles.model.carga.TrabajadoresTurnoEmbarqueFacade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.persistence.NoResultException;
import org.apache.commons.lang.time.DateUtils;
/**
 *
 * @author orco
 */
@Stateless
public class TarifaFacade extends AbstractFacade<Tarifa> {
    public static final String MESSAGE_EXISTE_TARIFA = "Ya existe una tarifa  definido para los datos seleccionados";
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }
    
    @EJB
    TrabajadoresTurnoEmbarqueFacade trabajadorTurnoEmbarqueF;

    public TarifaFacade() {
        super(Tarifa.class);
    }

    @Override
    public void create(Tarifa entity) {
        if (existeTarifa(entity)){
            throw new NegocioException(MESSAGE_EXISTE_TARIFA);
        } else {
            super.create(entity);
        }
    }

    @Override
    public void edit(Tarifa entity) {
        if (existeTarifa(entity)){
            throw new NegocioException(MESSAGE_EXISTE_TARIFA);
        } else {
            super.edit(entity);
        }
    }
    
    /**
     * Método que busca todos los salarios que peuden colisionar con el salario basico en cuestion
     * @param entity
     * @return si existen o no salarios para la búsqueda especificada
     */
    public boolean existeTarifa(Tarifa entity){
        boolean existe = false;
        
        List<Tarifa> tarifas = getEntityManager().createNamedQuery("Tarifa.findByPrincipalKey", Tarifa.class)
                                    .setParameter("tipoJornal", entity.getTipoJornal())
                                    .getResultList();
        //Una vez que obtuve los salarios, tengo que verifiacar si no colisionan con ninguna otra entidad
        if (tarifas != null && tarifas.size() > 0){
            for (Tarifa t : tarifas){
                if (!t.getId().equals(entity.getId())){
                    if (t.getHasta() == null && entity.getHasta() == null){
                        return true;
                    } else if (t.getHasta() == null && entity.getHasta() != null){
                        if (entity.getHasta().after(t.getDesde())){
                            return true;
                        }
                    } else if (t.getHasta() != null && entity.getHasta() == null){
                        if (t.getHasta().after(entity.getDesde())){
                            return true;
                        }
                    } else { //Ambas hasta no son nulas debo comparar por rango
                        if (entity.getDesde().before(t.getHasta()) && t.getHasta().before(entity.getHasta())
                            || entity.getDesde().before(t.getDesde()) && t.getDesde().before(entity.getHasta())
                           ){
                            return true;
                        } else if (t.getDesde().before(entity.getDesde()) && entity.getHasta().before(t.getHasta())){
                            return true;
                        }
                    }
                }
            }
        }
        
        return existe;
    }
    
    /**
     * Obtengo el salario activo para la Tarea, categoría y fecha especificada
     * @param fecha
     * @return 
     */
    public Tarifa obtenerTarifaActiva(TipoJornal tipoJornal, Date fecha){
        try {
            return getEntityManager().createNamedQuery("Tarifa.findActivo", Tarifa.class)
                        .setParameter("tipoJornal", tipoJornal)
                        .setParameter("fecha", fecha)
                        .getSingleResult();         
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Devuelve las tarifas activas de acuerdo a la fecha especificada
     * @param fecha
     * @return 
     */
    public List<Tarifa> obtenerTarifasActivas(Date fecha){
        return getEntityManager().createNamedQuery("Tarifa.findActivos", Tarifa.class)
                        .setParameter("fecha", fecha)
                        .getResultList();
    }
    
    
    /**
     * Obtiene los salarios activos dados una o 2 fechas
     * @param tarea (obligatorio)
     * @param categoria (obligatorio)
     * @param desde (obligatorio)
     * @param hasta (opcional)
     * @return 
     */ 
    public List<Tarifa> obtenerTarifas(TipoJornal tipoJornal, Date desde, Date hasta){
        try {
            List<Tarifa> result = new ArrayList<Tarifa>();
            List<Tarifa> tarifas = getEntityManager().createNamedQuery("Tarifa.findByPrincipalKey", Tarifa.class)
                        .setParameter("tipoJornal", tipoJornal)
                        .getResultList();
            
            
            for (Tarifa t : tarifas){
                if (t.getHasta() != null && hasta != null){
                    if (t.getDesde().before(hasta) || t.getDesde().equals(hasta) || t.getHasta().after(desde) || t.getHasta().equals(desde)){
                        result.add(t);
                    }                    
                } else if (t.getHasta() == null && hasta != null){
                    if (t.getDesde().before(hasta) || t.getDesde().equals(hasta)){
                        result.add(t);
                    }
                } else if (t.getHasta() != null && hasta == null){
                    if (desde.before(t.getHasta()) || desde.equals(t.getHasta())){
                        result.add(t);
                    }
                } else if (t.getHasta() == null && hasta == null){
                    result.add(t);
                }
            }
          
            Collections.sort(result);
            
            return result;
        } catch (NoResultException e) {
            return new ArrayList<Tarifa>();
        }
    }
    
}

