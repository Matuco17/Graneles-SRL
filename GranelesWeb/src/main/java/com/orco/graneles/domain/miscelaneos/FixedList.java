/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.miscelaneos;

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
@Table(name = "fixedList")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FixedList.findAll", query = "SELECT f FROM FixedList f"),
    @NamedQuery(name = "FixedList.findById", query = "SELECT f FROM FixedList f WHERE f.id = :id"),
    @NamedQuery(name = "FixedList.findByIdLista", query = "SELECT f FROM FixedList f WHERE f.lista.id = :idLista"),
    @NamedQuery(name = "FixedList.findByDescripcion", query = "SELECT f FROM FixedList f WHERE f.descripcion = :descripcion")})
public class FixedList implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JoinColumn(name = "idLista", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private List lista;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    
    /*    
    @OneToMany(mappedBy = "tipoJornal")
    private Collection<SalarioBasico> salarioBasicoCollection;
    @OneToMany(mappedBy = "estado")
    private Collection<Personal> personalCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipoRecibo")
    private Collection<Personal> personalCollection1;
    @OneToMany(mappedBy = "tipoDocumento")
    private Collection<Personal> personalCollection2;
    @OneToMany(mappedBy = "estadoCivil")
    private Collection<Personal> personalCollection3;
    @OneToMany(mappedBy = "tipoBuque")
    private Collection<Buque> buqueCollection;
    @OneToMany(mappedBy = "tipoTapas")
    private Collection<Buque> buqueCollection1;
    @OneToMany(mappedBy = "concepto")
    private Collection<Adelanto> adelantoCollection;
    @OneToMany(mappedBy = "tipo")
    private Collection<Feriado> feriadoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipoRecibo")
    private Collection<ConceptoRecibo> conceptoReciboCollection;
    @OneToMany(mappedBy = "tipoValor")
    private Collection<ConceptoRecibo> conceptoReciboCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipo")
    private Collection<ConceptoRecibo> conceptoReciboCollection2;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipoEmpresa")
    private Collection<Empresa> empresaCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fixedListid")
    private Collection<TurnoEmbarque> turnoEmbarqueCollection;
    */
    
    public FixedList() {
    }

    public FixedList(Integer id) {
        this.id = id;
    }

    public FixedList(Integer id, List lista, String descripcion) {
        this.id = id;
        this.lista = lista;
        this.descripcion = descripcion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List getLista() {
        return lista;
    }

    public void setLista(List lista) {
        this.lista = lista;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    /*
    @XmlTransient
    public Collection<SalarioBasico> getSalarioBasicoCollection() {
        return salarioBasicoCollection;
    }

    public void setSalarioBasicoCollection(Collection<SalarioBasico> salarioBasicoCollection) {
        this.salarioBasicoCollection = salarioBasicoCollection;
    }

    @XmlTransient
    public Collection<Personal> getPersonalCollection() {
        return personalCollection;
    }

    public void setPersonalCollection(Collection<Personal> personalCollection) {
        this.personalCollection = personalCollection;
    }

    @XmlTransient
    public Collection<Personal> getPersonalCollection1() {
        return personalCollection1;
    }

    public void setPersonalCollection1(Collection<Personal> personalCollection1) {
        this.personalCollection1 = personalCollection1;
    }

    @XmlTransient
    public Collection<Personal> getPersonalCollection2() {
        return personalCollection2;
    }

    public void setPersonalCollection2(Collection<Personal> personalCollection2) {
        this.personalCollection2 = personalCollection2;
    }

    @XmlTransient
    public Collection<Personal> getPersonalCollection3() {
        return personalCollection3;
    }

    public void setPersonalCollection3(Collection<Personal> personalCollection3) {
        this.personalCollection3 = personalCollection3;
    }

    @XmlTransient
    public Collection<Buque> getBuqueCollection() {
        return buqueCollection;
    }

    public void setBuqueCollection(Collection<Buque> buqueCollection) {
        this.buqueCollection = buqueCollection;
    }

    @XmlTransient
    public Collection<Buque> getBuqueCollection1() {
        return buqueCollection1;
    }

    public void setBuqueCollection1(Collection<Buque> buqueCollection1) {
        this.buqueCollection1 = buqueCollection1;
    }

    @XmlTransient
    public Collection<Adelanto> getAdelantoCollection() {
        return adelantoCollection;
    }

    public void setAdelantoCollection(Collection<Adelanto> adelantoCollection) {
        this.adelantoCollection = adelantoCollection;
    }

    @XmlTransient
    public Collection<Feriado> getFeriadoCollection() {
        return feriadoCollection;
    }

    public void setFeriadoCollection(Collection<Feriado> feriadoCollection) {
        this.feriadoCollection = feriadoCollection;
    }

    @XmlTransient
    public Collection<ConceptoRecibo> getConceptoReciboCollection() {
        return conceptoReciboCollection;
    }

    public void setConceptoReciboCollection(Collection<ConceptoRecibo> conceptoReciboCollection) {
        this.conceptoReciboCollection = conceptoReciboCollection;
    }

    @XmlTransient
    public Collection<ConceptoRecibo> getConceptoReciboCollection1() {
        return conceptoReciboCollection1;
    }

    public void setConceptoReciboCollection1(Collection<ConceptoRecibo> conceptoReciboCollection1) {
        this.conceptoReciboCollection1 = conceptoReciboCollection1;
    }

    @XmlTransient
    public Collection<ConceptoRecibo> getConceptoReciboCollection2() {
        return conceptoReciboCollection2;
    }

    public void setConceptoReciboCollection2(Collection<ConceptoRecibo> conceptoReciboCollection2) {
        this.conceptoReciboCollection2 = conceptoReciboCollection2;
    }

    @XmlTransient
    public Collection<Empresa> getEmpresaCollection() {
        return empresaCollection;
    }

    public void setEmpresaCollection(Collection<Empresa> empresaCollection) {
        this.empresaCollection = empresaCollection;
    }

    @XmlTransient
    public Collection<TurnoEmbarque> getTurnoEmbarqueCollection() {
        return turnoEmbarqueCollection;
    }

    public void setTurnoEmbarqueCollection(Collection<TurnoEmbarque> turnoEmbarqueCollection) {
        this.turnoEmbarqueCollection = turnoEmbarqueCollection;
    }
    */

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FixedList)) {
            return false;
        }
        FixedList other = (FixedList) object;
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
