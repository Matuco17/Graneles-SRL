/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.facturacion;

import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.miscelaneos.FixedList;
import com.orco.graneles.domain.miscelaneos.TipoTurnoFactura;
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
@Table(name = "turno_facturado")
@XmlRootElement
@NamedQueries({
  })
public class TurnoFacturado implements Serializable, Comparable<TurnoFacturado> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @JoinColumn(name = "factura", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Factura factura;
  
    @JoinColumn(name = "tipo_turno_facturado", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private FixedList tipoTurnoFacturado;
  
    @JoinColumn(name = "carga_turno", referencedColumnName = "id")
    @ManyToOne(optional = true)
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
    
    @Column(name = "agregado_mixto")
    private BigDecimal agregadoMixto;

    public TurnoFacturado() {
    }

    public TurnoFacturado(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public FixedList getTipoTurnoFacturado() {
        return tipoTurnoFacturado;
    }

    public void setTipoTurnoFacturado(FixedList tipoTurnoFacturado) {
        this.tipoTurnoFacturado = tipoTurnoFacturado;
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

    public BigDecimal getAgregadoMixto() {
        return agregadoMixto;
    }

    public void setAgregadoMixto(BigDecimal agregadoMixto) {
        this.agregadoMixto = agregadoMixto;
    }

    public BigDecimal getDiferencia(){
        return this.costo.subtract((this.valor != null) 
                                    ? this.valor
                                    : BigDecimal.ZERO);
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
        if (!(object instanceof TurnoFacturado)) {
            return false;
        }
        TurnoFacturado other = (TurnoFacturado) object;
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

    @Override
    public int compareTo(TurnoFacturado o) {
        return this.getCargaTurno().getTurnoEmbarque().getNroPlanilla().compareTo(o.getCargaTurno().getTurnoEmbarque().getNroPlanilla());
    }
   
    /*
     * Valores extras de getters que no sirven más que nada para los reportes
     */
    
    
    public Integer getPlanilla(){
        return this.cargaTurno.getTurnoEmbarque().getNroPlanilla();
    }
    
    public Date getFecha(){
        return this.cargaTurno.getTurnoEmbarque().getFecha();
    }
    
    public String getTurno(){
        return this.cargaTurno.getTurnoEmbarque().getTurno().getDescripcion();
    }
    
    public String getTipoJornal(){
        return this.cargaTurno.getTurnoEmbarque().getTipo().getDescripcion();
    }
    
    public BigDecimal getTotalEmbarcado(){
        return this.getCargaTurno().getTurnoEmbarque().getTotalEmbarcado();
    }
    
    public String getTipoTurnoDescripcion(){
        return this.getTipoTurnoFacturado().getDescripcion();
    }
    
    public BigDecimal getAdicionalAdminMixto () {
        switch (this.getTipoTurnoFacturado().getId()){
            case TipoTurnoFactura.ADMINISTRACION :
                return this.getPorcentajeAdministracion();
            case TipoTurnoFactura.MIXTO :
                return this.getAgregadoMixto();
        }
        return null;
    }
}

