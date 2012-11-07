/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.jsf.facturacion;

import com.orco.graneles.domain.salario.TipoJornal;
import com.orco.graneles.vo.FilaCalculadora;
import java.math.BigDecimal;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author orco
 */

@ManagedBean(name = "calculadoraController")
@SessionScoped
public class CalculadoraController {
    
    private List<FilaCalculadora> filas;
    private BigDecimal totalFilas;
    private List<TipoJornal> tipoJornales;
    
    

    public List<FilaCalculadora> getFilas() {
        return filas;
    }

    public void setFilas(List<FilaCalculadora> filas) {
        this.filas = filas;
    }

    public BigDecimal getTotalFilas() {
        return totalFilas;
    }

    public void setTotalFilas(BigDecimal totalFilas) {
        this.totalFilas = totalFilas;
    }

    public List<TipoJornal> getTipoJornales() {
        return tipoJornales;
    }

    public void setTipoJornales(List<TipoJornal> tipoJornales) {
        this.tipoJornales = tipoJornales;
    }
    
    
    
}
