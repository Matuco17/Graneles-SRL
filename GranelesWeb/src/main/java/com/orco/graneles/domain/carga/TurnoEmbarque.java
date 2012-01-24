/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.carga;

import com.orco.graneles.domain.miscelaneos.FixedList;
import com.orco.graneles.domain.salario.TipoJornal;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "turno_embarque")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TurnoEmbarque.findAll", query = "SELECT t FROM TurnoEmbarque t"),
    @NamedQuery(name = "TurnoEmbarque.findById", query = "SELECT t FROM TurnoEmbarque t WHERE t.id = :id"),
    @NamedQuery(name = "TurnoEmbarque.findByFecha", query = "SELECT t FROM TurnoEmbarque t WHERE t.fecha = :fecha"),
    @NamedQuery(name = "TurnoEmbarque.findByTurno", query = "SELECT t FROM TurnoEmbarque t WHERE t.turno = :turno"),
    @NamedQuery(name = "TurnoEmbarque.findByTipo", query = "SELECT t FROM TurnoEmbarque t WHERE t.tipo = :tipo")})
public class TurnoEmbarque implements Serializable, Comparable<TurnoEmbarque> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
   
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "turno", orphanRemoval = true)
    private Collection<TurnoEmbarqueObservaciones> turnoEmbarqueObservacionesCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "turnoEmbarque", orphanRemoval = true)
    private Collection<CargaTurno> cargaTurnoCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "planilla", orphanRemoval = true)
    private Collection<TrabajadoresTurnoEmbarque> trabajadoresTurnoEmbarqueCollection;
    
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private TipoJornal tipo;
    
    @JoinColumn(name = "turno", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private FixedList turno;
    
    
    @JoinColumn(name = "embarque", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Embarque embarque;

    public TurnoEmbarque() {
    }

    public TurnoEmbarque(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FixedList getTurno() {
        return turno;
    }

    public void setTurno(FixedList turno) {
        this.turno = turno;
    }

    public TipoJornal getTipo() {
        return tipo;
    }

    public void setTipo(TipoJornal tipo) {
        this.tipo = tipo;
    }

    @XmlTransient
    public Collection<CargaTurno> getCargaTurnoCollection() {
        return cargaTurnoCollection;
    }

    public void setCargaTurnoCollection(Collection<CargaTurno> cargaTurnoCollection) {
        this.cargaTurnoCollection = cargaTurnoCollection;
    }

    @XmlTransient
    public Collection<TrabajadoresTurnoEmbarque> getTrabajadoresTurnoEmbarqueCollection() {
        return trabajadoresTurnoEmbarqueCollection;
    }

    public void setTrabajadoresTurnoEmbarqueCollection(Collection<TrabajadoresTurnoEmbarque> trabajadoresTurnoEmbarqueCollection) {
        this.trabajadoresTurnoEmbarqueCollection = trabajadoresTurnoEmbarqueCollection;
    }

    public Embarque getEmbarque() {
        return embarque;
    }

    public void setEmbarque(Embarque embarque) {
        this.embarque = embarque;
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
        if (!(object instanceof TurnoEmbarque)) {
            return false;
        }
        TurnoEmbarque other = (TurnoEmbarque) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.TurnoEmbarque[ id=" + id + " ]";
    }

    @Override
    public int compareTo(TurnoEmbarque o) {
        if (this.embarque.equals(o.embarque)){
            if (this.getFecha().equals(o.getFecha())){
                return this.getTurno().getId().compareTo(o.getTurno().getId());
            } else {
                return this.getFecha().compareTo(o.getFecha());
            }
        } else {
            return this.embarque.compareTo(o.embarque);
        }
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @XmlTransient
    public Collection<TurnoEmbarqueObservaciones> getTurnoEmbarqueObservacionesCollection() {
        return turnoEmbarqueObservacionesCollection;
    }

    public void setTurnoEmbarqueObservacionesCollection(Collection<TurnoEmbarqueObservaciones> turnoEmbarqueObservacionesCollection) {
        this.turnoEmbarqueObservacionesCollection = turnoEmbarqueObservacionesCollection;
    }
    
}
