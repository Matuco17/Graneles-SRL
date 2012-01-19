/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.domain.carga;

import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.Sueldo;
import com.orco.graneles.domain.personal.Tarea;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "trabajadores_turno_embarque")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TrabajadoresTurnoEmbarque.findAll", query = "SELECT t FROM TrabajadoresTurnoEmbarque t"),
    @NamedQuery(name = "TrabajadoresTurnoEmbarque.findById", query = "SELECT t FROM TrabajadoresTurnoEmbarque t WHERE t.id = :id"),
    @NamedQuery(name = "TrabajadoresTurnoEmbarque.findXPeriodo",
                query = "SELECT t FROM TrabajadoresTurnoEmbarque t JOIN t.planilla p "
                               + " WHERE t.planilla.fecha BETWEEN :desde AND :hasta"),
    @NamedQuery(name = "TrabajadoresTurnoEmbarque.findXPeriodoYPersonal",
                query = "SELECT t FROM TrabajadoresTurnoEmbarque t JOIN t.planilla p "
                               + " WHERE t.personal = :personal AND"
                               + " t.planilla.fecha BETWEEN :desde AND :hasta"
                               + " ORDER BY t.planilla.fecha"),
    @NamedQuery(name = "TrabajadoresTurnoEmbarque.findXPersonalFechaDesc",
                query = "SELECT t FROM TrabajadoresTurnoEmbarque t "
                            + " WHERE t.personal = :personal "
                            + " ORDER BY t.planilla.fecha DESC ")})
public class TrabajadoresTurnoEmbarque implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "desde")
    private Integer desde;
    
    @Column(name = "hasta")
    private Integer hasta;
   
    @Column(name = "delegado")
    private Boolean delegado;    
    
    @JoinColumn(name = "libro_sueldo", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Sueldo libroSueldo;
    
    @JoinColumn(name = "tarea", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Tarea tarea;
    
    @JoinColumn(name = "categoria", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Categoria categoria;
    
    @JoinColumn(name = "personal", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Personal personal;
    
    @JoinColumn(name = "planilla", referencedColumnName = "id", nullable = true)
    @ManyToOne(optional = true)
    private TurnoEmbarque planilla;
    
   

    public TrabajadoresTurnoEmbarque() {
    }

    public TrabajadoresTurnoEmbarque(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getHoras() {
        if (desde != null && hasta != null){
            if (desde > hasta){
                return hasta + 24 - desde;                
            } else {
                return hasta - desde;
            }
        } else {
            return 0;
        }
    }

    public Integer getDesde() {
        return desde;
    }

    public void setDesde(Integer desde) {
        this.desde = desde;
    }

    public Integer getHasta() {
        return hasta;
    }

    public void setHasta(Integer hasta) {
        this.hasta = hasta;
    }

    public Boolean getDelegado() {
        return delegado;
    }

    public void setDelegado(Boolean delegado) {
        this.delegado = delegado;
    }
    
    public Sueldo getLibroSueldo() {
        return libroSueldo;
    }

    public void setLibroSueldo(Sueldo libroSueldo) {
        this.libroSueldo = libroSueldo;
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

    public Personal getPersonal() {
        return personal;
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
    }

    public TurnoEmbarque getPlanilla() {
        return planilla;
    }

    public void setPlanilla(TurnoEmbarque planilla) {
        this.planilla = planilla;
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
        if (!(object instanceof TrabajadoresTurnoEmbarque)) {
            return false;
        }
        TrabajadoresTurnoEmbarque other = (TrabajadoresTurnoEmbarque) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.TrabajadoresTurnoEmbarque[ id=" + id + " ]";
    }
    
}
