/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.personal;

import com.orco.graneles.domain.salario.Sueldo;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "accidentado")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Accidentado.findAll", query = "SELECT a FROM Accidentado a"),
    @NamedQuery(name = "Accidentado.findById", query = "SELECT a FROM Accidentado a WHERE a.id = :id"),
    @NamedQuery(name = "Accidentado.findByDesde", query = "SELECT a FROM Accidentado a WHERE a.desde = :desde"),
    @NamedQuery(name = "Accidentado.findByHasta", query = "SELECT a FROM Accidentado a WHERE a.hasta = :hasta"),
    @NamedQuery(name = "Accidentado.findByBruto", query = "SELECT a FROM Accidentado a WHERE a.bruto = :bruto"),
    @NamedQuery(name = "Accidentado.findSinLibroSueldo", query = "SELECT a FROM Accidentado a WHERE a.libroSueldo IS NULL"),
    @NamedQuery(name = "Accidentado.findByDescripcionCortaAccidente", query = "SELECT a FROM Accidentado a WHERE a.descripcionCortaAccidente = :descripcionCortaAccidente"),
    @NamedQuery(name = "Accidentado.findByPeriodo", 
                query = "SELECT a FROM Accidentado a "
                + "WHERE a.desde <= :hasta "
                + "AND (a.hasta IS NULL OR a.hasta >= :desde)"),
    @NamedQuery(name = "Accidentado.findByPeriodoYPersonal", 
                query = "SELECT a FROM Accidentado a "
                + "WHERE a.personal = :personal "
                + "AND a.desde <= :hasta "
                + "AND (a.hasta IS NULL OR a.hasta >= :desde)")})
public class Accidentado implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "desde")
    @Temporal(TemporalType.DATE)
    private Date desde;
    @Column(name = "hasta")
    @Temporal(TemporalType.DATE)
    private Date hasta;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "bruto")
    private BigDecimal bruto;
    @Size(max = 256)
    @Column(name = "descripcion_corta_accidente")
    private String descripcionCortaAccidente;
    @JoinColumn(name = "libro_sueldo", referencedColumnName = "id")
    @ManyToOne
    private Sueldo libroSueldo;
    @JoinColumn(name = "tarea", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Tarea tarea;
    @JoinColumn(name = "categoria", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Categoria categoria;
    @JoinColumn(name = "personal", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Personal personal;

    public Accidentado() {
    }

    public Accidentado(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getBruto() {
        return bruto;
    }

    public void setBruto(BigDecimal bruto) {
        this.bruto = bruto;
    }

    public String getDescripcionCortaAccidente() {
        return descripcionCortaAccidente;
    }

    public void setDescripcionCortaAccidente(String descripcionCortaAccidente) {
        this.descripcionCortaAccidente = descripcionCortaAccidente;
    }

    public Sueldo getLibroSueldo() {
        return libroSueldo;
    }

    public void setLibroSueldo(Sueldo libroSueldo) {
        this.libroSueldo = libroSueldo;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Personal getPersonal() {
        return personal;
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
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
        if (!(object instanceof Accidentado)) {
            return false;
        }
        Accidentado other = (Accidentado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.Accidentado[ id=" + id + " ]";
    }
    
}
