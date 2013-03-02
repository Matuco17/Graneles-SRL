/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.vo;

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
        if (this.totalXtipoJornal == null){
            this.totalXtipoJornal = new ArrayList<TipoJornalVO>();
            
            for (TipoJornal tJornal : getTiposJornales()){
                BigDecimal totalTJornal = BigDecimal.ZERO;
                
                for (FilaCalculadora fila : getFilas()){
                    for (FacturaCalculadora fCalculadora : fila.getFacturasCalculadoras()){
                        if (fCalculadora.getTipoJornal().getId() == tJornal.getId()){
                            totalTJornal = totalTJornal.add(fCalculadora.getValorTotal());
                            break;
                        }
                    }                    
                }
                this.totalXtipoJornal.add(new TipoJornalVO(tJornal, totalTJornal));
            }
            
        } 
        return this.totalXtipoJornal;
    }

    public void setTotalXtipoJornal(List<TipoJornalVO> totalXtipoJornal) {
        this.totalXtipoJornal = totalXtipoJornal;
    }
    
}
