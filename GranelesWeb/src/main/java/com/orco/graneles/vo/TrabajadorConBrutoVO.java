/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.miscelaneos.TipoConceptoRecibo;
import com.orco.graneles.domain.miscelaneos.TipoValorConcepto;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.ItemsSueldo;
import com.orco.graneles.domain.salario.Sueldo;
import java.math.BigDecimal;

/**
 *
 * @author orco
 */
public class TrabajadorConBrutoVO {

    private Personal personal;
    private BigDecimal brutoSinSacYVacaciones;
    private String descripcionPeriodo;
    
    public TrabajadorConBrutoVO(Sueldo sueldo){
        this.descripcionPeriodo = sueldo.getPeriodo().getDescripcion();
        this.personal = sueldo.getPersonal();
        
        brutoSinSacYVacaciones = BigDecimal.ZERO;
        
        for (ItemsSueldo is : sueldo.getItemsSueldoCollection()){
            if (is.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.REMUNERATIVO)
                && !is.getConceptoRecibo().getTipoValor().getId().equals(TipoValorConcepto.SAC)
                && !is.getConceptoRecibo().getTipoValor().getId().equals(TipoValorConcepto.VACACIONES)){
             
                brutoSinSacYVacaciones = brutoSinSacYVacaciones.add(is.getValorCalculado());
            }
        }        
    }
    
    public BigDecimal getBrutoSinSacYVacaciones() {
        return brutoSinSacYVacaciones;
    }

    public String getCuil() {
        return personal.getCuil();
    }
    
    public String getApellidoYNombre(){
        return personal.getApellido();
    }
    
    public Integer getCategoriaId(){
        return personal.getCategoriaPrincipal().getId();
    }

    public String getCategoriaDescripcion(){
        return personal.getCategoriaPrincipal().getDescripcion();
    }

    public String getDescripcionPeriodo() {
        return descripcionPeriodo;
    }
        
}
