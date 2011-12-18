/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.carga.CargaPrevia;
import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.carga.CargaTurnoCargas;
import com.orco.graneles.domain.carga.Embarque;
import com.orco.graneles.domain.carga.TurnoEmbarque;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author orco
 */
public class ResumenCargaEmbarqueVO {
    
    private Embarque embarque;
    
    private BigDecimal[] totalCargadoXBodega;
    private CargaPrevia[] cargasPrevias; //cargas previas del embarque ordenadas
    
    public ResumenCargaEmbarqueVO(Embarque embarque){
        this.embarque = embarque;
        
        //Realizo el proceso de calculos
        totalCargadoXBodega = new BigDecimal[8];
        for(int i = 0; i < totalCargadoXBodega.length; i++)
            totalCargadoXBodega[i] = BigDecimal.ZERO;
        
        for (TurnoEmbarque te : embarque.getTurnoEmbarqueCollection()){
            for (CargaTurno ct : te.getCargaTurnoCollection()){
                for (CargaTurnoCargas ctc : ct.getCargasCollection()){
                    totalCargadoXBodega[ctc.getNroBodega()] = totalCargadoXBodega[ctc.getNroBodega()].add(ctc.getCarga()); 
                }
            }
        }
        
        cargasPrevias = new CargaPrevia[8];
        for (CargaPrevia cp : embarque.getCargaPreviaCollection()){
            cargasPrevias[cp.getBodega().getNro()] = cp;
        }
        
        
    }
    
    public Long getEmbarqueId(){
        return embarque.getId();
    }
    
    public Long getEmbarqueCodigo(){
        return embarque.getCodigo();
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
        BigDecimal total = BigDecimal.ZERO;
        for(int i = 1; i < cargasPrevias.length; i++) 
            total = total.add(cargasPrevias[i].getCarga());
        return total;

    }
    
    public BigDecimal getTotalCargaActual(){
        BigDecimal total = BigDecimal.ZERO;
        for(int i = 1; i < totalCargadoXBodega.length; i++)
            total = total.add(totalCargadoXBodega[i]);
        return total;
        
    } 
    
    public BigDecimal getTotalCargaBuque(){
        return getTotalPuertoAnterior().add(getTotalCargaActual());
    }
}
