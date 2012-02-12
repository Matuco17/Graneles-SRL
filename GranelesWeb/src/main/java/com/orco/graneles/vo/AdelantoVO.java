/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.salario.Adelanto;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author orco
 */
public class AdelantoVO {
    
    private Adelanto adelanto;
    
    public AdelantoVO(Adelanto adelanto){
        this.adelanto = adelanto;
    }
    
    public Long getId(){
        return adelanto.getId();
    }
    
    public String getApellidoYNombre(){
        return adelanto.getPersonal().getApellido();
    }
    
    public Date getFecha(){
        return adelanto.getFecha();
    }
    
    public BigDecimal getValor(){
        return adelanto.getValor();
    }
    
    public String getValorEnLetras(){
        return NumberToStringConverter.decimalACastellano(adelanto.getValor(), 2, "pesos con ", "ctvos.");
    }
}
