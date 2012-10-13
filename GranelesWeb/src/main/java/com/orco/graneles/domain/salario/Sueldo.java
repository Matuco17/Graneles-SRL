/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.salario;

import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.personal.Accidentado;
import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.miscelaneos.TipoConceptoRecibo;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "sueldo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sueldo.findAll", query = "SELECT s FROM Sueldo s"),
    @NamedQuery(name = "Sueldo.findById", query = "SELECT s FROM Sueldo s WHERE s.id = :id"),
    @NamedQuery(name = "Sueldo.findByNroRecibo", query = "SELECT s FROM Sueldo s WHERE s.nroRecibo = :nroRecibo")})
public class Sueldo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "nro_recibo")
    private Integer nroRecibo;
    
    @JoinColumn(name = "periodo", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Periodo periodo;
    
    @JoinColumn(name = "personal", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Personal personal;
    
    @OneToMany(cascade = CascadeType.ALL, fetch= FetchType.EAGER, mappedBy="sueldo", orphanRemoval = true)
    private Collection<ItemsSueldo> itemsSueldoCollection;

    @OneToMany(mappedBy = "libroSueldo", cascade = CascadeType.PERSIST)
    private Collection<Accidentado> accidentadoCollection;
    
    /*
     * Propiedades Extendidas
     */
    public BigDecimal getTotalRemunerativo(boolean oficial){
        return totalXTipoConcepto(TipoConceptoRecibo.REMUNERATIVO, oficial);
    }
    
    public BigDecimal getTotalDeductivo(boolean oficial){
        return totalXTipoConcepto(TipoConceptoRecibo.DEDUCTIVO, oficial);
    }
    
    public BigDecimal getTotalNoRemunerativo(boolean oficial){
        return totalXTipoConcepto(TipoConceptoRecibo.NO_REMUNERATIVO, oficial);
    }
    
    public BigDecimal getTotalNoRemunerativoNegativo(boolean oficial){
        return totalXTipoConcepto(TipoConceptoRecibo.NO_REMUNERATIVO_NEGATIVO, oficial);
    }
    
    private BigDecimal totalXTipoConcepto(int tipoConcepto, boolean oficial){
        BigDecimal total = BigDecimal.ZERO;
        
        for (ItemsSueldo is : itemsSueldoCollection){
            if (is.getConceptoRecibo().getTipo().getId().equals(tipoConcepto)){
                if (!oficial || is.getConceptoRecibo().getOficial()){
                    total = total.add(is.getValorCalculado());        
                }
            }
        }
        return total;
    }
    
    public BigDecimal getTotalSueldoNeto(boolean oficial){
        return getTotalRemunerativo(oficial)
                .subtract(getTotalDeductivo(oficial))
                .add(getTotalNoRemunerativo(oficial))
                .subtract(getTotalNoRemunerativoNegativo(oficial));
    }
    
    public Sueldo(){
        
    }
    
    public Sueldo(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNroRecibo() {
        return nroRecibo;
    }

    public void setNroRecibo(Integer nroRecibo) {
        this.nroRecibo = nroRecibo;
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
    public Collection<Accidentado> getAccidentadoCollection() {
        return accidentadoCollection;
    }

    public void setAccidentadoCollection(Collection<Accidentado> accidentadoCollection) {
        this.accidentadoCollection = accidentadoCollection;
    }

    @XmlTransient
    public Collection<ItemsSueldo> getItemsSueldoCollection() {
        return itemsSueldoCollection;
    }

    public void setItemsSueldoCollection(Collection<ItemsSueldo> itemsSueldoCollection) {
        this.itemsSueldoCollection = itemsSueldoCollection;
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
        if (!(object instanceof Sueldo)) {
            return false;
        }
        Sueldo other = (Sueldo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.Sueldo[ id=" + id + " ]";
    }
    
}
