/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.salario;

import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Tarea;
import com.orco.graneles.domain.miscelaneos.FixedList;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "salario_basico")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SalarioBasico.findAll", query = "SELECT s FROM SalarioBasico s"),
    @NamedQuery(name = "SalarioBasico.findById", query = "SELECT s FROM SalarioBasico s WHERE s.id = :id"),
    @NamedQuery(name = "SalarioBasico.findByBasico", query = "SELECT s FROM SalarioBasico s WHERE s.basico = :basico"),
    @NamedQuery(name = "SalarioBasico.findByAdicional", query = "SELECT s FROM SalarioBasico s WHERE s.adicional = :adicional"),
    @NamedQuery(name = "SalarioBasico.findByDesde", query = "SELECT s FROM SalarioBasico s WHERE s.desde = :desde"),
    @NamedQuery(name = "SalarioBasico.findByHasta", query = "SELECT s FROM SalarioBasico s WHERE s.hasta = :hasta")})
    @NamedQuery(name = "SalarioBasico.findByPrincipalKey", 
                        query = "SELECT s FROM SalarioBasico s "
                                + "WHERE s.tipoJornal = :tipoJornal " 
                                + "AND s.categoria = :categoria "
                                + "AND s.tarea = :tarea")
public class SalarioBasico implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "basico")
    private BigDecimal basico;
    @Column(name = "adicional")
    private BigDecimal adicional;
    @Column(name = "desde")
    @Temporal(TemporalType.DATE)
    private Date desde;
    @Column(name = "hasta")
    @Temporal(TemporalType.DATE)
    private Date hasta;
    @JoinColumn(name = "tipo_jornal", referencedColumnName = "id")
    @ManyToOne
    private FixedList tipoJornal;
    @JoinColumn(name = "tarea", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Tarea tarea;
    @JoinColumn(name = "categoria", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Categoria categoria;

    public SalarioBasico() {
    }

    public SalarioBasico(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getBasico() {
        return basico;
    }

    public void setBasico(BigDecimal basico) {
        this.basico = basico;
    }

    public BigDecimal getAdicional() {
        return adicional;
    }

    public void setAdicional(BigDecimal adicional) {
        this.adicional = adicional;
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

    public FixedList getTipoJornal() {
        return tipoJornal;
    }

    public void setTipoJornal(FixedList tipoJornal) {
        this.tipoJornal = tipoJornal;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SalarioBasico)) {
            return false;
        }
        SalarioBasico other = (SalarioBasico) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.SalarioBasico[ id=" + id + " ]";
    }
    
}
