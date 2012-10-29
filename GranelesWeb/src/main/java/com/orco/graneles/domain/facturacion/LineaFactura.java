/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.facturacion;

import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.miscelaneos.FixedList;
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
    @NamedQuery(name = "LineaFactura.findByDescripcion", query = "SELECT l FROM LineaFactura l WHERE l.descripcion = :descripcion")
  })
public class LineaFactura implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Size(max = 256)
    @Column(name = "descripcion")
    private String descripcion;
    
    @JoinColumn(name = "factura", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Factura factura;
  
    @JoinColumn(name = "tipo_linea", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private FixedList tipoLinea;
  
    @JoinColumn(name = "carga_turno", referencedColumnName = "id")
    @ManyToOne()
    private CargaTurno cargaTurno;
  
    @Column(name = "porc_administracion")
    private BigDecimal porcentajeAdministracion;
     
    @Column(name = "total_bruto")
    private BigDecimal totalBruto;
    
    @Column(name = "costo")
    private BigDecimal costo;
    
    @Column(name = "administracion")
    private BigDecimal administracion;
    
    @Column(name = "tarifa")
    private BigDecimal tarifa;
    
    @Column(name = "valor")
    private BigDecimal valor;
    

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

    public FixedList getTipoLinea() {
        return tipoLinea;
    }

    public void setTipoLinea(FixedList tipoLinea) {
        this.tipoLinea = tipoLinea;
    }

    public CargaTurno getCargaTurno() {
        return cargaTurno;
    }

    public void setCargaTurno(CargaTurno cargaTurno) {
        this.cargaTurno = cargaTurno;
    }

    public BigDecimal getPorcentajeAdministracion() {
        return porcentajeAdministracion;
    }

    public void setPorcentajeAdministracion(BigDecimal porcentajeAdministracion) {
        this.porcentajeAdministracion = porcentajeAdministracion;
    }

    public BigDecimal getTotalBruto() {
        return totalBruto;
    }

    public void setTotalBruto(BigDecimal totalBruto) {
        this.totalBruto = totalBruto;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public BigDecimal getAdministracion() {
        return administracion;
    }

    public void setAdministracion(BigDecimal administracion) {
        this.administracion = administracion;
    }

    public BigDecimal getTarifa() {
        return tarifa;
    }

    public void setTarifa(BigDecimal tarifa) {
        this.tarifa = tarifa;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }


    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
    
    public BigDecimal getTotalLinea(){
        return valor;
    }
}
