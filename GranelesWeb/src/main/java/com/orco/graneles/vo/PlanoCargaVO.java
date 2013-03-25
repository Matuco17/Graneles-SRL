/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.vo;

import java.math.BigDecimal;

/**
 *
 * @author orco
 */
public class PlanoCargaVO implements Comparable<PlanoCargaVO> {
    
    private Integer bodega;
    private String mercaderia;
    private BigDecimal carga;
    private BigDecimal cargaPrevia;
    private String puertoPrevio;

    public PlanoCargaVO(Integer bodega) {
        this.bodega = bodega;
        carga = BigDecimal.ZERO;
        cargaPrevia = BigDecimal.ZERO;
    }

    
    
    public Integer getBodega() {
        return bodega;
    }

    public void setBodega(Integer bodega) {
        this.bodega = bodega;
    }

    public String getMercaderia() {
        return mercaderia;
    }

    public void setMercaderia(String mercaderia) {
        this.mercaderia = mercaderia;
    }

    public BigDecimal getCarga() {
        return carga;
    }

    public void setCarga(BigDecimal carga) {
        this.carga = carga;
    }

    public BigDecimal getCargaPrevia() {
        return cargaPrevia;
    }

    public void setCargaPrevia(BigDecimal cargaPrevia) {
        this.cargaPrevia = cargaPrevia;
    }

    public String getPuertoPrevio() {
        return puertoPrevio;
    }

    public void setPuertoPrevio(String puertoPrevio) {
        this.puertoPrevio = puertoPrevio;
    }
    
    @Override
    public int compareTo(PlanoCargaVO o) {
        return this.bodega.compareTo(o.bodega);
    }
}
