/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.facturacion.FacturaCalculadora;
import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Tarea;
import com.orco.graneles.domain.salario.SalarioBasico;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author orco
 */
public class FilaCalculadora {

    private Tarea tarea;
    private List<FacturaCalculadora> fCalculadoras;

    public Tarea getTarea() {
        return tarea;
    }

    public FilaCalculadora(Tarea tarea) {
        this.tarea = tarea;
    }
    
    public List<FacturaCalculadora> getFacturasCalculadoras() {
        return fCalculadoras;
    }

    public void setFacturasCalculadoras(List<FacturaCalculadora> salarios) {
        this.fCalculadoras = salarios;
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
    
}
