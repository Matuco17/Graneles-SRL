/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author orco
 */
public class TrabajadorTurnoEmbarqueVO {

    private TrabajadoresTurnoEmbarque tte;
    
    private BigDecimal jornalBasico;
    private BigDecimal insalubre;
    private BigDecimal peligrosa;
    private BigDecimal peligrosa2;
    private BigDecimal productiva;
    private BigDecimal valorBruto;
    private BigDecimal decreto;
    private BigDecimal valorTurno; //Neto
    
    public TrabajadorTurnoEmbarqueVO(TrabajadoresTurnoEmbarque tte, BigDecimal valorTurno) {
        this.tte = tte;
        this.valorTurno = valorTurno;
        jornalBasico = BigDecimal.ZERO;
        insalubre = BigDecimal.ZERO;
        peligrosa = BigDecimal.ZERO;
        peligrosa2 = BigDecimal.ZERO;
        productiva = BigDecimal.ZERO;
        valorBruto = BigDecimal.ZERO;
        decreto = BigDecimal.ZERO;
        valorTurno = BigDecimal.ZERO;
        
    }

    public BigDecimal getValorTurno() {
        return valorTurno;
    }

    public TrabajadoresTurnoEmbarque getTte() {
        return tte;
    }

    public void setTte(TrabajadoresTurnoEmbarque tte) {
        this.tte = tte;
    }

    public void setValorTurno(BigDecimal valorTurno) {
        this.valorTurno = valorTurno;
    }

    public BigDecimal getInsalubre() {
        return insalubre;
    }

    public void setInsalubre(BigDecimal insalubre) {
        this.insalubre = insalubre;
    }

    public BigDecimal getJornalBasico() {
        return jornalBasico;
    }

    public void setJornalBasico(BigDecimal jornalBasico) {
        this.jornalBasico = jornalBasico;
    }

    public BigDecimal getPeligrosa() {
        return peligrosa;
    }

    public void setPeligrosa(BigDecimal peligrosa) {
        this.peligrosa = peligrosa;
    }

    public BigDecimal getPeligrosa2() {
        return peligrosa2;
    }

    public void setPeligrosa2(BigDecimal peligrosa2) {
        this.peligrosa2 = peligrosa2;
    }

    
    public BigDecimal getProductiva() {
        return productiva;
    }

    public void setProductiva(BigDecimal productiva) {
        this.productiva = productiva;
    }

    public BigDecimal getValorBruto() {
        return valorBruto;
    }

    public void setValorBruto(BigDecimal valorBruto) {
        this.valorBruto = valorBruto;
    }
    
    public BigDecimal getDecreto() {
        return decreto;
    }

    public void setDecreto(BigDecimal decreto) {
        this.decreto = decreto;
    }
    
    //DATOS DEL TTE PUENTEADOS PARA REPORTES
    
    public String getBuqueNombre(){
        return tte.getPlanilla().getEmbarque().getBuque().getDescripcion();
    }
    
    public String getSitio(){
        return tte.getPlanilla().getEmbarque().getMuelle().getSitio();
    }
    
    public Date getFechaTurno(){
        return tte.getPlanilla().getFecha();
    }
    
    public String getTurnoDescripcion(){
        return tte.getPlanilla().getTurno().getDescripcion();
    }
    
    public Integer getHoras(){
        return tte.getHoras();
    }
    
    public String getCategoriaDescripcion(){
        return tte.getCategoria().getDescripcion();
    }
    
    public String getRegistro(){
        return tte.getPersonal().getRegistro();
    }
    
    public String getCuil(){
        return tte.getPersonal().getCuil();
    }
    
    public String getApellido(){
        return tte.getPersonal().getApellido();
    }
    
    public String getTareaDescripcion(){
        return tte.getTarea().getDescripcion();
    }
    
    public Integer getDesde(){
        return tte.getDesde();
    }
    
    public Integer getHasta(){
        return tte.getHasta();
    }
    
    public String getLugar(){
        return tte.getTarea().getLugar().getDescripcion();
    }
    
}
