/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.vo;

import java.math.BigDecimal;

/**
 *
 * @author orco
 */
public class TotalesCargaVO implements Comparable<TotalesCargaVO> {
    
    private String mercaderia;
    private BigDecimal carga;

    public TotalesCargaVO(String mercaderia, BigDecimal carga) {
        this.mercaderia = mercaderia;
        this.carga = carga;
    }

    public BigDecimal getCarga() {
        return carga;
    }

    public void setCarga(BigDecimal carga) {
        this.carga = carga;
    }

    public String getMercaderia() {
        return mercaderia;
    }

    public void setMercaderia(String mercaderia) {
        this.mercaderia = mercaderia;
    }

    @Override
    public int compareTo(TotalesCargaVO o) {
        return this.mercaderia.compareToIgnoreCase(o.mercaderia);
    }
    
    
    
    
}
