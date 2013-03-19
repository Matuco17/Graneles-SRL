/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.facturacion;

import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.miscelaneos.FixedList;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
public class LineaFactura implements Serializable, Comparable<LineaFactura> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @JoinColumn(name = "factura", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Factura factura;
  
    @JoinColumn(name = "tipo_linea", referencedColumnName = "id")
    @ManyToOne()
    private FixedList tipoLinea;
  
    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;
    
    @Column(name = "importe")
    private BigDecimal importe;

    @Column(name = "nro_linea")
    private Integer nroLinea;
    
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

    public String getDescripcion() {
        if (tipoLinea != null){
            descripcion = tipoLinea.getDescripcion();
        }
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

    public FixedList getTipoLinea() {
        return tipoLinea;
    }

    public void setTipoLinea(FixedList tipoLinea) {
        this.tipoLinea = tipoLinea;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
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

    public Integer getNroLinea() {
        return nroLinea;
    }

    public void setNroLinea(Integer nroLinea) {
        this.nroLinea = nroLinea;
    }
    
    
    
    /**
     * DATOS DE LA FACTURA
     */
    
    
    public Date getFacturaFecha(){
        return factura.getFecha();
    }
    
    public String getFacturaNombreEmpresa(){
        return factura.getExportador().getNombre();
    }
    
    public String getFacturaDireccion(){
        return factura.getExportador().getDireccion();
    }
    
    public String getFacturaLocalidad(){
        return factura.getExportador().getCuidad();
    }
    
    public String getFacturaCuit(){
        return factura.getExportador().getCuit();
    }
    
    public String getFacturaCodigoPostal () {
        return factura.getExportador().getCodigoPostal();
    }
    
    public BigDecimal getFacturaSubtotal(){
        return factura.getTotalFactura();
    }
    
    public BigDecimal getFacturaIVA(){
        return factura.getTotalIVA();
    }
    
    public BigDecimal getFacturaTotal(){
        return factura.getTotalConIVA();
    }
    
    public String getNombreBuque(){
        return factura.getEmbarque().getBuque().getDescripcion();
    }

    @Override
    public int compareTo(LineaFactura o) {
        if (this.nroLinea != null && o.nroLinea != null){
            return this.nroLinea.compareTo(o.nroLinea);
        }
        return 0;
    }
    
    
}
