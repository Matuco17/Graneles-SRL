/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.orco.graneles.vo;

import com.orco.graneles.domain.facturacion.Factura;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author groupon
 */
public class ComprobanteVO implements Comparable<ComprobanteVO> {
    
    Factura factura;

    public ComprobanteVO(Factura factura) {
        this.factura = factura;
    }
    
    public Date getFecha(){
        return this.factura.getFecha();
    }
    
    public String getTipo(){
        return "Factura A";
    }
    
    public String getNroComprobante(){
        return this.factura.getComprobante();
    }
    
    public String getClienteNombre(){
        return this.factura.getExportador().getNombre();
    }
    
    public String getClienteCUIT(){
        return this.factura.getExportador().getCuit();
    }
    
    public BigDecimal getSubtotal(){
        return this.factura.getTotalFactura();
    }
    
    public BigDecimal getIva(){
        return this.factura.getTotalIVA();
    }
    
    public BigDecimal getPercepciones(){
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getTotal(){
        return this.factura.getTotalConIVA();
    }

    @Override
    public int compareTo(ComprobanteVO o) {
        if (this.getFecha().equals(o.getFecha())) {
            if (this.getTipo().equalsIgnoreCase(o.getTipo())) {
                return this.getNroComprobante().compareToIgnoreCase(o.getNroComprobante());
            } else {
                return this.getTipo().compareToIgnoreCase(o.getTipo());
            }
        } else {
            return this.getFecha().compareTo(o.getFecha());
        }
    }
    
    
}


