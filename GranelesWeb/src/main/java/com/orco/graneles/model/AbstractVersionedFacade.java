
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJBException;
import javax.persistence.EntityManager;
import org.apache.commons.beanutils.BeanUtils;

public abstract class AbstractVersionedFacade<T> {

    protected Class<T> entityClass;

    public AbstractVersionedFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    protected abstract String getFindActiveNamedQuery();
    
    public void edit(T entity) {
        try {
            T original = (T) getEntityManager().find(entityClass, Long.valueOf(BeanUtils.getProperty(entity, "id")));
            if (!original.equals(entity)) {
                BeanUtils.setProperty(original, "actual", Boolean.FALSE);
                getEntityManager().merge(original);
                //entity.setActual(Boolean.TRUE);//no tiene q ser necesario
                BeanUtils.setProperty(entity, "versionAnterior", Long.valueOf(BeanUtils.getProperty(entity, "id")));
                getEntityManager().persist(entity);
            }
            //getEntityManager().merge(entity);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, ex);
        }catch (Exception ex)
        {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, ex);
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
            return getEntityManager().createNamedQuery(getFindActiveNamedQuery(), entityClass).getResultList();
        } catch (EJBException e) {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, e);
            return new ArrayList<T>();
        }
    }
    
    public List<T> findAllImpactoPositivo(String tipoImpacto) {
        try {
            return getEntityManager()
                .createNamedQuery("Impacto.findAllByTipoImpacto", entityClass)
                .setParameter("tipoImpacto", tipoImpacto).getResultList();
        } catch (EJBException e) {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, e);
            return new ArrayList<T>();
        }
    }
    
    public List<T> findAllImpactoEstrategiaImplementacion(String tipoImpacto) {
        try {
            return getEntityManager()
                .createNamedQuery("Impacto.findAllByTipoImpacto", entityClass)
                .setParameter("tipoImpacto", tipoImpacto).getResultList();
        } catch (EJBException e) {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, e);
            return new ArrayList<T>();
        }
    }
    
    public List<T> findAllImpactoNegativo(String tipoImpacto) {
        try {
            return getEntityManager()
                .createNamedQuery("Impacto.findAllByTipoImpacto", entityClass)
                .setParameter("tipoImpacto", tipoImpacto).getResultList();
        } catch (EJBException e) {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, e);
            return new ArrayList<T>();
        }
    }

    public List<T> findRange(int[] range) {
        try {
            return getEntityManager()
                .createNamedQuery(getFindActiveNamedQuery(), entityClass).setMaxResults(range[1] - range[0])
                .setFirstResult(range[0])
                .getResultList();
        } catch (EJBException e) {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, e);
            return new ArrayList<T>();
        }
    }

    public int count() {
        try {
            return getEntityManager().createNamedQuery(getFindActiveNamedQuery(), entityClass).getResultList().size();
        } catch (EJBException e) {
            Logger.getLogger(AbstractVersionedFacade.class.getName()).log(Level.SEVERE, null, e);
            return -1;
        }
    }
}
