/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.carga.CargaTurnoCargas;
import com.orco.graneles.domain.carga.Embarque;
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
    private Embarque embarque;
    private Set<Mercaderia> mercaderiasCargadas;
    private BigDecimal totalCargas;
    private Empresa exportador;
    
    public ResumenExportadorVO(Integer nroBodega, Embarque embarque, Empresa exportador, Mercaderia mercaderiaBodega) {
        this.carga = BigDecimal.ZERO;
        this.embarque = embarque;
        this.mercaderiaBodega = mercaderiaBodega;
        this.exportador = exportador;
        this.nroBodega = nroBodega;
    }
    
    public String getPuerto(){
        return embarque.getMuelle().getPuerto().getNombre().toUpperCase();
    }
    
    public String getBuque(){
        return embarque.getBuque().getDescripcion().toUpperCase();
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
        return exportador.getNombre().toUpperCase();
    }
    
    public String getDestino(){
        return embarque.getDestino().toUpperCase();
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
        return "CARGIL";
    }
    
    @Override
    public int compareTo(ResumenExportadorVO o) {
        return this.getNroBodega().compareTo(o.getNroBodega());
    }
}   
