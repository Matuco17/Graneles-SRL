/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.carga;

import com.orco.graneles.domain.facturacion.Empresa;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Table(name = "embarque")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Embarque.findAll", query = "SELECT e FROM Embarque e"),
    @NamedQuery(name = "Embarque.findById", query = "SELECT e FROM Embarque e WHERE e.id = :id"),
    @NamedQuery(name = "Embarque.findByDestino", query = "SELECT e FROM Embarque e WHERE e.destino = :destino"),
    @NamedQuery(name = "Embarque.findByCoordinador", query = "SELECT e FROM Embarque e WHERE e.coordinador = :coordinador"),
    @NamedQuery(name = "Embarque.findByControl", query = "SELECT e FROM Embarque e WHERE e.control = :control"),
    @NamedQuery(name = "Embarque.findByFumigacion", query = "SELECT e FROM Embarque e WHERE e.fumigacion = :fumigacion"),
    @NamedQuery(name = "Embarque.findByBoya11", query = "SELECT e FROM Embarque e WHERE e.boya11 = :boya11"),
    @NamedQuery(name = "Embarque.findByInicioNavegacion", query = "SELECT e FROM Embarque e WHERE e.inicioNavegacion = :inicioNavegacion"),
    @NamedQuery(name = "Embarque.findByAtco", query = "SELECT e FROM Embarque e WHERE e.atco = :atco"),
    @NamedQuery(name = "Embarque.findByAna", query = "SELECT e FROM Embarque e WHERE e.ana = :ana"),
    @NamedQuery(name = "Embarque.findByIb", query = "SELECT e FROM Embarque e WHERE e.ib = :ib"),
    @NamedQuery(name = "Embarque.findByCzo", query = "SELECT e FROM Embarque e WHERE e.czo = :czo"),
    @NamedQuery(name = "Embarque.findByTmo", query = "SELECT e FROM Embarque e WHERE e.tmo = :tmo"),
    @NamedQuery(name = "Embarque.findByMomentoFumigacion", query = "SELECT e FROM Embarque e WHERE e.momentoFumigacion = :momentoFumigacion"),
    @NamedQuery(name = "Embarque.findByBoletosPor", query = "SELECT e FROM Embarque e WHERE e.boletosPor = :boletosPor"),
    @NamedQuery(name = "Embarque.findByMaxACargar", query = "SELECT e FROM Embarque e WHERE e.maxACargar = :maxACargar"),
    @NamedQuery(name = "Embarque.findByPreStowPlan", query = "SELECT e FROM Embarque e WHERE e.preStowPlan = :preStowPlan")})
