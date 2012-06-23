/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.carga;

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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "bodega")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bodega.findAll", query = "SELECT b FROM Bodega b"),
    @NamedQuery(name = "Bodega.findById", query = "SELECT b FROM Bodega b WHERE b.id = :id"),
    @NamedQuery(name = "Bodega.findByNro", query = "SELECT b FROM Bodega b WHERE b.nro = :nro"),
    @NamedQuery(name = "Bodega.findByCapacidadPiesCubicos", query = "SELECT b FROM Bodega b WHERE b.capacidadPiesCubicos = :capacidadPiesCubicos")})
public class Bodega implements Serializable, Comparable<Bodega> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "nro")
    private Integer nro;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "capacidad_pies_cubicos")
    private BigDecimal capacidadPiesCubicos;
   
    transient private BigDecimal capacidadMetrosCubicos;
    
    @JoinColumn(name = "buque", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Buque buque;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bodega")
    private Collection<CargaPrevia> cargaPreviaCollection;

    public Bodega() {
    }

    public Bodega(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNro() {
        return nro;
    }

    public void setNro(Integer nro) {
        this.nro = nro;
    }

    public BigDecimal getCapacidadPiesCubicos() {
        return capacidadPiesCubicos;
    }

    public void setCapacidadPiesCubicos(BigDecimal capacidadPiesCubicos) {
        this.capacidadPiesCubicos = capacidadPiesCubicos;
    }

    public BigDecimal getCapacidadMetrosCubicos() {
        return capacidadMetrosCubicos;
    }

    public void setCapacidadMetrosCubicos(BigDecimal capacidadMetrosCubicos) {
        this.capacidadMetrosCubicos = capacidadMetrosCubicos;
    }
    
    public Buque getBuque() {
        return buque;
    }

    public void setBuque(Buque buque) {
        this.buque = buque;
    }

    @XmlTransient
    public Collection<CargaPrevia> getCargaPreviaCollection() {
        return cargaPreviaCollection;
    }

    public void setCargaPreviaCollection(Collection<CargaPrevia> cargaPreviaCollection) {
        this.cargaPreviaCollection = cargaPreviaCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Bodega)) {
            return false;
        }
        Bodega other = (Bodega) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.Bodega[ id=" + id + " ]";
    }

    @Override
    public int compareTo(Bodega o) {
        return this.getNro().compareTo(o.getNro());
    }
    
}
