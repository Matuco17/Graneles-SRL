/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.facturacion.Factura;
import com.orco.graneles.domain.facturacion.FacturaCalculadora;
import com.orco.graneles.domain.salario.TipoJornal;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author orco
 */
public class Calculadora implements Serializable {
    
    
    
    private List<FilaCalculadora> filas;
    private List<TipoJornal> tiposJornales;
    private List<TipoJornalVO> totalXtipoJornal;
    private Factura factura;

    public List<FilaCalculadora> getFilas() {
        return filas;
    }

    public void setFilas(List<FilaCalculadora> filas) {
        this.filas = filas;
    }

    public List<TipoJornal> getTiposJornales() {
        return tiposJornales;
    }

    public void setTiposJornales(List<TipoJornal> tiposJornales) {
        this.tiposJornales = tiposJornales;
    }
    
    public BigDecimal getTotal(){
        BigDecimal total = BigDecimal.ZERO;
        
        if (filas != null){
            for (FilaCalculadora fila : filas){
                total = total.add(fila.getValorTotal());
            }
        }
        
        return total;
    }
    
    public void setTotal(BigDecimal total){
        
    }
    
    public List<TipoJornalVO> getTotalXTipoJornal(){
        return this.totalXtipoJornal;
    }

    public void setTotalXtipoJornal(List<TipoJornalVO> totalXtipoJornal) {
        this.totalXtipoJornal = totalXtipoJornal;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }
    
    public BigDecimal getTotalLeyesSociales(){
        if (this.factura != null){
            return getTotal().multiply(this.factura.getPorcentajeAdministracion()).divide(new BigDecimal(100.00));
        } else {
            return BigDecimal.ZERO;
        }
    }
    
    public BigDecimal getTotalGeneral(){
        return getTotal().add(getTotalLeyesSociales());
    }
    
    public BigDecimal getPorcentajeAdministracion(){
        if (factura != null){
            return factura.getPorcentajeAdministracion();
        } else {
            return BigDecimal.ZERO;
        }
    }
    
    public void setPorcentajeAdministracion(BigDecimal porcentajeAdministracion){
        if (factura != null){
            factura.setPorcentajeAdministracion(porcentajeAdministracion);
        }
    }
}
