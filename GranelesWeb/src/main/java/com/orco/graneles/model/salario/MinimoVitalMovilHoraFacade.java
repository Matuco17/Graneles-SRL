/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.salario.MinimoVitalMovilHora;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.NegocioException;
import java.util.Date;
import java.util.List;
import javax.persistence.NoResultException;

/**
 *
 * @author orco
 */
@Stateless
public class MinimoVitalMovilHoraFacade extends AbstractFacade<MinimoVitalMovilHora> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public MinimoVitalMovilHoraFacade() {
        super(MinimoVitalMovilHora.class);
    }

    @Override
    public void create(MinimoVitalMovilHora entity) {
        if (existeMinimoVitalMovilHora(entity)){
            throw new NegocioException("Ya existe un Minimo Vital y Movil definido para los datos seleccionados");
        } else {
            super.create(entity);
        }
    }

    @Override
    public void edit(MinimoVitalMovilHora entity) {
        if (existeMinimoVitalMovilHora(entity)){
            throw new NegocioException("Ya existe un Minimo Vital y Movil definido para los datos seleccionados");
        } else {
            super.edit(entity);
        }
    }
    
    /**
     * Método que busca todos los MinimoVitalMovilHoras que peuden colisionar con el MinimoVitalMovilHora basico en cuestion
     * @param entity
     * @return si existen o no MinimoVitalMovilHoras para la búsqueda especificada
     */
    public boolean existeMinimoVitalMovilHora(MinimoVitalMovilHora entity){
        boolean existe = false;
        
        List<MinimoVitalMovilHora> minimos = getEntityManager().createNamedQuery("MinimoVitalMovilHora.findAll", MinimoVitalMovilHora.class)
                                    .getResultList();
        //Una vez que obtuve los MinimoVitalMovilHoras, tengo que verifiacar si no colisionan con ninguna otra entidad
        if (minimos != null && minimos.size() > 0){
            for (MinimoVitalMovilHora mvmh : minimos){
                if (!mvmh.getId().equals(entity.getId())){
                    if (mvmh.getHasta() == null && entity.getHasta() == null){
                        return true;
                    } else if (mvmh.getHasta() == null && entity.getHasta() != null){
                        if (entity.getHasta().after(mvmh.getHasta())){
                            return true;
                        }
                    } else if (mvmh.getHasta() != null && entity.getHasta() == null){
                        if (mvmh.getHasta().after(entity.getDesde())){
                            return true;
                        }
                    } else { //Ambas hasta no son nulas debo comparar por rango
                        if (entity.getDesde().before(mvmh.getHasta()) && mvmh.getHasta().before(entity.getHasta())
                            || entity.getDesde().before(mvmh.getDesde()) && mvmh.getDesde().before(entity.getHasta())
                           ){
                            return true;
                        } else if (mvmh.getDesde().before(entity.getDesde()) && entity.getHasta().before(mvmh.getHasta())){
                            return true;
                        }
                    }
                }
            }
        }
        
        return existe;
    }
    
    /**
     * Obtengo el MinimoVitalMovilHora activo para la Tarea, categoría y fecha especificada
     * @param tarea
     * @param categoria
     * @param fecha
     * @return 
     */
    public MinimoVitalMovilHora obtenerMinimoVitalMovilHoraActivo(Date fecha){
        try {
            return getEntityManager().createNamedQuery("MinimoVitalMovilHora.findActivo", MinimoVitalMovilHora.class)
                        .setParameter("fecha", fecha)
                        .getSingleResult();         
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Devuelve los MinimoVitalMovilHoras activos de acuerdo a la fecha especificada
     * @param fecha
     * @return 
     */
    public List<MinimoVitalMovilHora> obtenerMinimoVitalMovilHorasActivos(Date fecha){
        return getEntityManager().createNamedQuery("MinimoVitalMovilHoraBasico.findActivos", MinimoVitalMovilHora.class)
                        .setParameter("fecha", fecha)
                        .getResultList();
    }
}

