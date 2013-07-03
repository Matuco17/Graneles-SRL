/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model;

import com.orco.graneles.domain.EntidadAuditable;
import com.orco.graneles.domain.miscelaneos.Auditoria;
import com.orco.graneles.model.miscelaneos.AuditoriaFacade;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import org.apache.commons.beanutils.BeanUtils;

public abstract class AbstractFacade<T> {
    private Class<T> entityClass;

    @EJB
    private AuditoriaFacade auditoriaF;
    
    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
        try {
            if (entity instanceof EntidadAuditable){
                Auditoria a = new Auditoria();
                a.setEntidadId(-1L);
                a.setClase(entity.getClass().getSimpleName());
                a.setUsuarioCreacion(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
                a.setFechaCreacion(new Date());
                a.setUsuarioModificacion(a.getUsuarioCreacion());
                a.setFechaModificacion(a.getFechaCreacion());

                EntidadAuditable eA = (EntidadAuditable) entity;
                eA.setAuditoria(a);
            }
            getEntityManager().persist(entity);
                        
            getEntityManager().flush();
            
            if (entity instanceof EntidadAuditable){
                Auditoria a = ((EntidadAuditable) entity).getAuditoria();
                try {
                    a.setEntidadId(Long.parseLong(BeanUtils.getProperty(entity, "id")));                    
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException ex) {
                    Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (EJBException e) {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void edit(T entity) {
        try {
            if (entity instanceof EntidadAuditable){
                Auditoria a = ((EntidadAuditable) entity).getAuditoria();
                
                if (a == null){
                    crearAuditoria(entity);
                } else {
                    //Edito solamente la auditoria
                    a.setUsuarioModificacion(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
                    a.setFechaModificacion(new Date());

                    auditoriaF.edit(a);
                }
            }
            
            getEntityManager().merge(entity);
            
        } catch (EJBException e) {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
     public void persist(T entity){
        try {
            if ( BeanUtils.getProperty(entity, "id") == null){
                create(entity);
            } else {
                edit(entity);
            }
        } catch (IllegalAccessException ex) {
            getEntityManager().persist(entity);
        } catch (InvocationTargetException ex) {
            getEntityManager().persist(entity);
        } catch (NoSuchMethodException ex) {
            getEntityManager().persist(entity);
        } catch (EJBException e) {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, e);
        }        
    }

    public void remove(T entity) {
        try {
            getEntityManager().remove(getEntityManager().merge(entity));
        } catch (EJBException e) {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public T find(Object id) {
        try {
            return getEntityManager().find(entityClass, id);
        } catch (EJBException e) {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    public List<T> findAll() {
        try {
            javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
            cq.select(cq.from(entityClass));
            return getEntityManager().createQuery(cq).getResultList();
        } catch (EJBException e) {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, e);
            return new ArrayList<T>();
        }
    }

    public List<T> findRange(int[] range) {
        try {
            javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
            cq.select(cq.from(entityClass));
            javax.persistence.Query q = getEntityManager().createQuery(cq);
            q.setMaxResults(range[1] - range[0]);
            q.setFirstResult(range[0]);
            return q.getResultList();
        } catch (EJBException e) {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, e);
            return new ArrayList<T>();
        }
    }

    public int count() {
        try {
            javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
            javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
            cq.select(getEntityManager().getCriteriaBuilder().count(rt));
            javax.persistence.Query q = getEntityManager().createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } catch (EJBException e) {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, e);
            return -1;
        }
    }

    private void crearAuditoria(T entity) throws NumberFormatException {
        Auditoria a = new Auditoria();
        try {
            if (BeanUtils.getProperty(entity, "id") != null){
                a.setEntidadId(Long.parseLong(BeanUtils.getProperty(entity, "id")));
            }
            
            a.setClase(entity.getClass().getSimpleName());
            a.setUsuarioCreacion(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
            a.setFechaCreacion(new Date());
            a.setUsuarioModificacion(a.getUsuarioCreacion());
            a.setFechaModificacion(a.getFechaCreacion());
            
            EntidadAuditable eA = (EntidadAuditable) entity;
            eA.setAuditoria(a);

        } catch (IllegalAccessException ex) {
            Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
