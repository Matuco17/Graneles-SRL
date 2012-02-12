/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.domain.personal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "jornal_caido", catalog = "graneles", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "JornalCaido.findAll", query = "SELECT j FROM JornalCaido j"),
    @NamedQuery(name = "JornalCaido.findById", query = "SELECT j FROM JornalCaido j WHERE j.id = :id"),
    @NamedQuery(name = "JornalCaido.findByDesde", query = "SELECT j FROM JornalCaido j WHERE j.desde = :desde"),
    @NamedQuery(name = "JornalCaido.findByHasta", query = "SELECT j FROM JornalCaido j WHERE j.hasta = :hasta"),
    @NamedQuery(name = "JornalCaido.findByValor", query = "SELECT j FROM JornalCaido j WHERE j.valor = :valor")})
public class JornalCaido implements Serializable, Comparable<JornalCaido> {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "desde", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date desde;
    
    @Column(name = "hasta", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date hasta;
    
    @Column(name = "dia_pago", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date diaPago;
        
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor", nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;
    
    @JoinColumn(name = "accidentado", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Accidentado accidentado;
    
    transient String urlRecibo;

    public JornalCaido() {
    }

    public JornalCaido(Long id) {
        this.id = id;
    }

    public JornalCaido(Long id, Date desde, Date hasta, BigDecimal valor) {
        this.id = id;
        this.desde = desde;
        this.hasta = hasta;
        this.valor = valor;
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

    public Date getDiaPago() {
        return diaPago;
    }

    public void setDiaPago(Date diaPago) {
        this.diaPago = diaPago;
    }
    
    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Accidentado getAccidentado() {
        return accidentado;
    }

    public void setAccidentado(Accidentado accidentado) {
        this.accidentado = accidentado;
    }

    public String getUrlRecibo() {
        return urlRecibo;
    }

    public void setUrlRecibo(String urlRecibo) {
        this.urlRecibo = urlRecibo;
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
        if (!(object instanceof JornalCaido)) {
            return false;
        }
        JornalCaido other = (JornalCaido) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "JornalCaido[ id=" + id + " ]";
    }

    @Override
    public int compareTo(JornalCaido o) {
        return this.getDesde().compareTo(o.getDesde());
    }
    
}
