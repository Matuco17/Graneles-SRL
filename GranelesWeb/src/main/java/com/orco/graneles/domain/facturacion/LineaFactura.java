/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.facturacion;

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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "linea_factura")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LineaFactura.findAll", query = "SELECT l FROM LineaFactura l"),
    @NamedQuery(name = "LineaFactura.findById", query = "SELECT l FROM LineaFactura l WHERE l.id = :id"),
    @NamedQuery(name = "LineaFactura.findByCantidad", query = "SELECT l FROM LineaFactura l WHERE l.cantidad = :cantidad"),
    @NamedQuery(name = "LineaFactura.findByDescripcion", query = "SELECT l FROM LineaFactura l WHERE l.descripcion = :descripcion"),
    @NamedQuery(name = "LineaFactura.findByPrecioUnitario", query = "SELECT l FROM LineaFactura l WHERE l.precioUnitario = :precioUnitario")})
public class LineaFactura implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "cantidad")
    private BigDecimal cantidad;
    @Size(max = 256)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;
    @JoinColumn(name = "factura", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Factura factura;

    public LineaFactura() {
    }

    public LineaFactura(Long id) {
        this.id = id;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
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
        if (!(object instanceof LineaFactura)) {
            return false;
        }
        LineaFactura other = (LineaFactura) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.LineaFactura[ id=" + id + " ]";
    }
    
}
