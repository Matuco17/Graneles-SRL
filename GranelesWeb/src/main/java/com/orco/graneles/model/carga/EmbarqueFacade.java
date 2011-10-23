/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.carga;

import com.orco.graneles.domain.carga.ArchivoEmbarque;
import com.orco.graneles.domain.carga.Embarque;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;
/**
 *
 * @author orco
 */
@Stateless
public class EmbarqueFacade extends AbstractFacade<Embarque> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    
    @EJB
    private ArchivoEmbarqueFacade archivoEmbarqueFacade;
    

    protected EntityManager getEntityManager() {
        return em;
    }

    public EmbarqueFacade() {
        super(Embarque.class);
    }
    
    
    /**
     * Metodo factory que crea un nuevo embarque con los datos necesarios nuevos
     * @return 
     */
    public Embarque crearNuevoEmbarque(){
        Embarque nuevoEmbarque = new Embarque();
        
        //Codigo
        //Debo buscar el maximo codigo de este año y devuelvo uno más, si no encuentro entonces es el primero
        int anio = (new GregorianCalendar()).get(Calendar.YEAR);
        Long maximo = null;
        try {
            maximo = getEntityManager().createQuery("SELECT max(e.codigo) "
                                                   + "FROM Embarque e "
                                                   + "WHERE e.codigo < :limiteSuperior "
                                                   + "AND e.codigo > :limiteInferior ", Long.class)
                                        .setParameter("limiteSuperior", (anio + 1) * 1000)
                                        .setParameter("limiteInferior", anio * 1000)
                                        .getSingleResult();
        } catch (NoResultException e) {            
            maximo = null;
        }
        if (maximo == null)  
            maximo = new Long(anio * 1000);
        
        nuevoEmbarque.setCodigo(maximo + 1);
        
        
        return nuevoEmbarque;
    }
    
    /**
     * Metodo que realiza la subida del archivo al servidor y lo guarda en la base de datos asociado al embarque
     * @param fip
     * @param fileName
     * @param embarque
     * @return 
     */
    public void subirArchivo(InputStream fip, String fileName, Embarque embarque){
        ArchivoEmbarque nuevoArchivo = new ArchivoEmbarque();
        nuevoArchivo.setEmbarque(embarque);
        nuevoArchivo.setNombreArchivo(fileName);
        FileOutputStream fop = null;
        try {
            //Obtengo el contenido del archivo y lo guardo
            String pathBaseArchivos = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        
            fop = new FileOutputStream(pathBaseArchivos + nuevoArchivo.getNombreArchivoEnDisco());
            byte[] contenido = new byte[fip.available()];
            fip.read(contenido);
            fop.write(contenido);
            
            //Si pasa todo el grabado, entonces realizo la persistencia
            archivoEmbarqueFacade.persist(nuevoArchivo);
            embarque.getArchivoEmbarqueCollection().add(nuevoArchivo);
            
            fop.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EmbarqueFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EmbarqueFacade.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fip.close();
                if (fop != null)
                    fop.close();
            } catch (IOException ex) {
                Logger.getLogger(EmbarqueFacade.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
