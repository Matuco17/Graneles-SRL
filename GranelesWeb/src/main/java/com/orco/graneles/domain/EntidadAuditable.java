/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain;

import com.orco.graneles.domain.miscelaneos.Auditoria;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * Información comun a todas las entidades auditables
 * @author orco
 */
@MappedSuperclass
public abstract class EntidadAuditable {
    
    @JoinColumn(name = "auditoria", referencedColumnName = "id")
    @ManyToOne(optional= false, fetch= FetchType.LAZY, cascade= CascadeType.ALL)
    private Auditoria auditoria;

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }
    
    
    
}
