/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.personal;

import com.orco.graneles.domain.salario.SalarioBasico;
import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
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
@Table(name = "categoria")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Categoria.findAll", query = "SELECT c FROM Categoria c"),
    @NamedQuery(name = "Categoria.findById", query = "SELECT c FROM Categoria c WHERE c.id = :id"),
    @NamedQuery(name = "Categoria.findByDescripcion", query = "SELECT c FROM Categoria c WHERE c.descripcion = :descripcion")})
public class Categoria implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Size(max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoria")
    private Collection<SalarioBasico> salarioBasicoCollection;
    @JoinColumn(name = "sindicato", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Sindicato sindicato;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoriaPrincipal")
    private Collection<Personal> personalCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoria")
    private Collection<TrabajadoresTurnoEmbarque> trabajadoresTurnoEmbarqueCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoria")
    private Collection<Accidentado> accidentadoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoria")
    private Collection<CategoriaSecundaria> categoriaSecundariaCollection;

    public Categoria() {
    }

    public Categoria(Integer id) {
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

    @XmlTransient
    public Collection<SalarioBasico> getSalarioBasicoCollection() {
        return salarioBasicoCollection;
    }

    public void setSalarioBasicoCollection(Collection<SalarioBasico> salarioBasicoCollection) {
        this.salarioBasicoCollection = salarioBasicoCollection;
    }

    public Sindicato getSindicato() {
        return sindicato;
    }

    public void setSindicato(Sindicato sindicato) {
        this.sindicato = sindicato;
    }

    @XmlTransient
    public Collection<Personal> getPersonalCollection() {
        return personalCollection;
    }

    public void setPersonalCollection(Collection<Personal> personalCollection) {
        this.personalCollection = personalCollection;
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

    @XmlTransient
    public Collection<CategoriaSecundaria> getCategoriaSecundariaCollection() {
        return categoriaSecundariaCollection;
    }

    public void setCategoriaSecundariaCollection(Collection<CategoriaSecundaria> categoriaSecundariaCollection) {
        this.categoriaSecundariaCollection = categoriaSecundariaCollection;
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
        if (!(object instanceof Categoria)) {
            return false;
        }
        Categoria other = (Categoria) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descripcion;
    }
    
}
