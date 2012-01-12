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

/**
 *
 * @author orco
 */
public class ResumenCargaEmbarqueVO {
    
    private Embarque embarque;
    
    private BigDecimal[] totalCargadoXBodega;
    private CargaPrevia[] cargasPrevias; //cargas previas del embarque ordenadas
    private BigDecimal[] totalEnBuqueXBodega;
    
    private BigDecimal totalCargaActual;
    private BigDecimal totalCargaPrevia;
    private BigDecimal totalEnBuque;
    
    private List<TotalesCargaVO> totalesCargaActual;
    private List<TotalesCargaVO> totalesCargaPrevia;
    private List<TotalesCargaVO> totalesCargaEnBuque;
    
    
    public ResumenCargaEmbarqueVO(Embarque embarque){
        this.embarque = embarque;
        totalesCargaActual = new ArrayList<TotalesCargaVO>();
        totalesCargaEnBuque = new ArrayList<TotalesCargaVO>();
        totalesCargaPrevia = new ArrayList<TotalesCargaVO>();
        
        //Realizo el proceso de calculos
        totalCargadoXBodega = new BigDecimal[8];
        for(int i = 0; i < totalCargadoXBodega.length; i++)
            totalCargadoXBodega[i] = BigDecimal.ZERO;
        
        for (TurnoEmbarque te : embarque.getTurnoEmbarqueCollection()){
            for (CargaTurno ct : te.getCargaTurnoCollection()){
                for (CargaTurnoCargas ctc : ct.getCargasCollection()){
                    totalCargadoXBodega[ctc.getNroBodega()] = totalCargadoXBodega[ctc.getNroBodega()].add(ctc.getCarga());
                    agregarAlListaTotal(totalesCargaActual, ctc.getMercaderiaBodega().getDescripcion(), ctc.getCarga());
                    agregarAlListaTotal(totalesCargaEnBuque, ctc.getMercaderiaBodega().getDescripcion(), ctc.getCarga());
                }
            }
        }
        
        cargasPrevias = new CargaPrevia[8];
        for (CargaPrevia cp : embarque.getCargaPreviaCollection()){
            cargasPrevias[cp.getBodega().getNro()] = cp;
            agregarAlListaTotal(totalesCargaPrevia, cp.getMercaderia().getDescripcion(), cp.getCarga());
            agregarAlListaTotal(totalesCargaEnBuque, cp.getMercaderia().getDescripcion(), cp.getCarga());
        }
        
        totalEnBuqueXBodega = new BigDecimal[8];
        for(int i = 1; i < totalEnBuqueXBodega.length; i++){
            if (cargasPrevias[i].getCarga() != null) {
                totalEnBuqueXBodega[i] = cargasPrevias[i].getCarga().add(totalCargadoXBodega[i]);
            } else {
                totalEnBuqueXBodega[i] = BigDecimal.ZERO.add(totalCargadoXBodega[i]);
            }
        }
        
        //Pto, atenterior
        totalCargaPrevia = BigDecimal.ZERO;
        for(int i = 1; i < cargasPrevias.length; i++){
            if (cargasPrevias[i].getCarga() != null)
                totalCargaPrevia = totalCargaPrevia.add(cargasPrevias[i].getCarga());
        }
        
        //Total carga actual
        totalCargaActual = BigDecimal.ZERO;
        for(int i = 1; i < totalCargadoXBodega.length; i++){
            totalCargaActual = totalCargaActual.add(totalCargadoXBodega[i]);           
        }
        
        totalEnBuque = totalCargaPrevia.add(totalCargaActual);
        
        Collections.sort(totalesCargaActual);
        Collections.sort(totalesCargaEnBuque);
        Collections.sort(totalesCargaPrevia);
        
    }
    
    
    private void agregarAlListaTotal(List<TotalesCargaVO> lista, String mercaderiaNombre, BigDecimal carga){
        for (TotalesCargaVO tcVO : lista){
            if (tcVO.getMercaderia().equalsIgnoreCase(mercaderiaNombre)){
                if (tcVO.getCarga() == null)
                    tcVO.setCarga(BigDecimal.ZERO);
                if (carga != null)
                    tcVO.setCarga(tcVO.getCarga().add(carga));
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
    
    //Cargas Turnos
    public BigDecimal getCargadoBod1(){
        return totalCargadoXBodega[1];
    }
    public BigDecimal getCargadoBod2(){
        return totalCargadoXBodega[2];
    }
    public BigDecimal getCargadoBod3(){
        return totalCargadoXBodega[3];
    }
    public BigDecimal getCargadoBod4(){
        return totalCargadoXBodega[4];
    }
    public BigDecimal getCargadoBod5(){
        return totalCargadoXBodega[5];
    }
    public BigDecimal getCargadoBod6(){
        return totalCargadoXBodega[6];
    }
    public BigDecimal getCargadoBod7(){
        return totalCargadoXBodega[7];
    }
    
    //Carga Previa
    public BigDecimal getCargaPreviaBod1(){
        return cargasPrevias[1].getCarga();
    }
    public BigDecimal getCargaPreviaBod2(){
        return cargasPrevias[2].getCarga();
    }
    public BigDecimal getCargaPreviaBod3(){
        return cargasPrevias[3].getCarga();
    }
    public BigDecimal getCargaPreviaBod4(){
        return cargasPrevias[4].getCarga();
    }
    public BigDecimal getCargaPreviaBod5(){
        return cargasPrevias[5].getCarga();
    }
    public BigDecimal getCargaPreviaBod6(){
        return cargasPrevias[6].getCarga();
    }
    public BigDecimal getCargaPreviaBod7(){
        return cargasPrevias[7].getCarga();
    }
    
    //Cargas Totales X Bodega
    public BigDecimal getEnBuqueBod1(){
        return totalEnBuqueXBodega[1];
    }
    public BigDecimal getEnBuqueBod2(){
        return totalEnBuqueXBodega[2];
    }
    public BigDecimal getEnBuqueBod3(){
        return totalEnBuqueXBodega[3];
    }
    public BigDecimal getEnBuqueBod4(){
        return totalEnBuqueXBodega[4];
    }
    public BigDecimal getEnBuqueBod5(){
        return totalEnBuqueXBodega[5];
    }
    public BigDecimal getEnBuqueBod6(){
        return totalEnBuqueXBodega[6];
    }
    public BigDecimal getEnBuqueBod7(){
        return totalEnBuqueXBodega[7];
    }
    
    //Mercaderia
    public String getMercaderiaBod1(){
        return cargasPrevias[1].getMercaderia().getDescripcion();
    }
    public String getMercaderiaBod2(){
        return cargasPrevias[2].getMercaderia().getDescripcion();
    }
    public String getMercaderiaBod3(){
        return cargasPrevias[3].getMercaderia().getDescripcion();
    }
    public String getMercaderiaBod4(){
        return cargasPrevias[4].getMercaderia().getDescripcion();
    }
    public String getMercaderiaBod5(){
        return cargasPrevias[5].getMercaderia().getDescripcion();
    }
    public String getMercaderiaBod6(){
        return cargasPrevias[6].getMercaderia().getDescripcion();
    }
    public String getMercaderiaBod7(){
        return cargasPrevias[7].getMercaderia().getDescripcion();
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
    
    
}
