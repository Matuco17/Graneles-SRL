/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.personal;

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
@Table(name = "categoria_secundaria")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CategoriaSecundaria.findAll", query = "SELECT c FROM CategoriaSecundaria c"),
    @NamedQuery(name = "CategoriaSecundaria.findById", query = "SELECT c FROM CategoriaSecundaria c WHERE c.id = :id"),
    @NamedQuery(name = "CategoriaSecundaria.findByRegistro", query = "SELECT c FROM CategoriaSecundaria c WHERE c.registro = :registro")})
public class CategoriaSecundaria implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Size(max = 10)
    @Column(name = "registro")
    private String registro;
    @JoinColumn(name = "personal", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Personal personal;
    @JoinColumn(name = "categoria", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Categoria categoria;

    public CategoriaSecundaria() {
    }

    public CategoriaSecundaria(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    public Personal getPersonal() {
        return personal;
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
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
        if (!(object instanceof CategoriaSecundaria)) {
            return false;
        }
        CategoriaSecundaria other = (CategoriaSecundaria) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.CategoriaSecundaria[ id=" + id + " ]";
    }
    
}
