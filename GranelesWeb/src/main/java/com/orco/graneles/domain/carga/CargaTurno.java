/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.carga;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "carga_turno")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CargaTurno.findAll", query = "SELECT c FROM CargaTurno c"),
    @NamedQuery(name = "CargaTurno.findById", query = "SELECT c FROM CargaTurno c WHERE c.id = :id"),
    @NamedQuery(name = "CargaTurno.findByCarga", query = "SELECT c FROM CargaTurno c WHERE c.carga = :carga")})
public class CargaTurno implements Serializable, Comparable<CargaTurno> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "carga")
    private BigDecimal carga;
    @JoinColumn(name = "turno_embarque", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private TurnoEmbarque turnoEmbarque;
    @JoinColumn(name = "bodega", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Bodega bodega;

    public CargaTurno() {
    }

    public CargaTurno(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCarga() {
        return carga;
    }

    public void setCarga(BigDecimal carga) {
        this.carga = carga;
    }

    public TurnoEmbarque getTurnoEmbarque() {
        return turnoEmbarque;
    }

    public void setTurnoEmbarque(TurnoEmbarque turnoEmbarque) {
        this.turnoEmbarque = turnoEmbarque;
    }

    public Bodega getBodega() {
        return bodega;
    }

    public void setBodega(Bodega bodega) {
        this.bodega = bodega;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CargaTurno)) {
            return false;
        }
        CargaTurno other = (CargaTurno) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.CargaTurno[ id=" + id + " ]";
    }

    @Override
    public int compareTo(CargaTurno o) {
        if (this.getBodega() != null){
            if (this.getBodega().getBuque().equals(o.getBodega().getBuque())){
                return this.getBodega().compareTo(o.getBodega());
            } else {
                return this.getBodega().getBuque().compareTo(o.getBodega().getBuque());
            }
        } else {
            return 0;
        }      
    }
    
}
