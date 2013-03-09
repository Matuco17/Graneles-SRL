/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.carga;

import com.orco.graneles.domain.miscelaneos.FixedList;
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
@Table(name = "mercaderia")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Mercaderia.findAll", query = "SELECT m FROM Mercaderia m"),
    @NamedQuery(name = "Mercaderia.findById", query = "SELECT m FROM Mercaderia m WHERE m.id = :id"),
    @NamedQuery(name = "Mercaderia.findByDescripcion", query = "SELECT m FROM Mercaderia m WHERE m.descripcion = :descripcion")})
public class Mercaderia implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Size(max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "factor_estiba")
    private BigDecimal factorEstiba;
    
    @Size(max = 45)
    @Column(name = "descripcion_ingles")
    private String descripcionIngles;    
    
    @OneToMany(mappedBy = "mercaderia")
    private Collection<Embarque> embarqueCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mercaderia")
    private Collection<CargaPrevia> cargaPreviaCollection;

    @JoinColumn(name = "grupo_facturacion", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private FixedList grupoFacturacion;

    
    public Mercaderia() {
    }

    public Mercaderia(Integer id) {
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

    public BigDecimal getFactorEstiba() {
        return factorEstiba;
    }

    public void setFactorEstiba(BigDecimal factorEstiba) {
        this.factorEstiba = factorEstiba;
    }

    public String getDescripcionIngles() {
        if (descripcionIngles == null){
            return descripcion;
        }
        return descripcionIngles;
    }

    public void setDescripcionIngles(String descripcionIngles) {
        this.descripcionIngles = descripcionIngles;
    }

    public FixedList getGrupoFacturacion() {
        return grupoFacturacion;
    }

    public void setGrupoFacturacion(FixedList grupoFacturacion) {
        this.grupoFacturacion = grupoFacturacion;
    }
    
    @XmlTransient
    public Collection<Embarque> getEmbarqueCollection() {
        return embarqueCollection;
    }

    public void setEmbarqueCollection(Collection<Embarque> embarqueCollection) {
        this.embarqueCollection = embarqueCollection;
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Mercaderia)) {
            return false;
        }
        Mercaderia other = (Mercaderia) object;
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
