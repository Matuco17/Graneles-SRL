/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.salario.Periodo;
import java.math.BigDecimal;

/**
 *
 * @author orco
 */
public class AporteContribucionVO {
 
    private Integer nroSeccion;
    private String grupo;
    private String concepto;
    private BigDecimal aporte;
    private BigDecimal contribucion;
    private Periodo periodo;

    public AporteContribucionVO(Integer nroSeccion, String grupo, String concepto) {
        this.nroSeccion = nroSeccion;
        this.grupo = grupo;
        this.concepto = concepto;
        this.aporte = BigDecimal.ZERO;
        this.contribucion = BigDecimal.ZERO;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public BigDecimal getAporte() {
        return aporte;
    }

    public void setAporte(BigDecimal aporte) {
        this.aporte = aporte;
    }

    public BigDecimal getContribucion() {
        return contribucion;
    }

    public void setContribucion(BigDecimal contribucion) {
        this.contribucion = contribucion;
    }

    public Integer getNroSeccion() {
        return nroSeccion;
    }

    public void setNroSeccion(Integer nroSeccion) {
        this.nroSeccion = nroSeccion;
    }

    public String getDescripcionPeriodo(){
        if (periodo != null){
            return periodo.getDescripcion();
        }
        return "N/A";
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }
    
}
