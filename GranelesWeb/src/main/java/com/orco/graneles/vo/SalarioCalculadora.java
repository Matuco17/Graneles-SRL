/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.salario.SalarioBasico;
import java.math.BigDecimal;

/**
 *
 * @author orco
 */
public class SalarioCalculadora {
    
    BigDecimal cantidad;
    SalarioBasico salario;
    BigDecimal valorBruto;

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public SalarioBasico getSalario() {
        return salario;
    }

    public BigDecimal getValorBruto() {
        return valorBruto;
    }

    public void setValorBruto(BigDecimal valorBruto) {
        this.valorBruto = valorBruto;
    }

    public SalarioCalculadora(BigDecimal cantidad, SalarioBasico salario) {
        this.cantidad = cantidad;
        this.salario = salario;
    }
    
}
