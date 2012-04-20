/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.salario;

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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "minimo_vital_movil")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MinimoVitalMovilHora.findAll", query = "SELECT m FROM MinimoVitalMovilHora m"),
    @NamedQuery(name = "MinimoVitalMovilHora.findById", query = "SELECT m FROM MinimoVitalMovilHora m WHERE m.id = :id"),
    @NamedQuery(name = "MinimoVitalMovilHora.findByDesde", query = "SELECT m FROM MinimoVitalMovilHora m WHERE m.desde = :desde"),
    @NamedQuery(name = "MinimoVitalMovilHora.findByHasta", query = "SELECT m FROM MinimoVitalMovilHora m WHERE m.hasta = :hasta"),
    @NamedQuery(name = "MinimoVitalMovilHora.findActivo", 
                        query = "SELECT m FROM MinimoVitalMovilHora m "
                                + "WHERE m.desde <= :fecha "
                                + "AND (m.hasta IS NULL OR m.hasta >= :fecha)")})
public class MinimoVitalMovilHora implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    
    @Column(name = "desde")
    @Temporal(TemporalType.DATE)
    private Date desde;
    
    @Column(name = "hasta")
    @Temporal(TemporalType.DATE)
    private Date hasta;
    
  
    public MinimoVitalMovilHora() {
    }

    public MinimoVitalMovilHora(Integer id) {
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

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
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
        if (!(object instanceof MinimoVitalMovilHora)) {
            return false;
        }
        MinimoVitalMovilHora other = (MinimoVitalMovilHora) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.granelem.domain.MinimoVitalMovilHora[ id=" + id + " ]";
    }
    
}
