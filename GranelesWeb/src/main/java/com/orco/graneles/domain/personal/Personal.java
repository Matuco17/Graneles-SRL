/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.personal;

import com.orco.graneles.domain.salario.Adelanto;
import com.orco.graneles.domain.salario.Sueldo;
import com.orco.graneles.domain.miscelaneos.FixedList;
import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "personal")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Personal.findAll", query = "SELECT p FROM Personal p"),
    @NamedQuery(name = "Personal.findById", query = "SELECT p FROM Personal p WHERE p.id = :id"),
    @NamedQuery(name = "Personal.findByApellido", query = "SELECT p FROM Personal p WHERE p.apellido = :apellido"),
    @NamedQuery(name = "Personal.findByRegistro", query = "SELECT p FROM Personal p WHERE p.registro = :registro"),
    @NamedQuery(name = "Personal.findByCuil", query = "SELECT p FROM Personal p WHERE p.cuil = :cuil"),
    @NamedQuery(name = "Personal.findByNroAfiliado", query = "SELECT p FROM Personal p WHERE p.nroAfiliado = :nroAfiliado"),
    @NamedQuery(name = "Personal.findByDocumento", query = "SELECT p FROM Personal p WHERE p.documento = :documento"),
    @NamedQuery(name = "Personal.findByDomicilio", query = "SELECT p FROM Personal p WHERE p.domicilio = :domicilio"),
    @NamedQuery(name = "Personal.findByFechaNacimiento", query = "SELECT p FROM Personal p WHERE p.fechaNacimiento = :fechaNacimiento"),
    @NamedQuery(name = "Personal.findByIngreso", query = "SELECT p FROM Personal p WHERE p.ingreso = :ingreso"),
    @NamedQuery(name = "Personal.findByLocalidad", query = "SELECT p FROM Personal p WHERE p.localidad = :localidad"),
    @NamedQuery(name = "Personal.findByCuentaBancaria", query = "SELECT p FROM Personal p WHERE p.cuentaBancaria = :cuentaBancaria"),
    @NamedQuery(name = "Personal.findByEsposa", query = "SELECT p FROM Personal p WHERE p.esposa = :esposa"),
    @NamedQuery(name = "Personal.findByHijos", query = "SELECT p FROM Personal p WHERE p.hijos = :hijos"),
    @NamedQuery(name = "Personal.findByPrenatal", query = "SELECT p FROM Personal p WHERE p.prenatal = :prenatal"),
    @NamedQuery(name = "Personal.findByEscolaridad", query = "SELECT p FROM Personal p WHERE p.escolaridad = :escolaridad"),
    @NamedQuery(name = "Personal.findByDescuentoJudicial", query = "SELECT p FROM Personal p WHERE p.descuentoJudicial = :descuentoJudicial"),
    @NamedQuery(name = "Personal.findByTipoReciboYActivo", query = "SELECT p FROM Personal p WHERE p.tipoRecibo = :tipoRecibo AND p.estado = :estado AND p.versionActiva = 1"),
    @NamedQuery(name = "Personal.findByVersion", query = "SELECT p FROM Personal p WHERE p.version = :version"),
    @NamedQuery(name = "Personal.findByVersionActiva", query = "SELECT p FROM Personal p WHERE p.versionActiva = :versionActiva"),
    @NamedQuery(name = "Personal.findByUrlFoto", query = "SELECT p FROM Personal p WHERE p.urlFoto = :urlFoto")})
