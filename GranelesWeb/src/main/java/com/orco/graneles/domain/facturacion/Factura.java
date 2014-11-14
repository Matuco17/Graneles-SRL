/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.facturacion;

import com.orco.graneles.domain.EntidadAuditable;
import com.orco.graneles.domain.carga.Embarque;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "factura")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Factura.findAll", query = "SELECT f FROM Factura f"),
    @NamedQuery(name = "Factura.findById", query = "SELECT f FROM Factura f WHERE f.id = :id"),
    @NamedQuery(name = "Factura.findByFecha", query = "SELECT f FROM Factura f WHERE f.fecha = :fecha"),
    @NamedQuery(name = "Factura.findByComprobante", query = "SELECT f FROM Factura f WHERE f.comprobante = :comprobante"),
    @NamedQuery(name = "Factura.findByPagada", query = "SELECT f FROM Factura f WHERE f.pagada = :pagada"),
    @NamedQuery(name = "Factura.findByExportadorYPagada", query = "SELECT f FROM Factura f WHERE f.exportador = :exportador AND f.pagada = :pagada"),
    @NamedQuery(name = "Factura.findByPorcentajeIva", query = "SELECT f FROM Factura f WHERE f.porcentajeIva = :porcentajeIva"),
    @NamedQuery(name = "Factura.findByMesAnio", query = "SELECT f from Factura f WHERE f.fecha BETWEEN :desde AND :hasta")
  })
public class Factura extends EntidadAuditable implements Serializable, Comparable<Factura> {


    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    
    @Size(min = 1, max = 13)
    @Column(name = "comprobante")
    private String comprobante;
    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "porcentaje_iva")
    private BigDecimal porcentajeIva;
        
    @JoinColumn(name = "exportador", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Empresa exportador;
    
    @JoinColumn(name = "embarque", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Embarque embarque;
    
    @Column(name = "porcentaje_administracion")
    private BigDecimal porcentajeAdministracion;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "factura", orphanRemoval = true)
    private Collection<LineaFactura> lineaFacturaCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "factura", orphanRemoval = true)
    private Collection<TurnoFacturado> turnosFacturadosCollection;

    
    @JoinTable(name = "movctacte_factura", joinColumns = {
        @JoinColumn(name = "factura", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "movimiento", referencedColumnName = "id")})
    @ManyToMany(cascade = CascadeType.ALL)
    private Collection<MovimientoCtaCte> movimientoCtaCtesCollection;
   
    @Column(name = "pagada")
    private Boolean pagada;
    
    
    public Factura() {
    }

    public Factura(Long id) {
        this.id = id;
    }

    public Factura(Long id, Date fecha, String comprobante) {
        this.id = id;
        this.fecha = fecha;
        this.comprobante = comprobante;
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

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public BigDecimal getPorcentajeIva() {
        return porcentajeIva;
    }

    public void setPorcentajeIva(BigDecimal porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    public Empresa getExportador() {
        return exportador;
    }

    public void setExportador(Empresa exportador) {
        this.exportador = exportador;
    }

    public Embarque getEmbarque() {
        return embarque;
    }

    public void setEmbarque(Embarque embarque) {
        this.embarque = embarque;
    }
    
    @XmlTransient
    public Collection<LineaFactura> getLineaFacturaCollection() {
        return lineaFacturaCollection;
    }

    public void setLineaFacturaCollection(Collection<LineaFactura> lineaFacturaCollection) {
        this.lineaFacturaCollection = lineaFacturaCollection;
    }

    @XmlTransient
    public Collection<TurnoFacturado> getTurnosFacturadosCollection() {
        return turnosFacturadosCollection;
    }

    public void setTurnosFacturadosCollection(Collection<TurnoFacturado> turnosFacturadosCollection) {
        this.turnosFacturadosCollection = turnosFacturadosCollection;
    }

    @XmlTransient
    public Collection<MovimientoCtaCte> getMovimientoCtaCtesCollection() {
        return movimientoCtaCtesCollection;
    }

    public void setMovimientoCtaCtesCollection(Collection<MovimientoCtaCte> movimientoCtaCtesCollection) {
        this.movimientoCtaCtesCollection = movimientoCtaCtesCollection;
    }

    
    
    public BigDecimal getPorcentajeAdministracion() {
        if (porcentajeAdministracion == null){
            porcentajeAdministracion = BigDecimal.ZERO;
        }
        return porcentajeAdministracion;
    }

    public void setPorcentajeAdministracion(BigDecimal porcentajeAdministracion) {
        this.porcentajeAdministracion = porcentajeAdministracion;
    }

    public Boolean isPagada() {
        return pagada;
    }

    public void setPagada(Boolean pagada) {
        this.pagada = pagada;
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
        if (!(object instanceof Factura)) {
            return false;
        }
        Factura other = (Factura) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return comprobante;
    }
    
    public BigDecimal getTotalFactura(){
        BigDecimal total = BigDecimal.ZERO;
        
        if (lineaFacturaCollection != null){
            for (LineaFactura lf : lineaFacturaCollection){
                if (lf != null && lf.getImporte() != null){
                    total = total.add(lf.getImporte());
                }
            }
        }
        return total;
    }
    
    public BigDecimal getTotalIVA(){
        return getTotalFactura().multiply(new BigDecimal(0.21));
    }
    
    public BigDecimal getTotalConIVA(){
        return getTotalFactura().multiply(new BigDecimal(1.21));
    }
    
    public BigDecimal getTotalXTurnos(){
        BigDecimal total = BigDecimal.ZERO;
        
        if (turnosFacturadosCollection != null){
            for (TurnoFacturado tf : turnosFacturadosCollection){
                total = total.add(tf.getValor());
            }
        }
        return total;
    }
    
    public BigDecimal getTotalDiferencia(){
        BigDecimal total = BigDecimal.ZERO;
        
        if (turnosFacturadosCollection != null){
            for (TurnoFacturado tf : turnosFacturadosCollection){
                total = total.add(tf.getDiferencia());
            }
        }
        
        return total;
    }
    
    public BigDecimal getTotalEmbarcado(){
        BigDecimal total = BigDecimal.ZERO;
        
        if (turnosFacturadosCollection != null){
            for (TurnoFacturado tf : turnosFacturadosCollection){
                total = total.add(tf.getCargaTurno().getTotalCargado());
            }
        }
        
        return total;        
    }
    
    public BigDecimal getTotalCosto(){
        BigDecimal total = BigDecimal.ZERO;
        
        if (turnosFacturadosCollection != null){
            for (TurnoFacturado tf : turnosFacturadosCollection){
                total = total.add(tf.getCosto());
            }
        }
        
        return total;        
    }
    
    public BigDecimal getTotalJornales(){
        BigDecimal total = BigDecimal.ZERO;
        
        if (turnosFacturadosCollection != null){
            for (TurnoFacturado tf : turnosFacturadosCollection){
                total = total.add(tf.getTotalBruto());
            }
        }
        
        return total;        
    }

    @Override
    public int compareTo(Factura o) {
        return this.fecha.compareTo(o.fecha);
    }

    
    
    
}
