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
        
        int cantBodegas = 0;
        if (embarque.getBuque().getBodegaCollection() != null && embarque.getBuque().getBodegaCollection().size() > 0){
            cantBodegas = embarque.getBuque().getBodegaCollection().size();
        }
        
        //Realizo el proceso de calculos
        totalCargadoXBodega = new BigDecimal[cantBodegas+1];
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
        
        cargasPrevias = new CargaPrevia[cantBodegas+1];
        for (CargaPrevia cp : embarque.getCargaPreviaCollection()){
            cargasPrevias[cp.getBodega().getNro()] = cp;
            agregarAlListaTotal(totalesCargaPrevia, cp.getMercaderia().getDescripcion(), cp.getCarga());
            agregarAlListaTotal(totalesCargaEnBuque, cp.getMercaderia().getDescripcion(), cp.getCarga());
        }
        
        totalEnBuqueXBodega = new BigDecimal[cantBodegas+1];
        for(int i = 1; i < totalEnBuqueXBodega.length; i++){
            if (cargasPrevias[i] != null && cargasPrevias[i].getCarga() != null) {
                totalEnBuqueXBodega[i] = cargasPrevias[i].getCarga().add(totalCargadoXBodega[i]);
            } else {
                totalEnBuqueXBodega[i] = BigDecimal.ZERO.add(totalCargadoXBodega[i]);
            }
        }
        
        //Pto, atenterior
        totalCargaPrevia = BigDecimal.ZERO;
        for(int i = 1; i < cargasPrevias.length; i++){
            if (cargasPrevias[i] != null && cargasPrevias[i].getCarga() != null)
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
    private BigDecimal getCargado(int num){
        if (totalCargadoXBodega.length > num && totalCargadoXBodega[num] != null){
            return totalCargadoXBodega[num];
        } else {
            return BigDecimal.ZERO;
        }
    }
    
    public BigDecimal getCargadoBod1(){
        return getCargado(1);
    }
    public BigDecimal getCargadoBod2(){
        return getCargado(2);
    }
    public BigDecimal getCargadoBod3(){
        return getCargado(3);
    }
    public BigDecimal getCargadoBod4(){
        return getCargado(4);
    }
    public BigDecimal getCargadoBod5(){
        return getCargado(5);
    }
    public BigDecimal getCargadoBod6(){
        return getCargado(6);
    }
    public BigDecimal getCargadoBod7(){
        return getCargado(7);
    }
    public BigDecimal getCargadoBod8(){
        return getCargado(8);
    }
    public BigDecimal getCargadoBod9(){
        return getCargado(9);
    }
    
    //Carga Previa
    private BigDecimal getCargaPrevia(int num){
        if (cargasPrevias.length > num && cargasPrevias[num] != null){
            return cargasPrevias[num].getCarga();
        } else {
            return BigDecimal.ZERO;
        }
    }
    
    
    public BigDecimal getCargaPreviaBod1(){
        return getCargaPrevia(1);
    }
    public BigDecimal getCargaPreviaBod2(){
        return getCargaPrevia(2);
    }
    public BigDecimal getCargaPreviaBod3(){
        return getCargaPrevia(3);
    }
    public BigDecimal getCargaPreviaBod4(){
        return getCargaPrevia(4);
    }
    public BigDecimal getCargaPreviaBod5(){
        return getCargaPrevia(5);
    }
    public BigDecimal getCargaPreviaBod6(){
        return getCargaPrevia(6);
    }
    public BigDecimal getCargaPreviaBod7(){
        return getCargaPrevia(7);
    }
    public BigDecimal getCargaPreviaBod8(){
        return getCargaPrevia(8);
    }
    public BigDecimal getCargaPreviaBod9(){
        return getCargaPrevia(9);
    }
    
    //Cargas Totales X Bodega
    private BigDecimal getEnBuqueBod(int num){
        if (totalEnBuqueXBodega.length > num && totalEnBuqueXBodega[num] != null){
            return totalEnBuqueXBodega[num];
        } else {
            return BigDecimal.ZERO;
        }
    }
    
    
    public BigDecimal getEnBuqueBod1(){
        return getEnBuqueBod(1);
    }
    public BigDecimal getEnBuqueBod2(){
        return getEnBuqueBod(2);
    }
    public BigDecimal getEnBuqueBod3(){
        return getEnBuqueBod(3);
    }
    public BigDecimal getEnBuqueBod4(){
        return getEnBuqueBod(4);
    }
    public BigDecimal getEnBuqueBod5(){
        return getEnBuqueBod(5);
    }
    public BigDecimal getEnBuqueBod6(){
        return getEnBuqueBod(6);
    }
    public BigDecimal getEnBuqueBod7(){
        return getEnBuqueBod(7);
    }
    public BigDecimal getEnBuqueBod8(){
        return getEnBuqueBod(8);
    }
    public BigDecimal getEnBuqueBod9(){
        return getEnBuqueBod(9);
    }
    
    //Mercaderia
    private String getMercaderiaBod(int num){
        if (cargasPrevias.length > num && cargasPrevias[num] != null){
            return cargasPrevias[num].getMercaderia().getDescripcion();
        } else {
            return "N/A.";
        }
    }
    
    
    public String getMercaderiaBod1(){
        return getMercaderiaBod(1);
    }
    public String getMercaderiaBod2(){
        return getMercaderiaBod(2);
    }
    public String getMercaderiaBod3(){
        return getMercaderiaBod(3);
    }
    public String getMercaderiaBod4(){
        return getMercaderiaBod(4);
    }
    public String getMercaderiaBod5(){
        return getMercaderiaBod(5);
    }
    public String getMercaderiaBod6(){
        return getMercaderiaBod(6);
    }
    public String getMercaderiaBod7(){
        return getMercaderiaBod(7);
    }
    public String getMercaderiaBod8(){
        return getMercaderiaBod(8);
    }
    public String getMercaderiaBod9(){
        return getMercaderiaBod(9);
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
