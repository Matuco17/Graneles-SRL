/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.personal.Accidentado;
import com.orco.graneles.domain.personal.Personal;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.joda.time.DateTime;

/**
 *
 * @author orco
 */
public class NuevoAccidentadoVO implements Serializable {
    
    
    private TrabajadoresTurnoEmbarque ultimoTurnoTrabajado;
    private BigDecimal sueldoDiaSinAdicionales;
    private BigDecimal sueldoDiaConAdicionales;
    private Accidentado accidentado;

    public NuevoAccidentadoVO(TrabajadoresTurnoEmbarque ultimoTurnoTrabajado, Accidentado accidentado) {
        this.ultimoTurnoTrabajado = ultimoTurnoTrabajado;
        this.accidentado = accidentado;
    }
    
    public Date getFechaDesdePago(){
        DateTime desde = new DateTime(accidentado.getDesde());
        return desde.plusDays(1).toDate();
    }
    
    public Date getFechaHastaPago(){
        return accidentado.getHasta();
    }
    
    public Accidentado getAccidentado() {
        return accidentado;
    }

    public void setAccidentado(Accidentado accidentado) {
        this.accidentado = accidentado;
    }


    public BigDecimal getSueldoDiaConAdicionales() {
        return sueldoDiaConAdicionales;
    }

    public void setSueldoDiaConAdicionales(BigDecimal sueldoDiaConAdicionales) {
        this.sueldoDiaConAdicionales = sueldoDiaConAdicionales;
    }

    public BigDecimal getSueldoDiaSinAdicionales() {
        return sueldoDiaSinAdicionales;
    }

    public void setSueldoDiaSinAdicionales(BigDecimal sueldoDiaSinAdicionales) {
        this.sueldoDiaSinAdicionales = sueldoDiaSinAdicionales;
    }

    public TrabajadoresTurnoEmbarque getUltimoTurnoTrabajado() {
        return ultimoTurnoTrabajado;
    }

    public void setUltimoTurnoTrabajado(TrabajadoresTurnoEmbarque ultimoTurnoTrabajado) {
        this.ultimoTurnoTrabajado = ultimoTurnoTrabajado;
    }
    
    
}
