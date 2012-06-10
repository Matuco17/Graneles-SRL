/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.carga;

import com.orco.graneles.domain.facturacion.Empresa;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "buque")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Buque.findAll", query = "SELECT b FROM Buque b"),
    @NamedQuery(name = "Buque.findById", query = "SELECT b FROM Buque b WHERE b.id = :id"),
    @NamedQuery(name = "Buque.findByDescripcion", query = "SELECT b FROM Buque b WHERE b.descripcion = :descripcion"),
    @NamedQuery(name = "Buque.findByImo", query = "SELECT b FROM Buque b WHERE b.imo = :imo"),
    @NamedQuery(name = "Buque.findByEslora", query = "SELECT b FROM Buque b WHERE b.eslora = :eslora"),
    @NamedQuery(name = "Buque.findByMagna", query = "SELECT b FROM Buque b WHERE b.magna = :magna"),
    @NamedQuery(name = "Buque.findByPuntal", query = "SELECT b FROM Buque b WHERE b.puntal = :puntal"),
    @NamedQuery(name = "Buque.findByPoseeGrua", query = "SELECT b FROM Buque b WHERE b.poseeGrua = :poseeGrua")})
public class Buque implements Serializable, Comparable<Buque> {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    
    @Size(max = 45)
    @Column(name = "descripcion", length = 45)
    private String descripcion;
    
    @Column(name = "imo")
    private Integer imo;
    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "eslora", precision = 5, scale = 2)
    private BigDecimal eslora;
    
    @Column(name = "magna", precision = 5, scale = 2)
    private BigDecimal magna;
    
    @Column(name = "puntal", precision = 5, scale = 2)
    private BigDecimal puntal;
    
    @Column(name = "posee_grua")
    private Boolean poseeGrua;
    
    @JoinColumn(name = "tipo_buque", referencedColumnName = "id")
    @ManyToOne
    private FixedList tipoBuque;
    
    @JoinColumn(name = "tipo_tapas", referencedColumnName = "id")
    @ManyToOne
    private FixedList tipoTapas;
    
    @JoinTable(name = "buque_agencia", joinColumns = {
        @JoinColumn(name = "buque", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "empresa", referencedColumnName = "id")})
    @ManyToMany
    private Collection<Empresa> empresaCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "buque", orphanRemoval = true)
    private Collection<Bodega> bodegaCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "buque")
    private Collection<Embarque> embarqueCollection;
  
   
    public Buque() {
    }

    public Buque(Integer id) {
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

    public Integer getImo() {
        return imo;
    }

    public void setImo(Integer imo) {
        this.imo = imo;
    }

    public BigDecimal getEslora() {
        return eslora;
    }

    public void setEslora(BigDecimal eslora) {
        this.eslora = eslora;
    }

    public BigDecimal getMagna() {
        return magna;
    }

    public void setMagna(BigDecimal magna) {
        this.magna = magna;
    }

    public BigDecimal getPuntal() {
        return puntal;
    }

    public void setPuntal(BigDecimal puntal) {
        this.puntal = puntal;
    }

    public Boolean getPoseeGrua() {
        return poseeGrua;
    }

    public void setPoseeGrua(Boolean poseeGrua) {
        this.poseeGrua = poseeGrua;
    }

    @XmlTransient
    public Collection<Empresa> getEmpresaCollection() {
        return empresaCollection;
    }

    public void setEmpresaCollection(Collection<Empresa> empresaCollection) {
        this.empresaCollection = empresaCollection;
    }

    @XmlTransient
    public Collection<Bodega> getBodegaCollection() {
        return bodegaCollection;
    }

    public void setBodegaCollection(Collection<Bodega> bodegaCollection) {
        this.bodegaCollection = bodegaCollection;
    }

    @XmlTransient
    public Collection<Embarque> getEmbarqueCollection() {
        return embarqueCollection;
    }

    public void setEmbarqueCollection(Collection<Embarque> embarqueCollection) {
        this.embarqueCollection = embarqueCollection;
    }

    public FixedList getTipoBuque() {
        return tipoBuque;
    }

    public void setTipoBuque(FixedList tipoBuque) {
        this.tipoBuque = tipoBuque;
    }

    public FixedList getTipoTapas() {
        return tipoTapas;
    }

    public void setTipoTapas(FixedList tipoTapas) {
        this.tipoTapas = tipoTapas;
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
        if (!(object instanceof Buque)) {
            return false;
        }
        Buque other = (Buque) object;
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
    public int compareTo(Buque o) {
        return this.getDescripcion().compareToIgnoreCase(o.getDescripcion());
    }

   





}
