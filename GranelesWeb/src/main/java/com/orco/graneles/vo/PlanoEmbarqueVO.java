/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.carga.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author orco
 */
public class PlanoEmbarqueVO {
    
    private Embarque embarque;
    
    private BigDecimal totalCargaActual;
    private BigDecimal totalCargaPrevia;
    private BigDecimal totalEnBuque;
    
    private List<TotalesCargaVO> totalesCargaActual;
    private List<TotalesCargaVO> totalesCargaPrevia;
    private List<TotalesCargaVO> totalesCargaEnBuque;
    private List<PlanoCargaVO> planosBodegas;
    
    
    public PlanoEmbarqueVO(Embarque embarque){
        this.embarque = embarque;
        totalesCargaActual = new ArrayList<TotalesCargaVO>();
        totalesCargaEnBuque = new ArrayList<TotalesCargaVO>();
        totalesCargaPrevia = new ArrayList<TotalesCargaVO>();
        
        //Creo lo elementos del plano de carga
        planosBodegas = new ArrayList<PlanoCargaVO>();
        for (int i=0; i < embarque.getBuque().getBodegaCollection().size(); i++){
            planosBodegas.add(new PlanoCargaVO(i+1));
        }
        
        for (TurnoEmbarque te : embarque.getTurnoEmbarqueCollection()){
            for (CargaTurno ct : te.getCargaTurnoCollection()){
                for (CargaTurnoCargas ctc : ct.getCargasCollection()){
                    PlanoCargaVO pcVO = planosBodegas.get(ctc.getNroBodega()-1);
                    pcVO.setCarga(pcVO.getCarga().add(ctc.getCarga()));
                    
                    if (StringUtils.isEmpty(pcVO.getMercaderia())){
                        pcVO.setMercaderia(ctc.getMercaderiaBodega().getDescripcionIngles());
                    }
                    
                    agregarAlListaTotal(totalesCargaActual, ctc.getMercaderiaBodega().getDescripcionIngles(), ctc.getCarga());
                    agregarAlListaTotal(totalesCargaEnBuque, ctc.getMercaderiaBodega().getDescripcionIngles(), ctc.getCarga());
                }
            }
        }
        
       
        for (CargaPrevia cp : embarque.getCargaPreviaCollection()){
            PlanoCargaVO pcVO = planosBodegas.get(cp.getBodega().getNro()-1);
            if (cp.getCarga() != null){
                pcVO.setCargaPrevia(cp.getCarga());
            }
            if (StringUtils.isEmpty(cp.getPuertoAnterior())){
                pcVO.setPuertoPrevio(embarque.getPuertoAnterior());
            } else {
                pcVO.setPuertoPrevio(cp.getPuertoAnterior());
            }
            
            agregarAlListaTotal(totalesCargaPrevia, cp.getMercaderia().getDescripcionIngles(), cp.getCarga());
            agregarAlListaTotal(totalesCargaEnBuque, cp.getMercaderia().getDescripcionIngles(), cp.getCarga());
        }
        
        Collections.sort(totalesCargaActual);
        Collections.sort(totalesCargaEnBuque);
        Collections.sort(totalesCargaPrevia);
        Collections.reverse(planosBodegas);
        
    }
    
    
    private void agregarAlListaTotal(List<TotalesCargaVO> lista, String mercaderiaNombre, BigDecimal carga){
        for (TotalesCargaVO tcVO : lista){
            if (tcVO.getMercaderia().equalsIgnoreCase(mercaderiaNombre)){
                if (tcVO.getCarga() == null){
                    tcVO.setCarga(BigDecimal.ZERO);
                }
                if (carga != null){
                    tcVO.setCarga(tcVO.getCarga().add(carga));
                }
                return;
            }
        }
        lista.add(new TotalesCargaVO(mercaderiaNombre, carga));
    }
    
    public Long getEmbarqueId(){
        return embarque.getId();
    }
    
    public Long getEmbarqueCodigo(){
        return embarque.getCodigo();
    }
    
    public String getNombreBuque(){
        return embarque.getBuque().getDescripcion();
    }
    
    public Date getFechaReporte(){
        return new Date();
    }
    
    public String getPuertoDestino(){
        return embarque.getDestino();
    }
    
    public Integer getCantidadBodegas(){
        return embarque.getBuque().getBodegaCollection().size();
    }
    
    //TOTALES
    public BigDecimal getTotalPuertoAnterior(){
        return totalCargaPrevia;
    }
    
    public BigDecimal getTotalCargaActual(){
        return totalCargaActual;
    } 
    
    public BigDecimal getTotalCargaBuque(){
        return totalEnBuque;
    }

    public List<TotalesCargaVO> getTotalesCargaActual() {
        return totalesCargaActual;
    }

    public List<TotalesCargaVO> getTotalesCargaEnBuque() {
        return totalesCargaEnBuque;
    }

    public List<TotalesCargaVO> getTotalesCargaPrevia() {
        return totalesCargaPrevia;
    }

    public List<PlanoCargaVO> getPlanosBodegas() {
        return planosBodegas;
    }
    
    public String getPuertoAnterior(){
        return embarque.getPuertoAnterior();
    }
}
