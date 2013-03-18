/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.carga;

import com.orco.graneles.domain.carga.*;
import com.orco.graneles.domain.facturacion.Empresa;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.reports.ResumenCargasPorCargador;
import com.orco.graneles.vo.CargaTurnoVO;
import com.orco.graneles.vo.ResumenCargaEmbarqueVO;
import com.orco.graneles.vo.TurnoObservacionVO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author orco
 */
@Stateless
public class CargaTurnoFacade extends AbstractFacade<CargaTurno> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public CargaTurnoFacade() {
        super(CargaTurno.class);
    }
    
    /**
     * Metodo que crea una carga nueva para un buque seleccioando de acuerdo al coordinador
     * @param tembarque
     * @param coordinador
     * @return 
     */
    public CargaTurno cargarNuevaPorBuque(TurnoEmbarque tembarque){
        
        CargaTurno cargaTurno = new CargaTurno();
        cargaTurno.setTurnoEmbarque(tembarque);
        cargaTurno.setCargador(tembarque.getEmbarque().getCoordinador());
        cargaTurno.setCargasCollection(new ArrayList<CargaTurnoCargas>());
        
        completarCargas(tembarque, cargaTurno);
        
        return cargaTurno;
    }

    private void completarCargas(TurnoEmbarque tembarque, CargaTurno cargaTurno) {
        for (CargaPrevia cargaOriginal : tembarque.getEmbarque().getCargaPreviaCollection()){
            CargaTurnoCargas cargaTC = new CargaTurnoCargas();
            cargaTC.setCargaOriginalBodega(cargaOriginal);
            cargaTC.setCarga(BigDecimal.ZERO);
            cargaTC.setCargaTurno(cargaTurno);
            cargaTurno.getCargasCollection().add(cargaTC);
        }
    }
    
        
    public List<CargaTurno> obtenerCargas(TurnoEmbarque tembarque){
         List<CargaTurno> cargas = null;
        if (tembarque.getId() != null && tembarque.getCargaTurnoCollection() != null && tembarque.getCargaTurnoCollection().size() > 0){
            
            for (CargaTurno ct : tembarque.getCargaTurnoCollection()){
                if (ct.getCargasCollection() == null || ct.getCargasCollection().size() == 0){
                    completarCargas(tembarque, ct);
                }
            }            
            cargas = new ArrayList<CargaTurno>(tembarque.getCargaTurnoCollection());
        
        } else {
            cargas = new ArrayList<CargaTurno>();
            cargas.add(cargarNuevaPorBuque(tembarque));
        }
        Collections.sort(cargas);
        return cargas;
    }
    
    public List<CargaTurno> obtenerCargasSinFacturar(Embarque embarque, Empresa cargador){
        return getEntityManager().createNamedQuery("CargaTurno.findByEmbarqueYCargadorSinFacturar", CargaTurno.class)
                .setParameter("cargador", cargador)
                .setParameter("embarque", embarque)
                .getResultList();
    }
    
     public List<CargaTurnoVO> completarCargaTurnosXCargador(Embarque embarque) {
        ResumenCargaEmbarqueVO resumenCarga = new ResumenCargaEmbarqueVO(embarque);
    
        List<CargaTurnoVO> cargasTurnos = new ArrayList<CargaTurnoVO>();
        
        for (TurnoEmbarque te : embarque.getTurnoEmbarqueCollection()){
            for (CargaTurno ct : te.getCargaTurnoCollection()){
                cargasTurnos.add(new CargaTurnoVO(ct, resumenCarga));
            }
        }
        
        //Completo las observaciones
        Map<Long, List<TurnoObservacionVO>> mapObservaciones = new HashMap<Long, List<TurnoObservacionVO>>();
        for (EmbarqueCargador ec : embarque.getEmbarqueCargadoresCollection()){
            mapObservaciones.put(ec.getCargador().getId(), new ArrayList<TurnoObservacionVO>());
        }
        
        //Agrego las observaciones, si no figura cargador, entonces se los agrego a cada empresa que cargo en ese turno
        for (TurnoEmbarque te : embarque.getTurnoEmbarqueCollection()){
            for (TurnoEmbarqueObservaciones teObs : te.getTurnoEmbarqueObservacionesCollection()){
                if (teObs.getCargador() != null){
                    mapObservaciones.get(teObs.getCargador().getId()).add(new TurnoObservacionVO(teObs));
                } else {
                    for (CargaTurno ct : te.getCargaTurnoCollection()){
                        mapObservaciones.get(ct.getCargador().getId()).add(new TurnoObservacionVO(teObs));
                    }
                }
            }
        }
        
        
        Collections.sort(cargasTurnos, new ComparadorCargaTurnoXFechaTurnoCoordinador());
                
        //Completo el valor del calculo de abordo
        BigDecimal totalAcumulado = BigDecimal.ZERO;
        Long coordinadorActualId = 0L;
        
        for (CargaTurnoVO ctVO : cargasTurnos){
            if (!coordinadorActualId.equals(ctVO.getCoordinadorId())){
                totalAcumulado = BigDecimal.ZERO;
                coordinadorActualId = ctVO.getCoordinadorId();
            }
            totalAcumulado = totalAcumulado.add(ctVO.getTotalCargaTurno());
            ctVO.setAcumulado(ctVO.getAcumulado().add(totalAcumulado));
            ctVO.setObservaciones(mapObservaciones.get(ctVO.getCoordinadorId()));
        }
        
        return cargasTurnos;
    }
     
     
     private class ComparadorCargaTurnoXFechaTurnoCoordinador implements Comparator<CargaTurnoVO>{

        @Override
        public int compare(CargaTurnoVO o1, CargaTurnoVO o2) {
            if (o1.getCoordinadorId().equals(o2.getCoordinadorId())){
                if (o1.getTurnoFecha().equals(o2.getTurnoFecha())){
                    return o1.getTurno().compareTo(o2.getTurno());                    
                } else {
                    return o1.getTurnoFecha().compareTo(o2.getTurnoFecha());
                }
            } else {
                return o1.getCoodinadorNombre().compareToIgnoreCase(o2.getCoodinadorNombre());
            }                
        }        
    }
    
    
}
