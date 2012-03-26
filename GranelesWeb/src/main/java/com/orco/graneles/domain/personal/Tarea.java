/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.personal;

import com.orco.graneles.domain.salario.SalarioBasico;
import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.miscelaneos.FixedList;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "tarea")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tarea.findAll", query = "SELECT t FROM Tarea t"),
    @NamedQuery(name = "Tarea.findById", query = "SELECT t FROM Tarea t WHERE t.id = :id"),
    @NamedQuery(name = "Tarea.findByDescripcion", query = "SELECT t FROM Tarea t WHERE t.descripcion = :descripcion"),
    @NamedQuery(name = "Tarea.findByLugar", query = "SELECT t FROM Tarea t WHERE t.lugar = :lugar"),
    @NamedQuery(name = "Tarea.findByInsalubre", query = "SELECT t FROM Tarea t WHERE t.insalubre = :insalubre"),
    @NamedQuery(name = "Tarea.findByPeligrosa", query = "SELECT t FROM Tarea t WHERE t.peligrosa = :peligrosa"),
    @NamedQuery(name = "Tarea.findByProductiva", query = "SELECT t FROM Tarea t WHERE t.productiva = :productiva"),
    @NamedQuery(name = "Tarea.findByPeligrosa2", query = "SELECT t FROM Tarea t WHERE t.peligrosa2 = :peligrosa2")})
public class Tarea implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Size(max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    
    @Size(max = 45)
    @Column(name = "abreviatura")
    private String abreviatura;
    
    @JoinColumn(name = "lugar", referencedColumnName = "id")
    @ManyToOne
    private FixedList lugar;
    
    @Column(name = "insalubre")
    private Boolean insalubre;
    
    @Column(name = "peligrosa")
    private Boolean peligrosa;
    
    @Column(name = "peligrosa2")
    private Boolean peligrosa2;
    
    @Column(name = "productiva")
    private Boolean productiva;
    
    @Column(name = "especialidad")
    private Boolean especidalidad;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tarea")
    private Collection<SalarioBasico> salarioBasicoCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tarea")
    private Collection<TrabajadoresTurnoEmbarque> trabajadoresTurnoEmbarqueCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tarea")
    private Collection<Accidentado> accidentadoCollection;

    public Tarea() {
    }

    public Tarea(Integer id) {
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

    public FixedList getLugar() {
        return lugar;
    }

    public void setLugar(FixedList lugar) {
        this.lugar = lugar;
    }

    public Boolean getInsalubre() {
        return insalubre;
    }

    public void setInsalubre(Boolean insalubre) {
        this.insalubre = insalubre;
    }

    public Boolean getPeligrosa() {
        return peligrosa;
    }

    public void setPeligrosa(Boolean peligrosa) {
        this.peligrosa = peligrosa;
    }

    public Boolean getProductiva() {
        return productiva;
    }

    public void setProductiva(Boolean productiva) {
        this.productiva = productiva;
    }

    public Boolean getPeligrosa2() {
        return peligrosa2;
    }

    public void setPeligrosa2(Boolean peligrosa2) {
        this.peligrosa2 = peligrosa2;
    }

    public Boolean getEspecidalidad() {
        return especidalidad;
    }

    public void setEspecidalidad(Boolean especidalidad) {
        this.especidalidad = especidalidad;
    }
    
    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }
    
    @XmlTransient
    public Collection<SalarioBasico> getSalarioBasicoCollection() {
        return salarioBasicoCollection;
    }

    public void setSalarioBasicoCollection(Collection<SalarioBasico> salarioBasicoCollection) {
        this.salarioBasicoCollection = salarioBasicoCollection;
    }

    @XmlTransient
    public Collection<TrabajadoresTurnoEmbarque> getTrabajadoresTurnoEmbarqueCollection() {
        return trabajadoresTurnoEmbarqueCollection;
    }

    public void setTrabajadoresTurnoEmbarqueCollection(Collection<TrabajadoresTurnoEmbarque> trabajadoresTurnoEmbarqueCollection) {
        this.trabajadoresTurnoEmbarqueCollection = trabajadoresTurnoEmbarqueCollection;
    }

    @XmlTransient
    public Collection<Accidentado> getAccidentadoCollection() {
        return accidentadoCollection;
    }

    public void setAccidentadoCollection(Collection<Accidentado> accidentadoCollection) {
        this.accidentadoCollection = accidentadoCollection;
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
        if (!(object instanceof Tarea)) {
            return false;
        }
        Tarea other = (Tarea) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getDescripcion() + "(" + this.getLugar() + ")";
    }
    
}
