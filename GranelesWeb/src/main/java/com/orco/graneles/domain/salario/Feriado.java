/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.salario;

import com.orco.graneles.domain.miscelaneos.FixedList;
import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "feriado")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Feriado.findAll", query = "SELECT f FROM Feriado f"),
    @NamedQuery(name = "Feriado.findById", query = "SELECT f FROM Feriado f WHERE f.id = :id"),
    @NamedQuery(name = "Feriado.findByFecha", query = "SELECT f FROM Feriado f WHERE f.fecha = :fecha"),
    @NamedQuery(name = "Feriado.findByDescripcion", query = "SELECT f FROM Feriado f WHERE f.descripcion = :descripcion")})
public class Feriado implements Serializable, Comparable<Feriado> {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date fecha;
    
    @Size(max = 45)
    @Column(name = "descripcion")
    private String descripcion;
  

    public Feriado() {
    }

    public Feriado(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
        if (!(object instanceof Feriado)) {
            return false;
        }
        Feriado other = (Feriado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getFecha().toString();
    }

    @Override
    public int compareTo(Feriado o) { //Ordenamiento inverso para que pueda ver los ultimos feriados
        return o.getFecha().compareTo(this.getFecha());
    }
    
}
