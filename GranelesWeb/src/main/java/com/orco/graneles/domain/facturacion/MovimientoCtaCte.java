/*
 * To change thit template, choose Toolt | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.facturacion;

import com.orco.graneles.domain.salario.*;
import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Tarea;
import com.orco.graneles.domain.miscelaneos.FixedList;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "mov_cta_cte")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MovimientoCtaCte.findAll", query = "SELECT m FROM MovimientoCtaCte m"),
    @NamedQuery(name = "MovimientoCtaCte.findById", query = "SELECT m FROM MovimientoCtaCte m WHERE m.id = :id"),
    @NamedQuery(name = "MovimientoCtaCte.findByEmpresa", query = "SELECT m FROM MovimientoCtaCte m WHERE m.empresa = :empresa")
    })
public class MovimientoCtaCte implements Serializable, Comparable<MovimientoCtaCte> {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fieldt consider using these annotationt to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    
    @Column(name = "observaciones")
    private String observaciones;
    
    @JoinColumn(name = "tipo_movimiento", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private FixedList tipoMovimiento;

    @JoinColumn(name = "empresa", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Empresa empresa;

    @JoinColumn(name = "factura", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private Factura factura;

    
    

    public MovimientoCtaCte() {
    }

    public MovimientoCtaCte(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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
        // TODO: Warning - thit method won't work in the case the id fieldt are not set
        if (!(object instanceof MovimientoCtaCte)) {
            return false;
        }
        MovimientoCtaCte other = (MovimientoCtaCte) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.granelet.domain.MovimientoCtaCte[ id=" + id + " ]";
    }

    @Override
    public int compareTo(MovimientoCtaCte o) {
        return fecha.compareTo(o.fecha);
    }
    
}
