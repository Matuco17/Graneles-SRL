/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.vo;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.personal.Tarea;
import com.orco.graneles.domain.salario.Feriado;
import com.orco.graneles.domain.salario.TipoJornal;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author orco
 */
public class JornalVO implements Serializable, Comparable<JornalVO> {
    
    private static DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
    
    private Date fecha;
    private String planilla;
    private Personal personal;
    private Categoria categoria;
    private Tarea tarea;
    private TipoJornal tipoJornal;
    private Integer horas;
    private BigDecimal bruto;
  
    
    public JornalVO(TrabajadoresTurnoEmbarque tte, Feriado feriado, TipoJornal tipoJornal){
        if (feriado != null){
            fecha = feriado.getFecha();
            if (tipoJornal != null){
                this.tipoJornal = tipoJornal;
            } else {
                this.tipoJornal = tte.getPlanilla().getTipo();
            }
            this.planilla = "Feriado";
        } else {
            this.fecha = tte.getPlanilla().getFecha();
            this.tipoJornal = tte.getPlanilla().getTipo();
            this.planilla = tte.getPlanilla().getNroPlanilla().toString();
        }
        this.personal = tte.getPersonal();
        this.categoria = tte.getCategoria();
        this.tarea = tte.getTarea();
        this.horas = tte.getHoras();
        this.bruto = tte.getBruto();
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getPlanilla() {
        return planilla;
    }

    public void setPlanilla(String planilla) {
        this.planilla = planilla;
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

    public TipoJornal getTipoJornal() {
        return tipoJornal;
    }

    public void setTipoJornal(TipoJornal tipoJornal) {
        this.tipoJornal = tipoJornal;
    }

    public Integer getHoras() {
        return horas;
    }

    public void setHoras(Integer horas) {
        this.horas = horas;
    }

    public BigDecimal getBruto() {
        return bruto;
    }

    public void setBruto(BigDecimal bruto) {
        this.bruto = bruto;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }

    @Override
    public int compareTo(JornalVO o) {
        String thisFechaStr = dateFormatter.format(this.getFecha());
        String otherFechaStr = dateFormatter.format(o.getFecha());
        if (thisFechaStr.compareTo(otherFechaStr) == 0){
            if (this.getPlanilla().compareTo(o.getPlanilla()) == 0){
                return this.getPersonal().getApellido().compareToIgnoreCase(o.getPersonal().getApellido());
            } else {
                return this.getPlanilla().compareTo(o.getPlanilla());
            }
        } else {
            return thisFechaStr.compareTo(otherFechaStr);
        }
    }
    
    
    
}
