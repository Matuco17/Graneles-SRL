/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.personal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "obra_social")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ObraSocial.findAll", query = "SELECT o FROM ObraSocial o"),
    @NamedQuery(name = "ObraSocial.findById", query = "SELECT o FROM ObraSocial o WHERE o.id = :id"),
    @NamedQuery(name = "ObraSocial.findByDescripcion", query = "SELECT o FROM ObraSocial o WHERE o.descripcion = :descripcion"),
    @NamedQuery(name = "ObraSocial.findByAportes", query = "SELECT o FROM ObraSocial o WHERE o.aportes = :aportes"),
    @NamedQuery(name = "ObraSocial.findByContribucion", query = "SELECT o FROM ObraSocial o WHERE o.contribucion = :contribucion"),
    @NamedQuery(name = "ObraSocial.findByCodigoAfip", query = "SELECT o FROM ObraSocial o WHERE o.codigoAfip = :codigoAfip")})
public class ObraSocial implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Size(max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "aportes")
    private BigDecimal aportes;
    @Column(name = "contribucion")
    private BigDecimal contribucion;
    @Size(max = 16)
    @Column(name = "codigo_afip")
    private String codigoAfip;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "obraSocial")
    private Collection<Personal> personalCollection;

    public ObraSocial() {
    }

    public ObraSocial(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getAportes() {
        return aportes;
    }

    public void setAportes(BigDecimal aportes) {
        this.aportes = aportes;
    }

    public BigDecimal getContribucion() {
        return contribucion;
    }

    public void setContribucion(BigDecimal contribucion) {
        this.contribucion = contribucion;
    }

    public String getCodigoAfip() {
        return codigoAfip;
    }

    public void setCodigoAfip(String codigoAfip) {
        this.codigoAfip = codigoAfip;
    }

    @XmlTransient
    public Collection<Personal> getPersonalCollection() {
        return personalCollection;
    }

    public void setPersonalCollection(Collection<Personal> personalCollection) {
        this.personalCollection = personalCollection;
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
        if (!(object instanceof ObraSocial)) {
            return false;
        }
        ObraSocial other = (ObraSocial) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descripcion;
    }
    
}
