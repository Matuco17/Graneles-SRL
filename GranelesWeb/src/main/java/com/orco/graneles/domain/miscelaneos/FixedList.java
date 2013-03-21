/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.miscelaneos;

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
@Table(name = "fixedList")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FixedList.findAll", query = "SELECT f FROM FixedList f"),
    @NamedQuery(name = "FixedList.findById", query = "SELECT f FROM FixedList f WHERE f.id = :id"),
    @NamedQuery(name = "FixedList.findByIdLista", query = "SELECT f FROM FixedList f WHERE f.lista.id = :idLista"),
    @NamedQuery(name = "FixedList.findByDescripcion", query = "SELECT f FROM FixedList f WHERE f.descripcion = :descripcion")})
public class FixedList implements Serializable, Comparable<FixedList> {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @JoinColumn(name = "idLista", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private List lista;
    
    @Basic(optional = false)
    @NotNull
    @Size(max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "valor_defecto")
    private BigDecimal valorDefecto;
    
    public FixedList() {
    }

    public FixedList(Integer id) {
        this.id = id;
    }

    public FixedList(Integer id, List lista, String descripcion) {
        this.id = id;
        this.lista = lista;
        this.descripcion = descripcion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List getLista() {
        return lista;
    }

    public void setLista(List lista) {
        this.lista = lista;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getValorDefecto() {
        return valorDefecto;
    }

    public void setValorDefecto(BigDecimal valorDefecto) {
        this.valorDefecto = valorDefecto;
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
        if (!(object instanceof FixedList)) {
            return false;
        }
        FixedList other = (FixedList) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descripcion;
    }

    @Override
    public int compareTo(FixedList o) {
        if (this.lista.equals(o.lista)){
            return this.descripcion.compareToIgnoreCase(o.descripcion);
        } else {
            return this.lista.getId().compareTo(o.lista.getId());
        }
    }
    
}
