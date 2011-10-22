/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.salario;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "periodo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Periodo.findAll", query = "SELECT p FROM Periodo p"),
    @NamedQuery(name = "Periodo.findById", query = "SELECT p FROM Periodo p WHERE p.id = :id"),
    @NamedQuery(name = "Periodo.findByDescripcion", query = "SELECT p FROM Periodo p WHERE p.descripcion = :descripcion"),
    @NamedQuery(name = "Periodo.findByDesde", query = "SELECT p FROM Periodo p WHERE p.desde = :desde"),
    @NamedQuery(name = "Periodo.findByHasta", query = "SELECT p FROM Periodo p WHERE p.hasta = :hasta"),
    @NamedQuery(name = "Periodo.findByFolioLibro", query = "SELECT p FROM Periodo p WHERE p.folioLibro = :folioLibro"),
    @NamedQuery(name = "Periodo.findByNroPrimeraHoja", query = "SELECT p FROM Periodo p WHERE p.nroPrimeraHoja = :nroPrimeraHoja"),
    @NamedQuery(name = "Periodo.findByDesdeHasta", query = "SELECT p FROM Periodo p WHERE p.desde = :desde AND p.hasta = :hasta")})
public class Periodo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Size(max = 20)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "desde")
    @Temporal(TemporalType.DATE)
    private Date desde;
    @Column(name = "hasta")
    @Temporal(TemporalType.DATE)
    private Date hasta;
    @Size(max = 45)
    @Column(name = "folio_libro")
    private String folioLibro;
    @Column(name = "nro_primera_hoja")
    private BigInteger nroPrimeraHoja;
    
    @OneToMany(cascade = CascadeType.ALL, fetch= FetchType.LAZY)
    @JoinColumn(name = "periodo")
    private Collection<Sueldo> sueldoCollection;

    public Periodo() {
    }

    public Periodo(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public String getFolioLibro() {
        return folioLibro;
    }

    public void setFolioLibro(String folioLibro) {
        this.folioLibro = folioLibro;
    }

    public BigInteger getNroPrimeraHoja() {
        return nroPrimeraHoja;
    }

    public void setNroPrimeraHoja(BigInteger nroPrimeraHoja) {
        this.nroPrimeraHoja = nroPrimeraHoja;
    }

    @XmlTransient
    public Collection<Sueldo> getSueldoCollection() {
        return sueldoCollection;
    }

    public void setSueldoCollection(Collection<Sueldo> sueldoCollection) {
        this.sueldoCollection = sueldoCollection;
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
        if (!(object instanceof Periodo)) {
            return false;
        }
        Periodo other = (Periodo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.Periodo[ id=" + id + " ]";
    }
    
}
