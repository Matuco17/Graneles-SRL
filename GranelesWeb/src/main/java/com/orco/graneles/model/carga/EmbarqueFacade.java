/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.carga;

import com.orco.graneles.domain.carga.ArchivoEmbarque;
import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.carga.Embarque;
import com.orco.graneles.domain.carga.EmbarqueCargador;
import com.orco.graneles.domain.carga.TurnoEmbarque;
import com.orco.graneles.domain.facturacion.MovimientoCtaCteTons;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.NegocioException;
import com.orco.graneles.model.facturacion.MovimientoCtaCteTonsFacade;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
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
    @EJB
    private MovimientoCtaCteTonsFacade movimientoCtaCteFacade;

    /**
     * Método que busca el mayor embarque del año en cuestión, de ahi 
     * @return 
     */
    private Long maximoValorCodigoEmbarque(GregorianCalendar fechaAprox) {
        //Codigo
        //Debo buscar el maximo codigo de este año y devuelvo uno más, si no encuentro entonces es el primero
        int anio = (fechaAprox).get(Calendar.YEAR);
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
        return maximo;
    }
    

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
        Long maximo = maximoValorCodigoEmbarque(new GregorianCalendar());
        
        nuevoEmbarque.setCodigo(maximo + 1);
        nuevoEmbarque.setConsolidado(Boolean.TRUE);
        nuevoEmbarque.setConsolidadoEnBusqueda(Boolean.FALSE);
        nuevoEmbarque.setFacturado(Boolean.FALSE);
        
        return nuevoEmbarque;
    }
    
    /**
     * Se encarga de setear las propiedades respectivos si esta consolidado como no el embarque
     * @param embarque 
     */
    public void setearConsolidado(Embarque embarque){
        if (embarque.getConsolidado()){
            embarque.setCodigo(maximoValorCodigoEmbarque(new GregorianCalendar()) + 1);
        } else {
            embarque.setCodigo(null);
        }
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
                if (fop != null){
                    fop.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(EmbarqueFacade.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public Embarque find(Object id) {
        Embarque embarque = super.find(id);
    
        embarque.setConsolidadoEnBusqueda(embarque.getConsolidado());
        
        return embarque;
    }

    @Override
    public List<Embarque> findAll() {
        List<Embarque> embarques = super.findAll();
        
        for (Embarque e : embarques){
            e.setConsolidadoEnBusqueda(e.getConsolidado());
        }
        return embarques;
    }
    
    public List<Embarque> findAllSelected(){
        List<Embarque> embarques = findByConsolidado(Boolean.TRUE);
        Collections.sort(embarques);
        Collections.reverse(embarques);
        return embarques;
    }
    
    public List<Embarque> findByConsolidado(Boolean consolidado){
        List<Embarque> embarques = getEntityManager().createNamedQuery("Embarque.findByConsolidado", Embarque.class)
                                        .setParameter("consolidado", consolidado)
                                        .getResultList();
        
        for (Embarque e : embarques){
            e.setConsolidadoEnBusqueda(e.getConsolidado());
        }
        return embarques;
    }

    @Override
    public void edit(Embarque entity) {
        if (entity.getConsolidadoEnBusqueda() != null && entity.getConsolidadoEnBusqueda() && !entity.getConsolidado()){
            throw new NegocioException("No se puede desconosolidar un embarque previamente consolidado");
        }
        
        //Actualizo los movimientos en cuenta corriente de toneladas disponibles, remuevo los existentes y creo nuevos
        for (TurnoEmbarque t : entity.getTurnoEmbarqueCollection()) {
            for (CargaTurno ct : t.getCargaTurnoCollection()) {
                limpiarMovimientosToneladas(ct);
                crearMovimientosToneladas(ct);
            }
        }
        
        super.edit(entity);
    }

    private void limpiarMovimientosToneladas(CargaTurno ct) {
        for (MovimientoCtaCteTons m : ct.getMovimientoCtaCteCollection()) {
            movimientoCtaCteFacade.remove(m);
        }
        ct.setMovimientoCtaCteCollection(new ArrayList<MovimientoCtaCteTons>());
    }
    
    private void crearMovimientosToneladas(CargaTurno ct) {
        boolean deboCrearMovimiento = false;
        
        for (EmbarqueCargador ec : ct.getTurnoEmbarque().getEmbarque().getEmbarqueCargadoresCollection()) {
            deboCrearMovimiento |= ec.getEsCliente();
        }
        
        if (deboCrearMovimiento) {
            MovimientoCtaCteTons nuevoMovimiento = new MovimientoCtaCteTons();
            nuevoMovimiento.setEmpresa(ct.getCargador());
            nuevoMovimiento.setFecha(ct.getTurnoEmbarque().getFecha());
            nuevoMovimiento.setManual(false);
            nuevoMovimiento.setCargaTurno(ct);
            nuevoMovimiento.setTipoTurno(ct.getTurnoEmbarque().getTipo());
            nuevoMovimiento.setObservaciones(
                    "Embarque: " + ct.getTurnoEmbarque().getEmbarque().getCodigo() + 
                    ", Buque: " + ct.getTurnoEmbarque().getEmbarque().getBuque().getDescripcion());
            
            if (ct.getTurnoEmbarque().getEmbarque().getEstibamosNosotros()) {
                nuevoMovimiento.setValor(ct.getTotalCargado());
            } else {
                nuevoMovimiento.setValor(ct.getTotalCargado().negate());
            }
            
            //Agrego el nuevo movimiento
            ct.getMovimientoCtaCteCollection().add(nuevoMovimiento);
        }
        
    }
    
    public List<Embarque> findByFacturado(Boolean facturado){
        List<Embarque> embarques = getEntityManager().createNamedQuery("Embarque.findByFacturadoYConsolidado", Embarque.class)
                                        .setParameter("facturado", facturado)
                                        .setParameter("consolidado", Boolean.TRUE)
                                        .getResultList();
        
        for (Embarque e : embarques){
            e.setConsolidadoEnBusqueda(e.getConsolidado());
        }
        
        Collections.sort(embarques);
        return embarques;
    }
    
    
    
}
