/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import java.math.BigDecimal;

/**
 *
 * @author orco
 */
public class TrabajadorTurnoEmbarqueVO {

    private TrabajadoresTurnoEmbarque tte;
    private BigDecimal valorTurno;

    public TrabajadorTurnoEmbarqueVO(TrabajadoresTurnoEmbarque tte, BigDecimal valorTurno) {
        this.tte = tte;
        this.valorTurno = valorTurno;
    }

    public BigDecimal getValorTurno() {
        return valorTurno;
    }

    public TrabajadoresTurnoEmbarque getTte() {
        return tte;
    }

    public void setTte(TrabajadoresTurnoEmbarque tte) {
        this.tte = tte;
    }

    public void setValorTurno(BigDecimal valorTurno) {
        this.valorTurno = valorTurno;
    }
    
    
    
    
}
