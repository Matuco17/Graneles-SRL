/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.carga.TurnoEmbarque;
import com.orco.graneles.domain.facturacion.FacturaCalculadora;
import com.orco.graneles.domain.facturacion.TurnoFacturado;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author orco
 */
public class CalculadoraTurno implements Comparable<CalculadoraTurno> {
    
    private TurnoFacturado turno;
    private List<FacturaCalculadora> fcs;

    public CalculadoraTurno(TurnoFacturado turno) {
        this.turno = turno;
    }
    
    public TurnoFacturado getTurno() {
        return turno;
    }


    public List<FacturaCalculadora> getFcs() {
        return fcs;
    }

    public void setFcs(List<FacturaCalculadora> fcs) {
        this.fcs = fcs;
    }
    
    public BigDecimal getTotalTurno(){
        BigDecimal total = BigDecimal.ZERO;
        if (this.getFcs() != null){
            for (FacturaCalculadora fc : this.getFcs()){
                total = total.add(fc.getValorTotal());
            }
        }
        return total;
    }

    @Override
    public int compareTo(CalculadoraTurno o) {
        return this.getTurno().getPlanilla().compareTo(o.getTurno().getPlanilla());
    }
    
    
    
}
