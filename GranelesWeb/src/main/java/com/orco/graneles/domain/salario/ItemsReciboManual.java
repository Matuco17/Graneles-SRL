/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.salario;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "items_recibo_manual", catalog = "graneles", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ItemsReciboManual.findAll", query = "SELECT i FROM ItemsReciboManual i"),
    @NamedQuery(name = "ItemsReciboManual.findById", query = "SELECT i FROM ItemsReciboManual i WHERE i.id = :id"),
    @NamedQuery(name = "ItemsReciboManual.findByCantidad", query = "SELECT i FROM ItemsReciboManual i WHERE i.cantidad = :cantidad"),
    @NamedQuery(name = "ItemsReciboManual.findByValor", query = "SELECT i FROM ItemsReciboManual i WHERE i.valor = :valor")})
public class ItemsReciboManual implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(precision = 19, scale = 2)
    private BigDecimal cantidad;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;
    
    @JoinColumn(name = "concepto", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ConceptoRecibo conceptoRecibo;
    
    @JoinColumn(name = "recibo", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private ReciboManual recibo;

    public ItemsReciboManual() {
    }

    public ItemsReciboManual(Long id) {
        this.id = id;
    }

    public ItemsReciboManual(Long id, BigDecimal valor) {
        this.id = id;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public ReciboManual getRecibo() {
        return recibo;
    }

    public void setRecibo(ReciboManual recibo) {
        this.recibo = recibo;
    }

    public ConceptoRecibo getConceptoRecibo() {
        return conceptoRecibo;
    }

    public void setConceptoRecibo(ConceptoRecibo conceptoRecibo) {
        this.conceptoRecibo = conceptoRecibo;
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
        if (!(object instanceof ItemsReciboManual)) {
            return false;
        }
        ItemsReciboManual other = (ItemsReciboManual) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.salario.ItemsReciboManual[ id=" + id + " ]";
    }
    
}
