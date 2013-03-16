/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.carga;

import com.orco.graneles.domain.carga.ArchivoBuque;
import com.orco.graneles.domain.carga.Buque;
import com.orco.graneles.domain.carga.Embarque;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
/**
 *
 * @author orco
 */
@Stateless
public class BuqueFacade extends AbstractFacade<Buque> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    
    @EJB
    private ArchivoBuqueFacade archivoBuqueFacade;

    protected EntityManager getEntityManager() {
        return em;
    }

    public BuqueFacade() {
        super(Buque.class);
    }
    
     /**
     * Metodo que realiza la subida del archivo al servidor y lo guarda en la base de datos asociado al embarque
     * @param fip
     * @param fileName
     * @param embarque
     * @return 
     */
    public void subirArchivo(InputStream fip, String fileName, Buque buque){
        ArchivoBuque nuevoArchivo = new ArchivoBuque();
        nuevoArchivo.setBuque(buque);
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
            archivoBuqueFacade.persist(nuevoArchivo);
            buque.getArchivoBuqueCollection().add(nuevoArchivo);
            
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
