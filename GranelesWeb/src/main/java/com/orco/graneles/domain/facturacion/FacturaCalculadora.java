/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.facturacion;

import com.orco.graneles.domain.personal.Tarea;
import com.orco.graneles.domain.salario.SalarioBasico;
import com.orco.graneles.domain.salario.TipoJornal;
import com.orco.graneles.vo.TipoJornalVO;
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
@Table(name = "factura_calculadora")
@XmlRootElement
@NamedQueries({
  })
public class FacturaCalculadora implements Serializable, Comparable<FacturaCalculadora> {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @JoinColumn(name = "turno_facturado", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private TurnoFacturado turnoFacturado;
    
    @Column(name = "cantidad")
    private BigDecimal cantidad;
    
    @JoinColumn(name = "tarea", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Tarea tarea;
    
    @JoinColumn(name = "salario_basico", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private SalarioBasico salarioBasico;
    
    @JoinColumn(name = "tipo_jornal", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private TipoJornal tipoJornal;
    
    transient private BigDecimal valorTurno;
    
    transient TipoJornalVO totalTipoJornal;
    
    public FacturaCalculadora() {
    }

    public FacturaCalculadora(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TurnoFacturado getTurnoFacturado() {
        return turnoFacturado;
    }

    public void setTurnoFacturado(TurnoFacturado turnoFacturado) {
        this.turnoFacturado = turnoFacturado;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }

    public SalarioBasico getSalarioBasico() {
        return salarioBasico;
    }

    public void setSalarioBasico(SalarioBasico salarioBasico) {
        this.salarioBasico = salarioBasico;
    }

    public TipoJornal getTipoJornal() {
        return tipoJornal;
    }

    public void setTipoJornal(TipoJornal tipoJornal) {
        this.tipoJornal = tipoJornal;
    }

    public BigDecimal getValorTurno() {
        return valorTurno;
    }

    public void setValorTurno(BigDecimal valorTurno) {
        this.valorTurno = valorTurno;
    }
    
    public BigDecimal getValorTotal (){
        if (this.valorTurno != null && this.cantidad != null){
            return this.valorTurno.multiply(this.cantidad);
        } else {
            return BigDecimal.ZERO;
        }
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
        if (!(object instanceof FacturaCalculadora)) {
            return false;
        }
        FacturaCalculadora other = (FacturaCalculadora) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.FacturaCalculadora[ id=" + id + " ]";
    }
    
   
    @Override
    public int compareTo(FacturaCalculadora o) {
        return 0;  //TODO: IMPLEMENT
    }

    public TipoJornalVO getTotalTipoJornal() {
        return totalTipoJornal;
    }

    public void setTotalTipoJornal(TipoJornalVO totalTipoJornal) {
        this.totalTipoJornal = totalTipoJornal;
    }
   
    
    
}

