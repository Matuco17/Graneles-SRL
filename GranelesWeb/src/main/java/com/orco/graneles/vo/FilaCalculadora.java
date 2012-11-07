/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.salario.SalarioBasico;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author orco
 */
public class FilaCalculadora {

    private Categoria categoria;
    private List<SalarioCalculadora> salarios;
    

    


    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public List<SalarioCalculadora> getSalarios() {
        return salarios;
    }

    public void setSalarios(List<SalarioCalculadora> salarios) {
        this.salarios = salarios;
    }

    
}
