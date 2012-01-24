/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.domain.carga;

import com.orco.graneles.domain.facturacion.Empresa;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author orco
 */
@Entity
@Table(name = "turno_embarque_observaciones", catalog = "graneles", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TurnoEmbarqueObservaciones.findAll", query = "SELECT t FROM TurnoEmbarqueObservaciones t"),
    @NamedQuery(name = "TurnoEmbarqueObservaciones.findById", query = "SELECT t FROM TurnoEmbarqueObservaciones t WHERE t.id = :id"),
    @NamedQuery(name = "TurnoEmbarqueObservaciones.findByDesde", query = "SELECT t FROM TurnoEmbarqueObservaciones t WHERE t.desde = :desde"),
    @NamedQuery(name = "TurnoEmbarqueObservaciones.findByHasta", query = "SELECT t FROM TurnoEmbarqueObservaciones t WHERE t.hasta = :hasta"),
    @NamedQuery(name = "TurnoEmbarqueObservaciones.findByObservacion", query = "SELECT t FROM TurnoEmbarqueObservaciones t WHERE t.observacion = :observacion")})
public class TurnoEmbarqueObservaciones implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "desde", nullable = false)
    private int desde;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "hasta", nullable = false)
    private int hasta;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "observacion", nullable = false, length = 256)
    private String observacion;
    
    @JoinColumn(name = "turno", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private TurnoEmbarque turno;
    
    @JoinColumn(name = "bodega", referencedColumnName = "id")
    @ManyToOne
    private Bodega bodega;
    
    @JoinColumn(name = "cargador", referencedColumnName = "id")
    @ManyToOne
    private Empresa cargador;

    public TurnoEmbarqueObservaciones() {
    }

    public TurnoEmbarqueObservaciones(Long id) {
        this.id = id;
    }

    public TurnoEmbarqueObservaciones(Long id, int desde, int hasta, String observacion) {
        this.id = id;
        this.desde = desde;
        this.hasta = hasta;
        this.observacion = observacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDesde() {
        return desde;
    }

    public void setDesde(int desde) {
        this.desde = desde;
    }

    public int getHasta() {
        return hasta;
    }

    public void setHasta(int hasta) {
        this.hasta = hasta;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public TurnoEmbarque getTurno() {
        return turno;
    }

    public void setTurno(TurnoEmbarque turno) {
        this.turno = turno;
    }

    public Bodega getBodega() {
        return bodega;
    }

    public void setBodega(Bodega bodega) {
        this.bodega = bodega;
    }

    public Empresa getCargador() {
        return cargador;
    }

    public void setCargador(Empresa cargador) {
        this.cargador = cargador;
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
        if (!(object instanceof TurnoEmbarqueObservaciones)) {
            return false;
        }
        TurnoEmbarqueObservaciones other = (TurnoEmbarqueObservaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.orco.graneles.domain.carga.TurnoEmbarqueObservaciones[ id=" + id + " ]";
    }
    
}