public class Personal implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Size(max = 128)
    @Column(name = "apellido")
    private String apellido;
    
    @Size(max = 10)
    @Column(name = "registro")
    private String registro;
    
    @Size(max = 13)
    @Column(name = "cuil")
    private String cuil;
    
    @Column(name = "nro_afiliado")
    private Integer nroAfiliado;
    
    @Size(max = 10)
    @Column(name = "documento")
    private String documento;
    
    @Size(max = 256)
    @Column(name = "domicilio")
    private String domicilio;
    
    @Column(name = "fecha_nacimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;
    
    @Column(name = "ingreso")
    @Temporal(TemporalType.DATE)
    private Date ingreso;
    
    @Column(name = "baja")
    @Temporal(TemporalType.DATE)
    private Date baja;
    
    @Size(max = 45)
    @Column(name = "localidad")
    private String localidad;
    
    @Size(max = 45)
    @Column(name = "cuenta_bancaria")
    private String cuentaBancaria;
    
    @Column(name = "esposa")
    private Boolean esposa;
    
    @Column(name = "hijos")
    private Integer hijos;
    
    @Column(name = "prenatal")
    private Boolean prenatal;
    
    @Column(name = "escolaridad")
    private Integer escolaridad;
    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "descuento_judicial")
    private BigDecimal descuentoJudicial;
    
    @Column(name = "version")
    private Integer version;
    
    @Column(name = "version_activa")
    private Boolean versionActiva;
    
    @Size(max = 256)
    @Column(name = "url_foto")
    private String urlFoto;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "personal")
    private Collection<Sueldo> sueldoCollection;
    
    @JoinColumn(name = "estado", referencedColumnName = "id")
    @ManyToOne
    private FixedList estado;
    
    @JoinColumn(name = "tipo_recibo", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private FixedList tipoRecibo;
    
    @JoinColumn(name = "tipo_documento", referencedColumnName = "id")
    @ManyToOne
    private FixedList tipoDocumento;
    
    @JoinColumn(name = "estado_civil", referencedColumnName = "id")
    @ManyToOne
    private FixedList estadoCivil;
    
    @JoinColumn(name = "obra_social", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ObraSocial obraSocial;
    
    @Column(name = "sindicato")
    private Boolean sindicato;
    
    @Column(name = "afjp")
    private Boolean afjp;    
    
    @JoinColumn(name = "categoria_principal", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Categoria categoriaPrincipal;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "personal")
    private Collection<TrabajadoresTurnoEmbarque> trabajadoresTurnoEmbarqueCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "personal")
    private Collection<Adelanto> adelantoCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "personal")
    private Collection<Accidentado> accidentadoCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "personal")
    private Collection<CategoriaSecundaria> categoriaSecundariaCollection;

    public Personal() {
    }

    public Personal(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    public String getCuil() {
        return cuil;
    }

    public void setCuil(String cuil) {
        this.cuil = cuil;
    }

    public Integer getNroAfiliado() {
        return nroAfiliado;
    }

    public void setNroAfiliado(Integer nroAfiliado) {
        this.nroAfiliado = nroAfiliado;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Date getIngreso() {
        return ingreso;
    }

    public void setIngreso(Date ingreso) {
        this.ingreso = ingreso;
    }

    public Date getBaja() {
        return baja;
    }

    public void setBaja(Date baja) {
        this.baja = baja;
    }    
    
    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getCuentaBancaria() {
        return cuentaBancaria;
    }

    public void setCuentaBancaria(String cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    public Boolean getEsposa() {
        return esposa;
    }

    public void setEsposa(Boolean esposa) {
        this.esposa = esposa;
    }

    public Integer getHijos() {
        return hijos;
    }

    public void setHijos(Integer hijos) {
        this.hijos = hijos;
    }

    public Boolean getPrenatal() {
        return prenatal;
    }

    public void setPrenatal(Boolean prenatal) {
        this.prenatal = prenatal;
    }

    public Integer getEscolaridad() {
        return escolaridad;
    }

    public void setEscolaridad(Integer escolaridad) {
        this.escolaridad = escolaridad;
    }

    public BigDecimal getDescuentoJudicial() {
        return descuentoJudicial;
    }

    public void setDescuentoJudicial(BigDecimal descuentoJudicial) {
        this.descuentoJudicial = descuentoJudicial;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getVersionActiva() {
        return versionActiva;
    }

    public void setVersionActiva(Boolean versionActiva) {
        this.versionActiva = versionActiva;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    @XmlTransient
    public Collection<Sueldo> getSueldoCollection() {
        return sueldoCollection;
    }

    public void setSueldoCollection(Collection<Sueldo> sueldoCollection) {
        this.sueldoCollection = sueldoCollection;
    }

    public FixedList getEstado() {
        return estado;
    }

    public void setEstado(FixedList estado) {
        this.estado = estado;
    }

    public FixedList getTipoRecibo() {
        return tipoRecibo;
    }

    public void setTipoRecibo(FixedList tipoRecibo) {
        this.tipoRecibo = tipoRecibo;
    }

    public FixedList getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(FixedList tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public FixedList getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(FixedList estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public ObraSocial getObraSocial() {
        return obraSocial;
    }

    public void setObraSocial(ObraSocial obraSocial) {
        this.obraSocial = obraSocial;
    }

    public Boolean getSindicato() {
        return sindicato;
    }

    public void setSindicato(Boolean sindicato) {
        this.sindicato = sindicato;
    }

    public Categoria getCategoriaPrincipal() {
        return categoriaPrincipal;
    }

    public void setCategoriaPrincipal(Categoria categoriaPrincipal) {
        this.categoriaPrincipal = categoriaPrincipal;
    }

    public Boolean getAfjp() {
        return afjp;
    }

    public void setAfjp(Boolean afjp) {
        this.afjp = afjp;
    }
    
    @XmlTransient
    public Collection<TrabajadoresTurnoEmbarque> getTrabajadoresTurnoEmbarqueCollection() {
        return trabajadoresTurnoEmbarqueCollection;
    }

    public void setTrabajadoresTurnoEmbarqueCollection(Collection<TrabajadoresTurnoEmbarque> trabajadoresTurnoEmbarqueCollection) {
        this.trabajadoresTurnoEmbarqueCollection = trabajadoresTurnoEmbarqueCollection;
    }

    @XmlTransient
    public Collection<Adelanto> getAdelantoCollection() {
        return adelantoCollection;
    }

    public void setAdelantoCollection(Collection<Adelanto> adelantoCollection) {
        this.adelantoCollection = adelantoCollection;
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
        if (!(object instanceof Personal)) {
            return false;
        }
        Personal other = (Personal) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.apellido;
    }
    
}
