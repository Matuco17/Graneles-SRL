/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.salario;

import com.orco.graneles.domain.carga.TurnoEmbarque;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "tipo_jornal")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TipoJornal.findAll", query = "SELECT t FROM TipoJornal t"),
    @NamedQuery(name = "TipoJornal.findById", query = "SELECT t FROM TipoJornal t WHERE t.id = :id"),
    @NamedQuery(name = "TipoJornal.findByDescripcion", query = "SELECT t FROM TipoJornal t WHERE t.descripcion = :descripcion"),
    @NamedQuery(name = "TipoJornal.findByPorcExtraBasico", query = "SELECT t FROM TipoJornal t WHERE t.porcExtraBasico = :porcExtraBasico"),
    @NamedQuery(name = "TipoJornal.findByPorcExtraBruto", query = "SELECT t FROM TipoJornal t WHERE t.porcExtraBruto = :porcExtraBruto")})
public class TipoJornal implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Size(max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "porc_extra_basico")
    private BigDecimal porcExtraBasico;
    @Column(name = "porc_extra_bruto")
    private BigDecimal porcExtraBruto;
    
    @JoinColumn(name = "concepto_recibo", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ConceptoRecibo conceptoRecibo;
    
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipo")
    private Collection<TurnoEmbarque> turnoEmbarqueCollection;

    public TipoJornal() {
    }

    public TipoJornal(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPorcExtraBasico() {
        return porcExtraBasico;
    }

    public void setPorcExtraBasico(BigDecimal porcExtraBasico) {
        this.porcExtraBasico = porcExtraBasico;
    }

    public BigDecimal getPorcExtraBruto() {
        return porcExtraBruto;
    }

    public void setPorcExtraBruto(BigDecimal porcExtraBruto) {
        this.porcExtraBruto = porcExtraBruto;
    }

    public Collection<TurnoEmbarque> getTurnoEmbarqueCollection() {
        return turnoEmbarqueCollection;
    }

    public void setTurnoEmbarqueCollection(Collection<TurnoEmbarque> turnoEmbarqueCollection) {
        this.turnoEmbarqueCollection = turnoEmbarqueCollection;
    }

    public ConceptoRecibo getConceptoRecibo() {
        return conceptoRecibo;
    }

    public void setConceptoRecibo(ConceptoRecibo conceptoRecibo) {
        this.conceptoRecibo = conceptoRecibo;
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
        if (!(object instanceof TipoJornal)) {
            return false;
        }
        TipoJornal other = (TipoJornal) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getDescripcion();
    }
    
}
