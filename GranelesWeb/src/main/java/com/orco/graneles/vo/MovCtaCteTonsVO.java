/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.facturacion.MovimientoCtaCte;
import com.orco.graneles.domain.facturacion.MovimientoCtaCteTons;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author orco
 */
public class MovCtaCteTonsVO {
    
    private MovimientoCtaCteTons movCtaCteTons;
    
    public MovCtaCteTonsVO(MovimientoCtaCteTons movCtaCteTons){
        this.movCtaCteTons = movCtaCteTons;
    }
    
    public String getEmpresaNombre(){
        return movCtaCteTons.getEmpresa().getNombre();
    }
    
    public String getEmbarqueDescripcion(){
        if (movCtaCteTons.getCargaTurno() != null){
            return movCtaCteTons.getCargaTurno().getTurnoEmbarque().getEmbarque().toString();
        } else {
            return "Sin Embarque";
        }
    }
    
    public Date getFechaTurnoEmbarque(){
        if (movCtaCteTons.getCargaTurno() != null){
            return movCtaCteTons.getCargaTurno().getTurnoEmbarque().getFecha();
        } else {
            return null;
        }
    }
    
    public Date getFecha(){
        return movCtaCteTons.getFecha();
    }
    
    public String getObservaciones(){
        return movCtaCteTons.getObservaciones();
    }
    
    public BigDecimal getCredito(){
        return movCtaCteTons.getCredito();
    }
    
    public BigDecimal getDebito(){
        return movCtaCteTons.getDebito();
    }
    
    public BigDecimal getValor(){
        return movCtaCteTons.getValor();
    }
    
    public BigDecimal getSaldo() {
        return movCtaCteTons.getSaldo();
    }
    
    public String getTipoTurno() {
        return movCtaCteTons.getTipoTurno().getDescripcion();
    }
}
