/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.carga;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "carga_turno_cargas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CargaTurnoCargas.findAll", query = "SELECT c FROM CargaTurnoCargas c"),
    @NamedQuery(name = "CargaTurnoCargas.findById", query = "SELECT c FROM CargaTurnoCargas c WHERE c.id = :id"),
    @NamedQuery(name = "CargaTurnoCargas.findByCarga", query = "SELECT c FROM CargaTurnoCargas c WHERE c.carga = :carga")})
public class CargaTurnoCargas implements Serializable, Comparable<CargaTurnoCargas> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "carga")
    private BigDecimal carga;
    
    @JoinColumn(name = "carga_turno", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private CargaTurno cargaTurno;
    
    @JoinColumn(name = "carga_original", referencedColumnName = "id")
    @ManyToOne()
    private CargaPrevia cargaOriginalBodega;
    
    public CargaTurnoCargas() {
    }

    public CargaTurnoCargas(Long id) {
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

    public CargaTurno getCargaTurno() {
        return cargaTurno;
    }

    public void setCargaTurno(CargaTurno cargaTurno) {
        this.cargaTurno = cargaTurno;
    }

   public CargaPrevia getCargaOriginalBodega() {
        return cargaOriginalBodega;
    }

    public void setCargaOriginalBodega(CargaPrevia cargaOriginalBodega) {
        this.cargaOriginalBodega = cargaOriginalBodega;
    }
    
    public Integer getNroBodega(){
        if (this.getCargaOriginalBodega() != null){
            return this.getCargaOriginalBodega().getBodega().getNro();
        } else {
            return null;
        }
    }
    
    public Mercaderia getMercaderiaBodega(){
        if (this.getCargaOriginalBodega() != null){
            return this.getCargaOriginalBodega().getMercaderia();
        } else {
            return null;
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
        if (!(object instanceof CargaTurnoCargas)) {
            return false;
        }
        CargaTurnoCargas other = (CargaTurnoCargas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.CargaTurno[ id=" + id + " ]";
    }

    @Override
    public int compareTo(CargaTurnoCargas o) {
        if (this.getCargaOriginalBodega() != null){
            Bodega thisBodega = this.getCargaOriginalBodega().getBodega();
            Bodega otherBodega = o.getCargaOriginalBodega().getBodega();
            
            if (thisBodega.getBuque().equals(otherBodega.getBuque())){
                return thisBodega.compareTo(otherBodega);
            } else {
                return thisBodega.getBuque().compareTo(otherBodega.getBuque());
            }
        } else {
            return 0;
        }      
    }
    
}
