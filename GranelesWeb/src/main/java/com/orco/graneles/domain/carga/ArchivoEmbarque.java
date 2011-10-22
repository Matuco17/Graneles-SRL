/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.carga;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "archivo_embarque")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ArchivoEmbarque.findAll", query = "SELECT a FROM ArchivoEmbarque a"),
    @NamedQuery(name = "ArchivoEmbarque.findById", query = "SELECT a FROM ArchivoEmbarque a WHERE a.id = :id")})
public class ArchivoEmbarque implements Serializable, Comparable<ArchivoEmbarque> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Size(max = 2000)
    @Column(name = "url")
    private String nombreArchivo;
    @JoinColumn(name = "embarque", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Embarque embarque;

    public ArchivoEmbarque() {
    }

    public ArchivoEmbarque(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }
    
    public String getURLCompleto(){
        return "~/../../../resources/uploadedFiles/" + getNombreArchivoEnDisco();
    }
    
    public String getNombreArchivoEnDisco(){
        return "embarque_" + getEmbarque().getId() + "_" + getNombreArchivo();
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public Embarque getEmbarque() {
        return embarque;
    }

    public void setEmbarque(Embarque embarque) {
        this.embarque = embarque;
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
        if (!(object instanceof ArchivoEmbarque)) {
            return false;
        }
        ArchivoEmbarque other = (ArchivoEmbarque) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombreArchivo;
    }

    @Override
    public int compareTo(ArchivoEmbarque o) {
        return this.nombreArchivo.compareTo(o.nombreArchivo);
    }
    
}
