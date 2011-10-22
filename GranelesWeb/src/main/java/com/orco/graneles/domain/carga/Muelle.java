/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.carga;

import java.io.Serializable;
import java.util.Collection;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "muelle")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Muelle.findAll", query = "SELECT m FROM Muelle m"),
    @NamedQuery(name = "Muelle.findById", query = "SELECT m FROM Muelle m WHERE m.id = :id"),
    @NamedQuery(name = "Muelle.findBySitio", query = "SELECT m FROM Muelle m WHERE m.sitio = :sitio"),
    @NamedQuery(name = "Muelle.findByDescripcion", query = "SELECT m FROM Muelle m WHERE m.descripcion = :descripcion")})
public class Muelle implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Size(max = 5)
    @Column(name = "sitio")
    private String sitio;
    @Size(max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    @JoinColumn(name = "puerto", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Puerto puerto;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "muelle")
    private Collection<Embarque> embarqueCollection;

    public Muelle() {
    }

    public Muelle(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSitio() {
        return sitio;
    }

    public void setSitio(String sitio) {
        this.sitio = sitio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Puerto getPuerto() {
        return puerto;
    }

    public void setPuerto(Puerto puerto) {
        this.puerto = puerto;
    }

    @XmlTransient
    public Collection<Embarque> getEmbarqueCollection() {
        return embarqueCollection;
    }

    public void setEmbarqueCollection(Collection<Embarque> embarqueCollection) {
        this.embarqueCollection = embarqueCollection;
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
        if (!(object instanceof Muelle)) {
            return false;
        }
        Muelle other = (Muelle) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getDescripcion();
    }
    
}
