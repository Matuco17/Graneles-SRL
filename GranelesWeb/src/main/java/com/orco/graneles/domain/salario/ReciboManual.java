/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.salario;

import com.orco.graneles.domain.EntidadAuditable;
import com.orco.graneles.domain.personal.Personal;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "recibo_manual", catalog = "graneles", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ReciboManual.findAll", query = "SELECT r FROM ReciboManual r"),
    @NamedQuery(name = "ReciboManual.findById", query = "SELECT r FROM ReciboManual r WHERE r.id = :id")})
public class ReciboManual extends EntidadAuditable implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @JoinColumn(name = "periodo", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Periodo periodo;
    
    @JoinColumn(name = "personal", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Personal personal;
       
    @OneToMany(cascade = CascadeType.ALL, fetch= FetchType.EAGER, mappedBy="recibo", orphanRemoval = true)
    private Collection<ItemsReciboManual> itemsReciboManualCollection;

    public ReciboManual() {
    }

    public ReciboManual(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public Personal getPersonal() {
        return personal;
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
    }
    
    @XmlTransient
    public Collection<ItemsReciboManual> getItemsReciboManualCollection() {
        return itemsReciboManualCollection;
    }

    public void setItemsReciboManualCollection(Collection<ItemsReciboManual> itemsReciboManualCollection) {
        this.itemsReciboManualCollection = itemsReciboManualCollection;
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
        if (!(object instanceof ReciboManual)) {
            return false;
        }
        ReciboManual other = (ReciboManual) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.salario.ReciboManual[ id=" + id + " ]";
    }
    
}
