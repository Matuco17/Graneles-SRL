/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.carga.TurnoEmbarqueObservaciones;

/**
 *
 * @author orco
 */
public class TurnoObservacionVO {

    private TurnoEmbarqueObservaciones turnoObservacion;
    
    public TurnoObservacionVO(TurnoEmbarqueObservaciones turnoObservacion){
        this.turnoObservacion = turnoObservacion;
    }
    
    public String getCargador(){
        if (turnoObservacion.getCargador() != null){
            return turnoObservacion.getCargador().toString();
        }
        return "";
    }
    
    public Integer getDesde(){
        return turnoObservacion.getDesde();
    }
    
    public Integer getHasta(){
        return turnoObservacion.getHasta();
    }
    
    public Integer getBodega(){
        if (turnoObservacion.getBodega() != null){
            return turnoObservacion.getBodega().getNro();
        }
        return null;
    }
    
    public String getObservacion(){
        return turnoObservacion.getObservacion();
    }
}
