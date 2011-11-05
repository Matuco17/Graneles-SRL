/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.facturacion;

import com.orco.graneles.domain.miscelaneos.FixedList;
import com.orco.graneles.domain.carga.Embarque;
import com.orco.graneles.domain.carga.Buque;
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
import javax.persistence.ManyToMany;
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
@Table(name = "empresa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Empresa.findAll", query = "SELECT e FROM Empresa e"),
    @NamedQuery(name = "Empresa.findById", query = "SELECT e FROM Empresa e WHERE e.id = :id"),
    @NamedQuery(name = "Empresa.findByNombre", query = "SELECT e FROM Empresa e WHERE e.nombre = :nombre"),
    @NamedQuery(name = "Empresa.findByDireccion", query = "SELECT e FROM Empresa e WHERE e.direccion = :direccion"),
    @NamedQuery(name = "Empresa.findByCuit", query = "SELECT e FROM Empresa e WHERE e.cuit = :cuit"),
    @NamedQuery(name = "Empresa.findByCuidad", query = "SELECT e FROM Empresa e WHERE e.cuidad = :cuidad"),
    @NamedQuery(name = "Empresa.findByTelefono", query = "SELECT e FROM Empresa e WHERE e.telefono = :telefono"),
    @NamedQuery(name = "Empresa.findByMail", query = "SELECT e FROM Empresa e WHERE e.mail = :mail"),
    @NamedQuery(name = "Empresa.findByWeb", query = "SELECT e FROM Empresa e WHERE e.web = :web")})
public class Empresa implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Size(max = 128)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 128)
    @Column(name = "direccion")
    private String direccion;
    @Size(max = 13)
    @Column(name = "cuit")
    private String cuit;
    @Size(max = 128)
    @Column(name = "cuidad")
    private String cuidad;
    @Size(max = 45)
    @Column(name = "telefono")
    private String telefono;
    @Size(max = 45)
    @Column(name = "mail")
    private String mail;
    @Size(max = 45)
    @Column(name = "web")
    private String web;
    @ManyToMany(mappedBy = "empresaCollection")
    private Collection<Buque> buqueCollection;
    @ManyToMany(mappedBy = "empresaCollection")
    private Collection<Embarque> embarqueCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cliente")
    private Collection<Factura> facturaCollection;
    @JoinColumn(name = "tipo_empresa", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private FixedList tipoEmpresa;

    public Empresa() {
    }

    public Empresa(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getCuidad() {
        return cuidad;
    }

    public void setCuidad(String cuidad) {
        this.cuidad = cuidad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    @XmlTransient
    public Collection<Buque> getBuqueCollection() {
        return buqueCollection;
    }

    public void setBuqueCollection(Collection<Buque> buqueCollection) {
        this.buqueCollection = buqueCollection;
    }

    @XmlTransient
    public Collection<Embarque> getEmbarqueCollection() {
        return embarqueCollection;
    }

    public void setEmbarqueCollection(Collection<Embarque> embarqueCollection) {
        this.embarqueCollection = embarqueCollection;
    }

    @XmlTransient
    public Collection<Factura> getFacturaCollection() {
        return facturaCollection;
    }

    public void setFacturaCollection(Collection<Factura> facturaCollection) {
        this.facturaCollection = facturaCollection;
    }

    public FixedList getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(FixedList tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
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
        if (!(object instanceof Empresa)) {
            return false;
        }
        Empresa other = (Empresa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.Empresa[ id=" + id + " ]";
    }
    
}