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
import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author orco
 */
public class AccidentadoVO implements Serializable {
    
    
    private TrabajadoresTurnoEmbarque ultimoTurnoTrabajado;
    
    private List<SueldoAccidentadoVO> sueldos;
    
    private Accidentado accidentado;

    public AccidentadoVO(Accidentado accidentado) {
        this.ultimoTurnoTrabajado = accidentado.getTrabajoRealizado();
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

    public List<SueldoAccidentadoVO> getSueldos() {
        return sueldos;
    }

    public void setSueldos(List<SueldoAccidentadoVO> sueldos) {
        this.sueldos = sueldos;
    }

    
 
    public TrabajadoresTurnoEmbarque getUltimoTurnoTrabajado() {
        return ultimoTurnoTrabajado;
    }

    public void setUltimoTurnoTrabajado(TrabajadoresTurnoEmbarque ultimoTurnoTrabajado) {
        this.ultimoTurnoTrabajado = ultimoTurnoTrabajado;
    }
    
    
}
