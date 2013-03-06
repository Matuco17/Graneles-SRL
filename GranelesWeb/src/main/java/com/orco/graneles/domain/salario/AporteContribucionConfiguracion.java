/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.salario;

import com.orco.graneles.domain.personal.Personal;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "aporte_contribucion_config")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AporteContribucionConfiguracion.findAll", query = "SELECT a FROM AporteContribucionConfiguracion a")
    })
public class AporteContribucionConfiguracion implements Serializable, Comparable<AporteContribucionConfiguracion> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @JoinColumn(name = "tipo_valor_concepto", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @NotNull
    private FixedList tipoValor;
    
    @Column(name = "aporte")
    private BigDecimal aporte;
    
    @Column(name = "contribucion")
    private BigDecimal contribucion;

    @Column(name = "orden")
    private Integer orden;
    
    public AporteContribucionConfiguracion() {
    }

    public AporteContribucionConfiguracion(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FixedList getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(FixedList tipoValor) {
        this.tipoValor = tipoValor;
    }

    public BigDecimal getAporte() {
        return aporte;
    }

    public void setAporte(BigDecimal aporte) {
        this.aporte = aporte;
    }

    public BigDecimal getContribucion() {
        return contribucion;
    }

    public void setContribucion(BigDecimal contribucion) {
        this.contribucion = contribucion;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
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
        if (!(object instanceof AporteContribucionConfiguracion)) {
            return false;
        }
        AporteContribucionConfiguracion other = (AporteContribucionConfiguracion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.AporteContribucionConfiguracion[ id=" + id + " ]";
    }

    @Override
    public int compareTo(AporteContribucionConfiguracion o) {
        return this.orden.compareTo(o.orden);
    }
    
}
