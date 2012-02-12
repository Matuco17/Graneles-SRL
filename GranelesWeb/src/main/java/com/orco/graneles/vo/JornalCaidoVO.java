/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.personal.JornalCaido;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author orco
 */
public class JornalCaidoVO {
    
    private JornalCaido jc;
    
    public JornalCaidoVO(JornalCaido jornalCaido){
        this.jc = jornalCaido;
    }
    
    public Long getId(){
        return jc.getId();
    }
    
    public String getApellidoYNombre(){
        return jc.getAccidentado().getPersonal().getApellido();
    }
    
    public Date getDesde(){
        return jc.getDesde();
    }
    
    public Date getHasta(){
        return jc.getHasta();
    }
    
    public Date getDiaPago(){
        return jc.getDiaPago();
    }
    
    public BigDecimal getValor(){
        return jc.getValor();
    }
    
    public String getValorEnLetras(){
        return NumberToStringConverter.decimalACastellano(jc.getValor(), 2, "pesos con ", "ctvos.");
    }
}
