/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.carga.Embarque;
import java.util.Date;

/**
 *
 * @author ms.gonzalez
 */
public class EmbarqueVO {
    
    private Embarque embarque;

    public EmbarqueVO(Embarque embarque) {
        this.embarque = embarque;
    }
    
    public String getBuqueDescripcion(){
        return embarque.getBuque().getDescripcion();
    }
    
    public Date getFechaFinalizacion(){
        return embarque.getTmo();
    }
    
    public Long getId(){
        return embarque.getId();
    }
}
