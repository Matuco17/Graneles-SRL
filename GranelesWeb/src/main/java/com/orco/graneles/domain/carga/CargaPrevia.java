/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.carga;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "carga_previa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CargaPrevia.findAll", query = "SELECT c FROM CargaPrevia c"),
    @NamedQuery(name = "CargaPrevia.findById", query = "SELECT c FROM CargaPrevia c WHERE c.id = :id"),
    @NamedQuery(name = "CargaPrevia.findByCarga", query = "SELECT c FROM CargaPrevia c WHERE c.carga = :carga")})
public class CargaPrevia implements Serializable, Comparable<CargaPrevia> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "carga")
    private BigDecimal carga;
    
    @JoinColumn(name = "mercaderia", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Mercaderia mercaderia;
    
    @JoinColumn(name = "bodega", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Bodega bodega;
    
    @JoinColumn(name = "embarque", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Embarque embarque;    
    
    @Column(name = "puerto_anterior")
    private String puertoAnterior;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cargaOriginalBodega", orphanRemoval = true)
    private Collection<CargaTurnoCargas> cargaTurnosCargasCollection;

    public CargaPrevia() {
    }

    public CargaPrevia(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCarga() {
        return carga;
    }

    public void setCarga(BigDecimal carga) {
        this.carga = carga;
    }

    public Mercaderia getMercaderia() {
        return mercaderia;
    }

    public void setMercaderia(Mercaderia mercaderia) {
        this.mercaderia = mercaderia;
    }

    public Bodega getBodega() {
        return bodega;
    }

    public void setBodega(Bodega bodega) {
        this.bodega = bodega;
    }

    public Embarque getEmbarque() {
        return embarque;
    }

    public void setEmbarque(Embarque embarque) {
        this.embarque = embarque;
    }

    public String getPuertoAnterior() {
        return puertoAnterior;
    }

    public void setPuertoAnterior(String puertoAnterior) {
        this.puertoAnterior = puertoAnterior;
    }
    
    
    
    /**
     * Calculo realizado por kilos en vez de toneladas
     */
    public BigDecimal getCapacidadBodegaMercaderia(){
        if ((mercaderia != null) && (mercaderia.getFactorEstiba() != null) && 
             (bodega != null) &&
             (bodega.getCapacidadPiesCubicos().abs().doubleValue() > 0.1)){
            double factorEstiva = mercaderia.getFactorEstiba().doubleValue();
            double capPCubicos = bodega.getCapacidadPiesCubicos().doubleValue();
            double resultado = (capPCubicos / factorEstiva); //el 1000 es x 1000 kilos = tonelada
            //redondeo el resultado y lo dejo asi ya que es en kilos y no importan los deciamles
            return new BigDecimal(Math.round(resultado)); 
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
        if (!(object instanceof CargaPrevia)) {
            return false;
        }
        CargaPrevia other = (CargaPrevia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.CargaPrevia[ id=" + id + " ]";
    }

    @Override
    public int compareTo(CargaPrevia o) {
        if (o != null && this.getBodega() != null && o.getBodega() != null){
            if (this.getBodega().getBuque().equals(o.getBodega().getBuque())){
                return this.getBodega().compareTo(o.getBodega());
            } else {
                return this.getBodega().getBuque().compareTo(o.getBodega().getBuque());
            }
        } else {
            return 0;
        }        
    }
    
}