public class Embarque implements Serializable, Comparable<Embarque> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "codigo")
    private Long codigo;
    @Size(max = 128)
    @Column(name = "destino")
    private String destino;
    @Size(max = 128)
    @Column(name = "coordinador")
    private String coordinador;
    @Size(max = 128)
    @Column(name = "control")
    private String control;
    @Size(max = 128)
    @Column(name = "fumigacion")
    private String fumigacion;
    @Column(name = "boya11")
    @Temporal(TemporalType.TIMESTAMP)
    private Date boya11;
    @Column(name = "inicio_navegacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inicioNavegacion;
    @Column(name = "atco")
    @Temporal(TemporalType.TIMESTAMP)
    private Date atco;
    @Column(name = "ana")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ana;
    @Column(name = "ib")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ib;
    @Column(name = "czo")
    @Temporal(TemporalType.TIMESTAMP)
    private Date czo;
    @Column(name = "tmo")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tmo;
    @Size(max = 45)
    @Column(name = "momento_fumigacion")
    private String momentoFumigacion;
    @Size(max = 128)
    @Column(name = "boletos_por")
    private String boletosPor;
    @Size(max = 128)
    @Column(name = "max_a_cargar")
    private String maxACargar;
    @Size(max = 45)
    @Column(name = "pre_stow_plan")
    private String preStowPlan;
    @JoinTable(name = "embarque_exportador", joinColumns = {
        @JoinColumn(name = "embarque", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "empresa", referencedColumnName = "id")})
    @ManyToMany
    private Collection<Empresa> empresaCollection;
    @JoinColumn(name = "mercaderia", referencedColumnName = "id")
    @ManyToOne
    private Mercaderia mercaderia;
    @JoinColumn(name = "muelle", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Muelle muelle;
    
    @JoinColumn(name = "buque", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Buque buque;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "embarque", orphanRemoval = true)
    private Collection<ArchivoEmbarque> archivoEmbarqueCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "embarque", orphanRemoval = true)
    private Collection<CargaPrevia> cargaPreviaCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "embarque", orphanRemoval = true)
    private Collection<TurnoEmbarque> turnoEmbarqueCollection;

    public Embarque() {
    }

    public Embarque(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getCoordinador() {
        return coordinador;
    }

    public void setCoordinador(String coordinador) {
        this.coordinador = coordinador;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public String getFumigacion() {
        return fumigacion;
    }

    public void setFumigacion(String fumigacion) {
        this.fumigacion = fumigacion;
    }

    public Date getBoya11() {
        return boya11;
    }

    public void setBoya11(Date boya11) {
        this.boya11 = boya11;
    }

    public Date getInicioNavegacion() {
        return inicioNavegacion;
    }

    public void setInicioNavegacion(Date inicioNavegacion) {
        this.inicioNavegacion = inicioNavegacion;
    }

    public Date getAtco() {
        return atco;
    }

    public void setAtco(Date atco) {
        this.atco = atco;
    }

    public Date getAna() {
        return ana;
    }

    public void setAna(Date ana) {
        this.ana = ana;
    }

    public Date getIb() {
        return ib;
    }

    public void setIb(Date ib) {
        this.ib = ib;
    }

    public Date getCzo() {
        return czo;
    }

    public void setCzo(Date czo) {
        this.czo = czo;
    }

    public Date getTmo() {
        return tmo;
    }

    public void setTmo(Date tmo) {
        this.tmo = tmo;
    }

    public String getMomentoFumigacion() {
        return momentoFumigacion;
    }

    public void setMomentoFumigacion(String momentoFumigacion) {
        this.momentoFumigacion = momentoFumigacion;
    }

    public String getBoletosPor() {
        return boletosPor;
    }

    public void setBoletosPor(String boletosPor) {
        this.boletosPor = boletosPor;
    }

    public String getMaxACargar() {
        return maxACargar;
    }

    public void setMaxACargar(String maxACargar) {
        this.maxACargar = maxACargar;
    }

    public String getPreStowPlan() {
        return preStowPlan;
    }

    public void setPreStowPlan(String preStowPlan) {
        this.preStowPlan = preStowPlan;
    }

    @XmlTransient
    public Collection<Empresa> getEmpresaCollection() {
        return empresaCollection;
    }

    public void setEmpresaCollection(Collection<Empresa> empresaCollection) {
        this.empresaCollection = empresaCollection;
    }

    public Mercaderia getMercaderia() {
        return mercaderia;
    }

    public void setMercaderia(Mercaderia mercaderia) {
        this.mercaderia = mercaderia;
    }

    public Muelle getMuelle() {
        return muelle;
    }

    public void setMuelle(Muelle muelle) {
        this.muelle = muelle;
    }

    public Buque getBuque() {
        return buque;
    }

    public void setBuque(Buque buque) {
        this.buque = buque;
    }

    @XmlTransient
    public Collection<ArchivoEmbarque> getArchivoEmbarqueCollection() {
        return archivoEmbarqueCollection;
    }

    public void setArchivoEmbarqueCollection(Collection<ArchivoEmbarque> archivoEmbarqueCollection) {
        this.archivoEmbarqueCollection = archivoEmbarqueCollection;
    }

    @XmlTransient
    public Collection<CargaPrevia> getCargaPreviaCollection() {
        return cargaPreviaCollection;
    }

    public void setCargaPreviaCollection(Collection<CargaPrevia> cargaPreviaCollection) {
        this.cargaPreviaCollection = cargaPreviaCollection;
    }

    @XmlTransient
    public Collection<TurnoEmbarque> getTurnoEmbarqueCollection() {
        return turnoEmbarqueCollection;
    }

    public void setTurnoEmbarqueCollection(Collection<TurnoEmbarque> turnoEmbarqueCollection) {
        this.turnoEmbarqueCollection = turnoEmbarqueCollection;
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
        if (!(object instanceof Embarque)) {
            return false;
        }
        Embarque other = (Embarque) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getCodigo().toString();
    }

    @Override
    public int compareTo(Embarque o) {
        return this.codigo.compareTo(o.codigo);
    }
    
}
