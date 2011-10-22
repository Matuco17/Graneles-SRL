/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.salario;

import com.orco.graneles.domain.personal.Personal;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "adelanto")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Adelanto.findAll", query = "SELECT a FROM Adelanto a"),
    @NamedQuery(name = "Adelanto.findById", query = "SELECT a FROM Adelanto a WHERE a.id = :id"),
    @NamedQuery(name = "Adelanto.findByFecha", query = "SELECT a FROM Adelanto a WHERE a.fecha = :fecha")})
public class Adelanto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @JoinColumn(name = "concepto", referencedColumnName = "id")
    @ManyToOne
    private FixedList concepto;
    @JoinColumn(name = "personal", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Personal personal;

    public Adelanto() {
    }

    public Adelanto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public FixedList getConcepto() {
        return concepto;
    }

    public void setConcepto(FixedList concepto) {
        this.concepto = concepto;
    }

    public Personal getPersonal() {
        return personal;
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
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
        if (!(object instanceof Adelanto)) {
            return false;
        }
        Adelanto other = (Adelanto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.Adelanto[ id=" + id + " ]";
    }
    
}
