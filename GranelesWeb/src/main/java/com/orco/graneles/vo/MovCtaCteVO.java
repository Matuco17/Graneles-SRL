/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.facturacion.MovimientoCtaCte;
import com.orco.graneles.domain.miscelaneos.TipoValorMovimientoCtaCte;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author orco
 */
public class MovCtaCteVO {
    
    private MovimientoCtaCte movCtaCte;
    
    public MovCtaCteVO(MovimientoCtaCte movCtaCte){
        this.movCtaCte = movCtaCte;
    }
    
    public String getEmpresaNombre(){
        return movCtaCte.getEmpresa().getNombre();
    }
    
    public String getFacturaDescripcion(){
        if (movCtaCte.getFactura() != null){
            return movCtaCte.getFactura().getComprobante();
        } else {
            return "Sin Factura";
        }
    }
    
    public Date getFechaFactura(){
        if (movCtaCte.getFactura() != null){
            return movCtaCte.getFactura().getFecha();
        } else {
            return null;
        }
    }
    
    public Date getFecha(){
        return movCtaCte.getFecha();
    }
    
    public String getObservaciones(){
        return movCtaCte.getObservaciones();
    }
    
    public BigDecimal getCreditoToneladas(){
        if (movCtaCte.getTipoValor().equals(TipoValorMovimientoCtaCte.TONELADA)){
            return movCtaCte.getCredito();
        } else {
            return null;
        }
    }
    
    public BigDecimal getDebitoToneladas(){
        if (movCtaCte.getTipoValor().equals(TipoValorMovimientoCtaCte.TONELADA)){
            return movCtaCte.getDebito();
        } else {
            return null;
        }
    }
    
    public BigDecimal getCreditoDinero(){
        if (movCtaCte.getTipoValor().equals(TipoValorMovimientoCtaCte.DINERO)){
            return movCtaCte.getCredito();
        } else {
            return null;
        }
    }
    
    public BigDecimal getDebitoDinero(){
        if (movCtaCte.getTipoValor().equals(TipoValorMovimientoCtaCte.DINERO)){
            return movCtaCte.getDebito();
        } else {
            return null;
        }
    }
}
