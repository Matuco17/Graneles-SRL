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
    private BigDecimal brutoLimpio;
    private String descripcionPeriodo;
    private BigDecimal sac;
    private BigDecimal vacaciones;
    private BigDecimal accidentado;
    
    public TrabajadorConBrutoVO(Sueldo sueldo){
        this.descripcionPeriodo = sueldo.getPeriodo().getDescripcion();
        this.personal = sueldo.getPersonal();
        
        brutoLimpio = BigDecimal.ZERO;
        sac = BigDecimal.ZERO;
        vacaciones = BigDecimal.ZERO;
        accidentado = BigDecimal.ZERO;
        
        for (ItemsSueldo is : sueldo.getItemsSueldoCollection()){
            if (is.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.REMUNERATIVO)){
                switch (is.getConceptoRecibo().getTipoValor().getId()){
                    case TipoValorConcepto.SAC :
                        sac = sac.add(is.getValorCalculado());
                        break;
                    case TipoValorConcepto.VACACIONES :
                        vacaciones = vacaciones.add(is.getValorCalculado());
                        break;
                    case TipoValorConcepto.PAGOS_ACCIDENTADO_ART :
                        accidentado = accidentado.add(is.getValorCalculado());
                        break;
                    default :
                        brutoLimpio = brutoLimpio.add(is.getValorCalculado());
                        break;
                }
            }            
        }        
    }
    
    public BigDecimal getBrutoLimpio() {
        return brutoLimpio;
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

    public BigDecimal getSac() {
        return sac;
    }

    public BigDecimal getVacaciones() {
        return vacaciones;
    }

    public BigDecimal getAccidentado() {
        return accidentado;
    }
        
    public BigDecimal getTotal(){
        return brutoLimpio.add(sac).add(vacaciones).add(accidentado);
    }
    
}
