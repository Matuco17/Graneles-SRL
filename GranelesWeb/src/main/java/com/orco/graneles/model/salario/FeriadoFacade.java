/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.miscelaneos.TipoJornal;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.Feriado;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.carga.TrabajadoresTurnoEmbarqueFacade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
/**
 *
 * @author orco
 */
@Stateless
public class FeriadoFacade extends AbstractFacade<Feriado> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @EJB
    private TrabajadoresTurnoEmbarqueFacade tteF;
    
    
    protected EntityManager getEntityManager() {
        return em;
    }

    public FeriadoFacade() {
        super(Feriado.class);
    }
    
    private static int DIAS_ANTERIORES_FERIADO = 10;
    private static int DIAS_POSTERIORES_FERIADO = 5;
    
    /**
     * Metodo que calcula los trabajadores que corresponden cobrar el feriado
     * @param fechaFeriado
     * @return 
     */
    public Map<Personal, TrabajadoresTurnoEmbarque> obtenerTrabajadoresIncluidos(Date fechaFeriado){
        DateTime feriadoDT = new DateTime(fechaFeriado);
        Map<Personal, Integer> trabajadoresDiaAnterior = new HashMap<Personal, Integer>();
        Map<Personal, Integer> trabajadoresXDiasPosteriores = new HashMap<Personal, Integer>();
        Map<Personal, Integer> trabajadoresXDiasAnteriores = new HashMap<Personal, Integer>();
        Map<Personal, TrabajadoresTurnoEmbarque> trabajadoresIncluidos = new HashMap<Personal, TrabajadoresTurnoEmbarque>(); //Map que lo lleno con todos lso tte y luego elimino los q no pertenecen al feriado
        Set<Personal> personalIncluido = new HashSet<Personal>();
        Set<Personal> personalMismoDia = new HashSet<Personal>();
        
        DateTime diaAnterior = diaAnteriorFeriado(feriadoDT);
        DateTime diaInicioAnteriores = diasAnterioresFeriado(feriadoDT);
        DateTime diaFinPosteriores = diasPosterioresFeriado(feriadoDT);
        
        for (TrabajadoresTurnoEmbarque tte : tteF.getTrabajadoresPeriodo(diaInicioAnteriores.toDate(), diaFinPosteriores.toDate())){
            Date fechaPlanilla = tte.getPlanilla().getFecha();
            //Para contar las horas, solamente tengo que contar las jornadas habiles, las otras no se tienen en cuenta
            if (tte.getPlanilla().getTipo().getId() == TipoJornal.HABIL){
                if (fechaPlanilla.equals(diaAnterior.toDate())){
                    agregarTTE(trabajadoresDiaAnterior, tte);
                }
                if (fechaPlanilla.before(fechaFeriado)){
                    agregarTTE(trabajadoresXDiasAnteriores, tte);
                }
                if (fechaPlanilla.after(fechaFeriado)){
                    agregarTTE(trabajadoresXDiasPosteriores, tte);
                }
                trabajadoresIncluidos.put(tte.getPersonal(), tte); //Guardo el ultimo tte del trabajador
            }
            if (fechaPlanilla.equals(fechaFeriado)){
                personalMismoDia.add(tte.getPersonal());
            }
        }
        
        //Evaluación de casos
        //Caso 1: Si un estibador trabaja en cualquiera de los 1 turno habil el día habil anterior al día del feriado y mete 1 jornal habil en los 5 días habiles posteriores al feriado
        for (Personal p : trabajadoresDiaAnterior.keySet()){
            if (trabajadoresXDiasPosteriores.containsKey(p) && trabajadoresXDiasPosteriores.get(p) >= 6){
                personalIncluido.add(p);
            }
        }
        
        //Caso 2: si el estibador en los 10 días hábiles previos al feriado tiene trabajadas 36 hs hábiles,
        for (Map.Entry<Personal, Integer> horasPersonal : trabajadoresXDiasAnteriores.entrySet()){
            if (horasPersonal.getValue() >= 36){
                personalIncluido.add(horasPersonal.getKey());
            }
        }
        
        //Eliminación de los trabajadores que trabajaron en el feriado
        for (Personal p : personalMismoDia){
            trabajadoresIncluidos.remove(p);
        }
        
        //Limpio el listado de todos los trabajadores que no estan incluidos en el feriado
        for (Map.Entry<Personal, TrabajadoresTurnoEmbarque> ttePersonal : trabajadoresIncluidos.entrySet()){
            if (!personalIncluido.contains(ttePersonal.getKey())){
                trabajadoresIncluidos.remove(ttePersonal.getKey());
            }
        }
        
        return trabajadoresIncluidos;
    }
    
    private void agregarTTE(Map<Personal, Integer> horasTrabajador, TrabajadoresTurnoEmbarque tte){
        if (horasTrabajador.containsKey(tte.getPersonal())){
            horasTrabajador.put(tte.getPersonal(), horasTrabajador.get(tte.getPersonal()) + tte.getHoras());
        } else {
            horasTrabajador.put(tte.getPersonal(), tte.getHoras());
        }
    }
    
    public List<TrabajadoresTurnoEmbarque> obtenerTTEsFeriados(Personal personal, Date desde, Date hasta){
        List<TrabajadoresTurnoEmbarque> ttesFeriados = new ArrayList<TrabajadoresTurnoEmbarque>();
        for (Feriado feriado : obtenerFeriados(desde, hasta)){
            DateTime feriadoDT = new DateTime(feriado.getFecha());
            boolean trabajoDiaAnterior = false;
            boolean trabajoMismoDia = false;
            int horasDiasAnteriores = 0;
            int horasDiasPosteriores = 0;
            TrabajadoresTurnoEmbarque ultimoTTE = null;
            
            DateTime diaAnterior = diaAnteriorFeriado(feriadoDT);
            DateTime diaInicioAnteriores = diasAnterioresFeriado(feriadoDT);
            DateTime diaFinPosteriores = diasPosterioresFeriado(feriadoDT);

            for (TrabajadoresTurnoEmbarque tte : tteF.getTrabajadoresPeriodo(personal, diaInicioAnteriores.toDate(), diaFinPosteriores.toDate())){
                Date fechaPlanilla = tte.getPlanilla().getFecha();
                //Para contar las horas, solamente tengo que contar las jornadas habiles, las otras no se tienen en cuenta
                if (tte.getPlanilla().getTipo().getId() == TipoJornal.HABIL){
                    if (fechaPlanilla.equals(diaAnterior.toDate())){
                        trabajoDiaAnterior = true;
                    }
                    if (fechaPlanilla.before(feriado.getFecha())){
                        horasDiasAnteriores += tte.getHoras();
                    }
                    if (fechaPlanilla.after(feriado.getFecha())){
                        horasDiasPosteriores += tte.getHoras();
                    }
                    ultimoTTE = tte;
                }
                if (fechaPlanilla.equals(feriado.getFecha())){
                    trabajoMismoDia = true;
                }
            }

            //Evaluación de casos
            if (!trabajoMismoDia) {
                //Caso 1: Si un estibador trabaja en cualquiera de los 1 turno habil el día habil anterior al día del feriado y mete 1 jornal habil en los 5 días habiles posteriores al feriado
                if (trabajoDiaAnterior && horasDiasPosteriores >= 6){
                    ttesFeriados.add(ultimoTTE);
                } else {
                    //Caso 2: si el estibador en los 10 días hábiles previos al feriado tiene trabajadas 36 hs hábiles,
                    if (horasDiasAnteriores >= 36){
                        ttesFeriados.add(ultimoTTE);
                    }
                }
            }
        }
        return ttesFeriados;
    }
    
    public List<Feriado> obtenerFeriados(Date desde, Date hasta){
        return getEntityManager().createNamedQuery("Feriado.findByFechaDesdeHasta", Feriado.class)
                .setParameter("fechaDesde", desde)
                .setParameter("fechaHasta", hasta)
                .getResultList();
    }

    private DateTime diaAnteriorFeriado(DateTime feriadoDT) {
        //Configuro los dias parametrizados q tengo
        DateTime diaAnterior = feriadoDT.minusDays(1);
        if (diaAnterior.getDayOfWeek() == DateTimeConstants.SUNDAY){
            diaAnterior = diaAnterior.minusDays(1);
        }
        return diaAnterior;
    }

    private DateTime diasAnterioresFeriado(DateTime feriadoDT) {
        DateTime diaInicioAnteriores = feriadoDT;
        for (int i = 0; i < DIAS_ANTERIORES_FERIADO; i++){
            diaInicioAnteriores = diaInicioAnteriores.minusDays(1);
            if (diaInicioAnteriores.getDayOfWeek() == DateTimeConstants.SUNDAY){
                diaInicioAnteriores = diaInicioAnteriores.minusDays(1);
            }
        }
        return diaInicioAnteriores;
    }

    private DateTime diasPosterioresFeriado(DateTime feriadoDT) {
        DateTime diaFinPosteriores = feriadoDT;
        for (int i = 0; i < DIAS_POSTERIORES_FERIADO; i++){
            diaFinPosteriores = diaFinPosteriores.plusDays(1);
            if (diaFinPosteriores.getDayOfWeek() == DateTimeConstants.SUNDAY){
                diaFinPosteriores = diaFinPosteriores.plusDays(1);
            }
        }
        return diaFinPosteriores;
    }
    
}
