/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Tarea;
import com.orco.graneles.domain.salario.SalarioBasico;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.NegocioException;
import java.util.Date;
import java.util.List;
import javax.persistence.NoResultException;
import org.apache.commons.lang.time.DateUtils;
/**
 *
 * @author orco
 */
@Stateless
public class SalarioBasicoFacade extends AbstractFacade<SalarioBasico> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public SalarioBasicoFacade() {
        super(SalarioBasico.class);
    }

    @Override
    public void create(SalarioBasico entity) {
        if (existeSalario(entity)){
            throw new NegocioException("Ya existe un Salario definido para los datos seleccionados");
        } else {
            super.create(entity);
        }
    }

    @Override
    public void edit(SalarioBasico entity) {
        if (existeSalario(entity)){
            throw new NegocioException("Ya existe un Salario definido para los datos seleccionados");
        } else {
            super.edit(entity);
        }
    }
    
    /**
     * Método que busca todos los salarios que peuden colisionar con el salario basico en cuestion
     * @param entity
     * @return si existen o no salarios para la búsqueda especificada
     */
    public boolean existeSalario(SalarioBasico entity){
        boolean existe = false;
        
        List<SalarioBasico> salarios = getEntityManager().createNamedQuery("SalarioBasico.findByPrincipalKey", SalarioBasico.class)
                                    .setParameter("categoria", entity.getCategoria())
                                    .setParameter("tarea", entity.getTarea())
                                    .getResultList();
        //Una vez que obtuve los salarios, tengo que verifiacar si no colisionan con ninguna otra entidad
        if (salarios != null && salarios.size() > 0){
            for (SalarioBasico sb : salarios){
                if (!sb.getId().equals(entity.getId())){
                    if (sb.getHasta() == null && entity.getHasta() == null){
                        return true;
                    } else if (sb.getHasta() == null && entity.getHasta() != null){
                        if (entity.getHasta().after(sb.getHasta())){
                            return true;
                        }
                    } else if (sb.getHasta() != null && entity.getHasta() == null){
                        if (sb.getHasta().after(entity.getDesde())){
                            return true;
                        }
                    } else { //Ambas hasta no son nulas debo comparar por rango
                        if (entity.getDesde().before(sb.getHasta()) && sb.getHasta().before(entity.getHasta())
                            || entity.getDesde().before(sb.getDesde()) && sb.getDesde().before(entity.getHasta())
                           ){
                            return true;
                        } else if (sb.getDesde().before(entity.getDesde()) && entity.getHasta().before(sb.getHasta())){
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
     * @param tarea
     * @param categoria
     * @param fecha
     * @return 
     */
    public SalarioBasico obtenerSalarioActivo(Tarea tarea, Categoria categoria, Date fecha){
        try {
            return getEntityManager().createNamedQuery("SalarioBasico.findActivo", SalarioBasico.class)
                        .setParameter("categoria", categoria)
                        .setParameter("tarea", tarea)
                        .setParameter("fecha", fecha)
                        .getSingleResult();         
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Devuelve los salarios activos de acuerdo a la fecha especificada
     * @param fecha
     * @return 
     */
    public List<SalarioBasico> obtenerSalariosActivos(Date fecha){
        return getEntityManager().createNamedQuery("SalarioBasico.findActivos", SalarioBasico.class)
                        .setParameter("fecha", fecha)
                        .getResultList();
    }
}

