/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.carga.CargaTurnoCargas;
import com.orco.graneles.domain.carga.Embarque;
import com.orco.graneles.domain.carga.EmbarqueCargador;
import com.orco.graneles.domain.carga.Mercaderia;
import com.orco.graneles.domain.facturacion.Empresa;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 *
 * @author orco
 */
public class ResumenExportadorVO implements Comparable<ResumenExportadorVO> {
    
    private Integer nroBodega;
    private BigDecimal carga;
    private Mercaderia mercaderiaBodega;
    private EmbarqueCargador embarqueCargador;
    private Set<Mercaderia> mercaderiasCargadas;
    private BigDecimal totalCargas;
    
    public ResumenExportadorVO(Integer nroBodega, EmbarqueCargador embarqueCargador, Mercaderia mercaderiaBodega) {
        this.carga = BigDecimal.ZERO;
        this.embarqueCargador = embarqueCargador;
        this.mercaderiaBodega = mercaderiaBodega;
        this.nroBodega = nroBodega;
    }
    
    public String getPuerto(){
        return embarqueCargador.getEmbarque().getMuelle().getPuerto().getNombre().toUpperCase();
    }
    
    public String getBuque(){
        return embarqueCargador.getEmbarque().getBuque().getDescripcion().toUpperCase();
    }
    
    public String getMercaderiasLineal(){
        StringBuilder result = new StringBuilder();
        for (Mercaderia m : mercaderiasCargadas){
            result.append(", ").append(m.getDescripcion());
        }
        return result.toString().substring(2).toUpperCase();
    }
    
    public void setMercaderiasCargadas(Set<Mercaderia> mercaderias){
        this.mercaderiasCargadas = mercaderias;
    }
    
    public String getExportador(){
        return embarqueCargador.getCargador().getNombre().toUpperCase();
    }
    
    public String getDestino(){
        return (embarqueCargador.getDestino() != null) ? embarqueCargador.getDestino().toUpperCase() : "";
    }
    
    public Integer getNroBodega(){
        return nroBodega;
    }
    
    public BigDecimal getCarga(){
        return carga;
    }
    
    public void setCarga(BigDecimal carga){
        this.carga = carga;
    }
    
    public BigDecimal getTotalCarga(){
        return totalCargas;
    }
    
    public void setTotalCarga(BigDecimal totalCarga){
        this.totalCargas = totalCarga;
    }
    
    public String getMercaderiaBodega(){
        return mercaderiaBodega.getDescripcion();
    }

    public String getOrigenMercaderia(){
        return embarqueCargador.getEmbarque().getOrigenMercaderia();
    }
    
    @Override
    public int compareTo(ResumenExportadorVO o) {
        return this.getNroBodega().compareTo(o.getNroBodega());
    }
}   
