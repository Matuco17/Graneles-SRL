/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author orco
 */
public class SueldoAccidentadoVO {
    
    private Date desde;
    private Date hasta;
    private BigDecimal brutoSinAdicionales;
    private BigDecimal brutoConAdicionales;

    public BigDecimal getBrutoConAdicionales() {
        return brutoConAdicionales;
    }

    public void setBrutoConAdicionales(BigDecimal brutoConAdicionales) {
        this.brutoConAdicionales = brutoConAdicionales;
    }

    public BigDecimal getBrutoSinAdicionales() {
        return brutoSinAdicionales;
    }

    public void setBrutoSinAdicionales(BigDecimal brutoSinAdicionales) {
        this.brutoSinAdicionales = brutoSinAdicionales;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }
    
    
    
}
