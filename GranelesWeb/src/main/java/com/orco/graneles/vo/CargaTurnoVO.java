/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.carga.CargaTurnoCargas;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 *
 * @author orco
 */
public class CargaTurnoVO {
    
    private CargaTurno ct;
    
    private BigDecimal[] cargasXBodega;
    
    private BigDecimal acumulado;
    
    private ResumenCargaEmbarqueVO resumenCargaEmbarque;
    
    private List<TurnoObservacionVO> observaciones;
    
    public CargaTurnoVO(CargaTurno cargaTurno, ResumenCargaEmbarqueVO resumenCargaEmbarque){
        this.ct = cargaTurno;
        this.resumenCargaEmbarque = resumenCargaEmbarque;
        
        int cantBodegas = 9; //Cantidad de bodegas en el reporte
        cargasXBodega = new BigDecimal[cantBodegas + 1];
        for (int i = 0; i < cargasXBodega.length; i++)
            cargasXBodega[i] = BigDecimal.ZERO;
        
        for (CargaTurnoCargas ctc : ct.getCargasCollection()){
            cargasXBodega[ctc.getNroBodega()] = ctc.getCarga();
        }
        
        acumulado = BigDecimal.ZERO;
    }

    public Long getCoordinadorId(){
        return ct.getCargador().getId();
    }
    
    public String getCoodinadorNombre(){
        return ct.getCargador().getNombre();
    }
    
    public Date getTurnoFecha(){
        return ct.getTurnoEmbarque().getFecha();
    }
    
    public String getTurno(){
        return ct.getTurnoEmbarque().getTurno().getDescripcion();
    }
    
    public String getPuertoAnterior(){
        return ct.getTurnoEmbarque().getEmbarque().getPuertoAnterior();
    }
    
    //Valores de las bodegas
    public BigDecimal getCargaBodega1(){
        return cargasXBodega[1];
    }
    public BigDecimal getCargaBodega2(){
        return cargasXBodega[2];
    }
    public BigDecimal getCargaBodega3(){
        return cargasXBodega[3];
    }
    public BigDecimal getCargaBodega4(){
        return cargasXBodega[4];
    }
    public BigDecimal getCargaBodega5(){
        return cargasXBodega[5];
    }
    public BigDecimal getCargaBodega6(){
        return cargasXBodega[6];
    }
    public BigDecimal getCargaBodega7(){
        return cargasXBodega[7];
    }
    public BigDecimal getCargaBodega8(){
        return cargasXBodega[8];
    }
    public BigDecimal getCargaBodega9(){
        return cargasXBodega[9];
    }
    
    public BigDecimal getTotalCargaTurno(){
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 1; i < cargasXBodega.length; i++)
            total = total.add(cargasXBodega[i]);
        return total;
    }
    
    
    //Datos provistos por el resumen de carga
    //Cargas Turnos
    public BigDecimal getResumenCargadoBod1(){
        return resumenCargaEmbarque.getCargadoBod1();
    }
    public BigDecimal getResumenCargadoBod2(){
        return resumenCargaEmbarque.getCargadoBod2();
    }
    public BigDecimal getResumenCargadoBod3(){
        return resumenCargaEmbarque.getCargadoBod3();
    }
    public BigDecimal getResumenCargadoBod4(){
        return resumenCargaEmbarque.getCargadoBod4();
    }
    public BigDecimal getResumenCargadoBod5(){
        return resumenCargaEmbarque.getCargadoBod5();
    }
    public BigDecimal getResumenCargadoBod6(){
        return resumenCargaEmbarque.getCargadoBod6();
    }
    public BigDecimal getResumenCargadoBod7(){
        return resumenCargaEmbarque.getCargadoBod7();
    }
    public BigDecimal getResumenCargadoBod8(){
        return resumenCargaEmbarque.getCargadoBod8();
    }
    public BigDecimal getResumenCargadoBod9(){
        return resumenCargaEmbarque.getCargadoBod9();
    }
    
    //Carga Previa
    public BigDecimal getResumenCargaPreviaBod1(){
        return resumenCargaEmbarque.getCargaPreviaBod1();
    }
    public BigDecimal getResumenCargaPreviaBod2(){
        return resumenCargaEmbarque.getCargaPreviaBod2();
    }
    public BigDecimal getResumenCargaPreviaBod3(){
        return resumenCargaEmbarque.getCargaPreviaBod3();
    }
    public BigDecimal getResumenCargaPreviaBod4(){
        return resumenCargaEmbarque.getCargaPreviaBod4();
    }
    public BigDecimal getResumenCargaPreviaBod5(){
        return resumenCargaEmbarque.getCargaPreviaBod5();
    }
    public BigDecimal getResumenCargaPreviaBod6(){
        return resumenCargaEmbarque.getCargaPreviaBod6();
    }
    public BigDecimal getResumenCargaPreviaBod7(){
        return resumenCargaEmbarque.getCargaPreviaBod7();
    }
    public BigDecimal getResumenCargaPreviaBod8(){
        return resumenCargaEmbarque.getCargaPreviaBod8();
    }
    public BigDecimal getResumenCargaPreviaBod9(){
        return resumenCargaEmbarque.getCargaPreviaBod9();
    }
    
        //Carga Previa
    public BigDecimal getResumenCargaTotalBod1(){
        return resumenCargaEmbarque.getEnBuqueBod1();
    }
    public BigDecimal getResumenCargaTotalBod2(){
        return resumenCargaEmbarque.getEnBuqueBod2();
    }
    public BigDecimal getResumenCargaTotalBod3(){
        return resumenCargaEmbarque.getEnBuqueBod3();
    }
    public BigDecimal getResumenCargaTotalBod4(){
        return resumenCargaEmbarque.getEnBuqueBod4();
    }
    public BigDecimal getResumenCargaTotalBod5(){
        return resumenCargaEmbarque.getEnBuqueBod5();
    }
    public BigDecimal getResumenCargaTotalBod6(){
        return resumenCargaEmbarque.getEnBuqueBod6();
    }
    public BigDecimal getResumenCargaTotalBod7(){
        return resumenCargaEmbarque.getEnBuqueBod7();
    }
    public BigDecimal getResumenCargaTotalBod8(){
        return resumenCargaEmbarque.getEnBuqueBod8();
    }
    public BigDecimal getResumenCargaTotalBod9(){
        return resumenCargaEmbarque.getEnBuqueBod9();
    }
  
    //TOTALES
    public BigDecimal getResumenTotalPuertoAnterior(){
        return resumenCargaEmbarque.getTotalPuertoAnterior();
    }
    
    public BigDecimal getResumenTotalCargaActual(){
        return resumenCargaEmbarque.getTotalCargaActual();
    } 
    
    public BigDecimal getResumenTotalCargaBuque(){
        return resumenCargaEmbarque.getTotalCargaBuque();
    }
    
    
    //Datos Unicos del embarque
    public Long getEmbarqueCodigo(){
        return resumenCargaEmbarque.getEmbarqueCodigo();
    }
    
    public String getEmbarqueBuque(){
        return resumenCargaEmbarque.getNombreBuque();
    }
    
    public String getPuertoDestino(){
        return resumenCargaEmbarque.getPuertoDestino();
    }

    public BigDecimal getAcumulado() {
        return acumulado;
    }

    public void setAcumulado(BigDecimal acumulado) {
        this.acumulado = acumulado;
    }

    public List<TurnoObservacionVO> getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(List<TurnoObservacionVO> observaciones) {
        this.observaciones = observaciones;
    }
    
    
    
    
}
