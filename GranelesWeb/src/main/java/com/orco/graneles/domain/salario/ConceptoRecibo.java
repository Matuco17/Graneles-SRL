/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.salario;

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
@Table(name = "concepto_recibo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ConceptoRecibo.findAll", query = "SELECT c FROM ConceptoRecibo c"),
    @NamedQuery(name = "ConceptoRecibo.findById", query = "SELECT c FROM ConceptoRecibo c WHERE c.id = :id"),
    @NamedQuery(name = "ConceptoRecibo.findByConcepto", query = "SELECT c FROM ConceptoRecibo c WHERE c.concepto = :concepto"),
    @NamedQuery(name = "ConceptoRecibo.findByVersionActiva", query = "SELECT c FROM ConceptoRecibo c WHERE c.versionActiva = :versionActiva"),
    @NamedQuery(name = "ConceptoRecibo.findByVersion", query = "SELECT c FROM ConceptoRecibo c WHERE c.version = :version"),
    @NamedQuery(name = "ConceptoRecibo.findByOrden", query = "SELECT c FROM ConceptoRecibo c WHERE c.orden = :orden"),
    @NamedQuery(name = "ConceptoRecibo.findByValor", query = "SELECT c FROM ConceptoRecibo c WHERE c.valor = :valor"),
    @NamedQuery(name = "ConceptoRecibo.findByTipoRecibo", query = "SELECT c FROM ConceptoRecibo c WHERE c.tipoRecibo = :tipoRecibo AND c.versionActiva = :versionActiva"),
    @NamedQuery(name = "ConceptoRecibo.findByTipoReciboYTipoConcepto", query = "SELECT c FROM ConceptoRecibo c WHERE c.tipoRecibo = :tipoRecibo AND c.tipo = :tipo AND c.versionActiva = :versionActiva"),
    @NamedQuery(name = "ConceptoRecibo.findByTipoReciboYTipoValor", query = "SELECT c FROM ConceptoRecibo c WHERE c.tipoRecibo = :tipoRecibo AND c.tipoValor = :tipoValor AND c.versionActiva = :versionActiva"),
    @NamedQuery(name = "ConceptoRecibo.findByCalculado", query = "SELECT c FROM ConceptoRecibo c WHERE c.calculado = :calculado")})
public class ConceptoRecibo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Size(max = 45)
    @Column(name = "concepto")
    private String concepto;
    @Column(name = "version_activa")
    private Boolean versionActiva;
    @Column(name = "version")
    private Integer version;
    @Column(name = "orden")
    private Integer orden;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "calculado")
    private Boolean calculado;
    @JoinColumn(name = "tipo_recibo", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private FixedList tipoRecibo;
    @JoinColumn(name = "tipo_valor", referencedColumnName = "id")
    @ManyToOne
    private FixedList tipoValor;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private FixedList tipo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "conceptoRecibo")
    private Collection<ItemsSueldo> itemsSueldoCollection;

    public ConceptoRecibo() {
    }

    public ConceptoRecibo(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Boolean getVersionActiva() {
        return versionActiva;
    }

    public void setVersionActiva(Boolean versionActiva) {
        this.versionActiva = versionActiva;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getCalculado() {
        return calculado;
    }

    public void setCalculado(Boolean calculado) {
        this.calculado = calculado;
    }

    public FixedList getTipoRecibo() {
        return tipoRecibo;
    }

    public void setTipoRecibo(FixedList tipoRecibo) {
        this.tipoRecibo = tipoRecibo;
    }

    public FixedList getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(FixedList tipoValor) {
        this.tipoValor = tipoValor;
    }

    public FixedList getTipo() {
        return tipo;
    }

    public void setTipo(FixedList tipo) {
        this.tipo = tipo;
    }

    @XmlTransient
    public Collection<ItemsSueldo> getItemsSueldoCollection() {
        return itemsSueldoCollection;
    }

    public void setItemsSueldoCollection(Collection<ItemsSueldo> itemsSueldoCollection) {
        this.itemsSueldoCollection = itemsSueldoCollection;
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
        if (!(object instanceof ConceptoRecibo)) {
            return false;
        }
        ConceptoRecibo other = (ConceptoRecibo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.concepto;
    }
    
}
