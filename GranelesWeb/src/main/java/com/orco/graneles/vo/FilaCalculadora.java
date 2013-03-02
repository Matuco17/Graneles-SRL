/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.facturacion.FacturaCalculadora;
import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Tarea;
import com.orco.graneles.domain.salario.SalarioBasico;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author orco
 */
public class FilaCalculadora implements Serializable{

    private Tarea tarea;
    private List<FacturaCalculadora> facturasCalculadoras;

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }
    
    public FilaCalculadora(Tarea tarea) {
        this.tarea = tarea;
    }
    
    public List<FacturaCalculadora> getFacturasCalculadoras() {
        if (facturasCalculadoras == null){
            facturasCalculadoras = new ArrayList<FacturaCalculadora>();
        }
        return facturasCalculadoras;
    }

    public void setFacturasCalculadoras(List<FacturaCalculadora> salarios) {
        this.facturasCalculadoras = salarios;
    }

    public BigDecimal getValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        
        if (this.getFacturasCalculadoras() != null){
            for (FacturaCalculadora fCalculadora : this.getFacturasCalculadoras()){
                total = total.add(fCalculadora.getValorTotal());
            }
        }
                
        return total;
    }
    
    public void setValorTotal(BigDecimal valorTotal){
        
    }

    @Override
    public String toString() {
        return getTarea().getDescripcion();
    }
    
    
    
}
