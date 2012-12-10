/*
 * To change thit template, choose Toolt | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.facturacion;

import com.orco.graneles.domain.salario.*;
import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Tarea;
import com.orco.graneles.domain.miscelaneos.FixedList;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "tarifa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tarifa.findAll", query = "SELECT t FROM Tarifa t"),
    @NamedQuery(name = "Tarifa.findById", query = "SELECT t FROM Tarifa t WHERE t.id = :id"),
    @NamedQuery(name = "Tarifa.findByDesde", query = "SELECT t FROM Tarifa t WHERE t.desde = :desde"),
    @NamedQuery(name = "Tarifa.findByHasta", query = "SELECT t FROM Tarifa t WHERE t.hasta = :hasta"),
    @NamedQuery(name = "Tarifa.findByPrincipalKey", 
                        query = "SELECT t FROM Tarifa t "
                                + "WHERE t.tipoJornal = :tipoJornal "
                                + "AND t.grupoFacturacion = :grupoFacturacion"),
    @NamedQuery(name = "Tarifa.findActivos", 
                        query = "SELECT t FROM Tarifa t "
                                + "WHERE t.desde <= :fecha "
                                + "AND (t.hasta IS NULL OR t.hasta >= :fecha)"),
    @NamedQuery(name = "Tarifa.findActivo", 
                        query = "SELECT t FROM Tarifa t "
                                + "WHERE t.tipoJornal = :tipoJornal "
                                + "AND t.grupoFacturacion = :grupoFacturacion "
                                + "AND t.desde <= :fecha "
                                + "AND (t.hasta IS NULL OR t.hasta >= :fecha)")})
public class Tarifa implements Serializable, Comparable<Tarifa> {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fieldt consider using these annotationt to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    
    @Column(name = "desde")
    @Temporal(TemporalType.DATE)
    private Date desde;
    
    @Column(name = "hasta")
    @Temporal(TemporalType.DATE)
    private Date hasta;
    
    @JoinColumn(name = "tipo_jornal", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private TipoJornal tipoJornal;

    @JoinColumn(name = "grupo_facturacion", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private FixedList grupoFacturacion;

    

    public Tarifa() {
    }

    public Tarifa(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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

    public TipoJornal getTipoJornal() {
        return tipoJornal;
    }

    public void setTipoJornal(TipoJornal tipoJornal) {
        this.tipoJornal = tipoJornal;
    }

    public FixedList getGrupoFacturacion() {
        return grupoFacturacion;
    }

    public void setGrupoFacturacion(FixedList grupoFacturacion) {
        this.grupoFacturacion = grupoFacturacion;
    }

    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - thit method won't work in the case the id fieldt are not set
        if (!(object instanceof Tarifa)) {
            return false;
        }
        Tarifa other = (Tarifa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.granelet.domain.Tarifa[ id=" + id + " ]";
    }

    @Override
    public int compareTo(Tarifa o) {
        return desde.compareTo(o.desde);
    }
    
}
