/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.personal.Personal;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author orco
 */
public class CargaRegVO {

    
    private String cuilPersonal;
    private BigDecimal sueldoBruto;
    private BigDecimal jubilacion;
    private BigDecimal obraSocial;
    private BigDecimal sindicato;
    private BigDecimal noRemunerativo;
    private BigDecimal dtoJudicial;
    private BigDecimal fondoComp;    
    private Integer tipoPagoFeri; //Tipo de la tabla Pago Feri
    private BigDecimal cantidadBruto; //Cantidad del Bruto;

    public BigDecimal getCantidadBruto() {
        return cantidadBruto;
    }

    public void setCantidadBruto(BigDecimal cantidadBruto) {
        this.cantidadBruto = cantidadBruto;
    }
    
    public CargaRegVO(){
        sueldoBruto = BigDecimal.ZERO;
        jubilacion = BigDecimal.ZERO;
        obraSocial = BigDecimal.ZERO;
        sindicato = BigDecimal.ZERO; 
        noRemunerativo = BigDecimal.ZERO;
        dtoJudicial = BigDecimal.ZERO;
        fondoComp = BigDecimal.ZERO;
        tipoPagoFeri = null;
        cantidadBruto = BigDecimal.ZERO;
    }
    
    
    
    public String getCuilPersonal() {
        return cuilPersonal;
    }

    public void setCuilPersonal(String cuilPersonal) {
        this.cuilPersonal = cuilPersonal;
    }

    public BigDecimal getDtoJudicial() {
        return dtoJudicial;
    }

    public void setDtoJudicial(BigDecimal dtoJudicial) {
        this.dtoJudicial = dtoJudicial;
    }

    public BigDecimal getFondoComp() {
        return fondoComp;
    }

    public void setFondoComp(BigDecimal fondoComp) {
        this.fondoComp = fondoComp;
    }

    public BigDecimal getJubilacion() {
        return jubilacion;
    }

    public void setJubilacion(BigDecimal jubilacion) {
        this.jubilacion = jubilacion;
    }

    public BigDecimal getNoRemunerativo() {
        return noRemunerativo;
    }

    public void setNoRemunerativo(BigDecimal noRemunerativo) {
        this.noRemunerativo = noRemunerativo;
    }

    public BigDecimal getObraSocial() {
        return obraSocial;
    }

    public void setObraSocial(BigDecimal obraSocial) {
        this.obraSocial = obraSocial;
    }

    public BigDecimal getSindicato() {
        return sindicato;
    }

    public void setSindicato(BigDecimal sindicato) {
        this.sindicato = sindicato;
    }

    public BigDecimal getSueldoBruto() {
        return sueldoBruto;
    }

    public void setSueldoBruto(BigDecimal sueldoBruto) {
        this.sueldoBruto = sueldoBruto;
    }
        
    public Integer getTipoPagoFeri() {
        return tipoPagoFeri;
    }

    public void setTipoPagoFeri(Integer tipoPagoFeri) {
        this.tipoPagoFeri = tipoPagoFeri;
    }


    
}
