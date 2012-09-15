/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.personal.Personal;
import java.math.BigDecimal;

/**
 *
 * @author orco
 */
public class ProyeccionSacVacYAdelantosVO implements Comparable<ProyeccionSacVacYAdelantosVO> {
 
    private Personal personal;
    private BigDecimal proyeccionBruto;
    private BigDecimal proyeccionNeto;
    private BigDecimal totalAdelantos;
    private BigDecimal proyeccionNetoConAdelantos;
    private TrabajadoresTurnoEmbarque ultimoTTE;

    public ProyeccionSacVacYAdelantosVO(Personal personal) {
        this.personal = personal;
        proyeccionBruto = BigDecimal.ZERO;
        proyeccionNeto = BigDecimal.ZERO;
        totalAdelantos = BigDecimal.ZERO;
        proyeccionNetoConAdelantos = BigDecimal.ZERO;
    }

    public Personal getPersonal() {
        return personal;
    }

    /**
     * Devuelve la ultima categoria
     * @return 
     */
    public String getCategoria(){
        if (ultimoTTE != null){
            return ultimoTTE.getCategoria().getDescripcion();
        }
        return "";
    }
    
    /**
     * Devuelve la ultima tarea
     * @return 
     */
    public String getTarea(){
         if (ultimoTTE != null){
            return ultimoTTE.getTarea().getDescripcion();
        }
        return "";
    }
    
    public BigDecimal getProyeccionBruto() {
        return proyeccionBruto;
    }

    public void setProyeccionBruto(BigDecimal proyeccionBruto) {
        this.proyeccionBruto = proyeccionBruto;
    }

    public BigDecimal getProyeccionNeto() {
        return proyeccionNeto;
    }

    public void setProyeccionNeto(BigDecimal proyeccionNeto) {
        this.proyeccionNeto = proyeccionNeto;
    }

    public BigDecimal getTotalAdelantos() {
        return totalAdelantos;
    }

    public void setTotalAdelantos(BigDecimal totalAdelantos) {
        this.totalAdelantos = totalAdelantos;
    }

    public BigDecimal getProyeccionNetoConAdelantos() {
        return proyeccionNetoConAdelantos;
    }

    public void setProyeccionNetoConAdelantos(BigDecimal proyeccionNetoConAdelantos) {
        this.proyeccionNetoConAdelantos = proyeccionNetoConAdelantos;
    }

    public void setUltimoTTE(TrabajadoresTurnoEmbarque ultimoTTE) {
        this.ultimoTTE = ultimoTTE;
    }
    
    

    @Override
    public int compareTo(ProyeccionSacVacYAdelantosVO o) {
        return this.getPersonal().getCuil().compareToIgnoreCase(o.getPersonal().getCuil());
    }

    
}
