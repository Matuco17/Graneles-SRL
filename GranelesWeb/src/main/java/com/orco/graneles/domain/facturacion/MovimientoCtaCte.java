/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.facturacion;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "mov_cta_cte", catalog = "graneles", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MovimientoCtaCte.findAll", query = "SELECT m FROM MovimientoCtaCte m"),
    @NamedQuery(name = "MovimientoCtaCte.findById", query = "SELECT m FROM MovimientoCtaCte m WHERE m.id = :id"),
    @NamedQuery(name = "MovimientoCtaCte.findByFecha", query = "SELECT m FROM MovimientoCtaCte m WHERE m.fecha = :fecha"),
    @NamedQuery(name = "MovimientoCtaCte.findByValor", query = "SELECT m FROM MovimientoCtaCte m WHERE m.valor = :valor"),
    @NamedQuery(name = "MovimientoCtaCte.findByObservaciones", query = "SELECT m FROM MovimientoCtaCte m WHERE m.observaciones = :observaciones"),
    @NamedQuery(name = "MovimientoCtaCte.findByManual", query = "SELECT m FROM MovimientoCtaCte m WHERE m.manual = :manual"),
    @NamedQuery(name = "MovimientoCtaCte.findByEmpresa", query = "SELECT m FROM MovimientoCtaCte m WHERE m.empresa = :empresa"),
    @NamedQuery(name = "MovimientoCtaCte.findByEmpresaYFechas", query = "SELECT m FROM MovimientoCtaCte m "
        + "     WHERE m.tipoValor = :tipoValor"
        + "     AND (:empresa IS NULL OR m.empresa = :empresa)"
        + "     AND (:desde IS NULL OR m.fecha >= :desde)"
        + "     AND (:hasta IS NULL OR m.fecha <= :hasta)"),
    @NamedQuery(name = "MovimientoCtaCte.findByEmpresaYValor", query = "SELECT m FROM MovimientoCtaCte m WHERE m.empresa = :empresa AND m.tipoValor = :tipoValor")
})
public class MovimientoCtaCte implements Serializable, Comparable<MovimientoCtaCte> {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fecha;
    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "valor", nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;
    
    @Size(max = 256)
    @Column(name = "observaciones", length = 256)
    private String observaciones;
    
    @Column(name = "manual")
    private Boolean manual;
    
    @JoinColumn(name = "factura", referencedColumnName = "id")
    @ManyToOne
    private Factura factura;
    
    @JoinColumn(name = "tipo_movimiento", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private FixedList tipoMovimiento;
    
    @JoinColumn(name = "empresa", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Empresa empresa;

    @JoinColumn(name = "tipo_valor", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private FixedList tipoValor;
    
    transient BigDecimal saldo;
    
    
    public MovimientoCtaCte() {
    }

    public MovimientoCtaCte(Long id) {
        this.id = id;
    }

    public MovimientoCtaCte(Long id, Date fecha, BigDecimal valor) {
        this.id = id;
        this.fecha = fecha;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Boolean getManual() {
        return manual;
    }

    public void setManual(Boolean manual) {
        this.manual = manual;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public FixedList getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(FixedList tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public FixedList getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(FixedList tipoValor) {
        this.tipoValor = tipoValor;
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
        if (!(object instanceof MovimientoCtaCte)) {
            return false;
        }
        MovimientoCtaCte other = (MovimientoCtaCte) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
    
    

    @Override
    public String toString() {
        return "com.orco.graneles.domain.facturacion.MovimientoCtaCte[ id=" + id + " ]";
    }

    @Override
    public int compareTo(MovimientoCtaCte o) {
        return this.getFecha().compareTo(o.getFecha());
    }
    
    public BigDecimal getDebito(){
        if (this.getValor().doubleValue() >= 0){
            return this.getValor();
        } else {
            return null;
        }
    }
    
    public BigDecimal getCredito(){
        if (this.getValor().doubleValue() < 0){
            return  this.getValor().abs();
        } else {
            return null;
        }
    }
    
    
}
