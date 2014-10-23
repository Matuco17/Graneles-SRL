/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.facturacion.Factura;
import com.orco.graneles.domain.facturacion.MovimientoCtaCte;
import java.math.BigDecimal;
import java.util.Collections;
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
        if (movCtaCte.getFacturaCollection() != null && movCtaCte.getFacturaCollection().size() > 0){
            StringBuilder sb = new StringBuilder();
            for (Factura f : movCtaCte.getFacturaCollection()) {
                sb.append(", ").append(f.getComprobante());
            }
            return sb.substring(2);
        } else {
            return "Sin Factura";
        }
    }
    
    public Date getFechaFactura(){
        if (movCtaCte.getFacturaCollection() != null && movCtaCte.getFacturaCollection().size() > 0){
            return movCtaCte.getFacturaCollection().iterator().next().getFecha();
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
    
    public BigDecimal getCredito(){
        return movCtaCte.getCredito();
    }
    
    public BigDecimal getDebito(){
        return movCtaCte.getDebito();
    }
    
    public BigDecimal getValor(){
        return movCtaCte.getValor();
    }
    
    public BigDecimal getSaldo() {
        return movCtaCte.getSaldo();
    }
    
}
