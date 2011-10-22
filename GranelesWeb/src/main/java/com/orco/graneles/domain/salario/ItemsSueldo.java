/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.salario;

import com.orco.graneles.domain.miscelaneos.TipoConceptoRecibo;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import javax.xml.bind.annotation.XmlRootElement;
import org.joda.time.DateTime;
import org.joda.time.Years;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "items_sueldo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ItemsSueldo.findAll", query = "SELECT i FROM ItemsSueldo i"),
    @NamedQuery(name = "ItemsSueldo.findById", query = "SELECT i FROM ItemsSueldo i WHERE i.id = :id"),
    @NamedQuery(name = "ItemsSueldo.findByValorIngresado", query = "SELECT i FROM ItemsSueldo i WHERE i.valorIngresado = :valorIngresado"),
    @NamedQuery(name = "ItemsSueldo.findByValorCalculado", query = "SELECT i FROM ItemsSueldo i WHERE i.valorCalculado = :valorCalculado")})
public class ItemsSueldo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor_ingresado")
    private BigDecimal valorIngresado;
    @Column(name = "valor_calculado")
    private BigDecimal valorCalculado;
    @Column(name = "cantidad")
    private BigDecimal cantidad;

    @JoinColumn(name = "concepto_recibo", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ConceptoRecibo conceptoRecibo;
    @JoinColumn(name = "sueldo", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Sueldo sueldo;

    
    
    
    public ItemsSueldo() {
    }

    public ItemsSueldo(Long id) {
        this.id = id;
    }
    
    /**
     * Atributos con propiedades extendidas
     * @return 
     */
    
    public Integer getOrdenConcepto(){
        return this.getConceptoRecibo().getOrden();
    }
    
      public BigInteger getNroPrimeraHoja(){
        return this.getSueldo().getPeriodo().getNroPrimeraHoja();
    }

    public String getNroTomo(){
        return this.getSueldo().getPeriodo().getFolioLibro();
    }
    
    public String getApellidoNombre(){
        return this.getSueldo().getPersonal().getApellido();
    }
    
    public String getDireccion(){
        return this.getSueldo().getPersonal().getDomicilio();
    }
   
    public String getLocalidad(){
        return this.getSueldo().getPersonal().getLocalidad();
    }
    
    public Date getFechaIngreso(){
        return this.getSueldo().getPersonal().getIngreso();
    }
    /*
    public Integer getAntiguedad(){
        return Years.yearsBetween(
                    new DateTime(this.getSueldo().getPersonal().getIngreso()), 
                    new DateTime()).getYears();        
        
    }
    */
    public String getCategoria(){
        return (this.getSueldo().getPersonal().getCategoriaPrincipal() != null)
                    ?this.getSueldo().getPersonal().getCategoriaPrincipal().getDescripcion()
                : null;
    }
    
    public String getDescripcionPeriodo(){
        return this.getSueldo().getPeriodo().getDescripcion();
    }
    
    public String getCuil(){
        return this.getSueldo().getPersonal().getCuil();
    }
    
    public String getSexo(){
        //TODO: AGREGAR LA COLUMA DE SEXO
        return "M";
    }
    
    public String getDocumentoYTipo(){
        return this.getSueldo().getPersonal().getDocumento();
    }
    
    public Date getFechaEgreso(){
        //TODO: AGREGAR LA COLUMNA FECHA EGRESO
        return null;
    }
    
    public String getEstadoCivil(){
        return this.getSueldo().getPersonal().getEstadoCivil().getDescripcion();
    }
    
    public String getConceptoDescripcion(){
        return this.getConceptoRecibo().getConcepto();
    }
    
    public BigDecimal getConceptoCantidad(){
        return (cantidad != null && cantidad.doubleValue() > 0.009)? cantidad : null;
    }
    
    public String getLegajo(){
        return this.getSueldo().getPersonal().getRegistro();
    }
    
    public BigDecimal getValorRemunerativo(){
        if (this.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.REMUNERATIVO)){
            return this.getValorCalculado();
        } else {
            return null;
        }
    }
    
    public BigDecimal getValorDeductivo(){
        if (this.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.DEDUCTIVO)){
            return this.getValorCalculado();
        } else {
            return null;
        }
    }
    
    public BigDecimal getValorNoRemunerativo(){
        if (this.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.NO_REMUNERATIVO)){
            return this.getValorCalculado();
        } else {
            return null;
        }
    }
    
    public BigDecimal getTotaRemunerativo(){
        return this.getSueldo().getTotalRemunerativo();
    }
    
    public BigDecimal getTotalDeductivo(){
        return this.getSueldo().getTotalDeductivo();
    }
    
    public BigDecimal getTotalNoRemunerativo(){
        return this.getSueldo().getTotalNoRemunerativo();
    }
    
    public BigDecimal getTotalSueldoNeto(){
        return this.getSueldo().getTotalSueldoNeto();
    }
    
     /*
     * Variables para agrupar y crear resultados
     */
   
    public Integer getIDConceptoRecibo(){
        return this.getConceptoRecibo().getId();
    }
    
    public Long getIDSueldo(){
        return this.getSueldo().getId();
    }
    
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorIngresado() {
        return valorIngresado;
    }

    public void setValorIngresado(BigDecimal valorIngresado) {
        this.valorIngresado = valorIngresado;
    }

    public BigDecimal getValorCalculado() {
        return valorCalculado;
    }

    public void setValorCalculado(BigDecimal valorCalculado) {
        this.valorCalculado = valorCalculado;
    }

    public ConceptoRecibo getConceptoRecibo() {
        return conceptoRecibo;
    }

    public void setConceptoRecibo(ConceptoRecibo conceptoRecibo) {
        this.conceptoRecibo = conceptoRecibo;
    }

    public Sueldo getSueldo() {
        return sueldo;
    }

    public void setSueldo(Sueldo sueldo) {
        this.sueldo = sueldo;
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
        if (!(object instanceof ItemsSueldo)) {
            return false;
        }
        ItemsSueldo other = (ItemsSueldo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.ItemsSueldo[ id=" + id + " ]";
    }
    
    
    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }
    
}
