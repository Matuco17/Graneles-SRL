/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.carga.TurnoEmbarque;
import com.orco.graneles.domain.miscelaneos.TipoJornal;
import com.orco.graneles.domain.miscelaneos.TipoValorConcepto;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.Feriado;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.carga.TrabajadoresTurnoEmbarqueFacade;
import com.orco.graneles.vo.TrabajadorTurnoEmbarqueVO;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
    @EJB
    private TipoJornalFacade tipoJornalF;
    @EJB
    private ConceptoReciboFacade conceptoReciboF;
    
    
    protected EntityManager getEntityManager() {
        return em;
    }

    public FeriadoFacade() {
        super(Feriado.class);
    }
    
    private static int DIAS_ANTERIORES_FERIADO = 10;
    private static int DIAS_POSTERIORES_FERIADO = 5;
    
    private String[] feriadosCache = null;
    private Date fechaFeriadoCache = null;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Metodo que calcula los trabajadores que corresponden cobrar el feriado
     * @param fechaFeriado
     * @return 
     */
    public Map<Personal, TrabajadoresTurnoEmbarque> obtenerTrabajadoresIncluidos(Date fechaFeriado){
        DateTime feriadoDT = new DateTime(fechaFeriado);
        cargarFeriadosCache(feriadoDT);
        Map<Personal, Integer> trabajadoresDiaAnterior = new HashMap<Personal, Integer>();
        Map<Personal, Integer> trabajadoresXDiasPosteriores = new HashMap<Personal, Integer>();
        Map<Personal, Integer> trabajadoresXDiasAnteriores = new HashMap<Personal, Integer>();
        Map<Personal, TrabajadoresTurnoEmbarque> trabajadores = new HashMap<Personal, TrabajadoresTurnoEmbarque>(); //Map que lo lleno con todos lso tte y luego elimino los q no pertenecen al feriado
        Map<Personal, TrabajadoresTurnoEmbarque> trabajadoresIncluidos = new HashMap<Personal, TrabajadoresTurnoEmbarque>(); //Map que lo lleno con todos lso tte y luego elimino los q no pertenecen al feriado
        Set<Personal> personalIncluido = new HashSet<Personal>();
        Set<Personal> personalMismoDia = new HashSet<Personal>();
        
        DateTime diaAnterior = diaAnteriorFeriado(feriadoDT);
        DateTime diaInicioAnteriores = diasAnterioresFeriado(feriadoDT, DIAS_ANTERIORES_FERIADO);
        DateTime diaFinPosteriores = diasPosterioresFeriado(feriadoDT, DIAS_POSTERIORES_FERIADO);
        
        for (TrabajadoresTurnoEmbarque tte : tteF.getTrabajadoresPeriodo(diaInicioAnteriores.toDate(), diaFinPosteriores.toDate())){
            Date fechaPlanilla = tte.getPlanilla().getFecha();
            //Para contar las horas, solamente tengo que contar las jornadas habiles, las otras no se tienen en cuenta
            if (tte.getPlanilla().getTipo().getConceptoRecibo().getTipoValor().getId() == TipoValorConcepto.HORAS_HABILES){
                if (fechaPlanilla.equals(diaAnterior.toDate())){
                    agregarTTE(trabajadoresDiaAnterior, tte);
                }
                if (fechaPlanilla.before(fechaFeriado)){
                    agregarTTE(trabajadoresXDiasAnteriores, tte);
                }
                if (fechaPlanilla.after(fechaFeriado)){
                    agregarTTE(trabajadoresXDiasPosteriores, tte);
                }
                trabajadores.put(tte.getPersonal(), tte); //Guardo el ultimo tte del trabajador
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
        personalIncluido.removeAll(personalMismoDia);
        
        //Limpio el listado de todos los trabajadores que no estan incluidos en el feriado
        for (Map.Entry<Personal, TrabajadoresTurnoEmbarque> ttePersonal : trabajadores.entrySet()){
            if (personalIncluido.contains(ttePersonal.getKey())){
                TrabajadoresTurnoEmbarque tteFeriado = crearTTEFeriadoFantasma(fechaFeriado, ttePersonal.getValue());
                
                trabajadoresIncluidos.put(ttePersonal.getKey(), tteFeriado);
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
            cargarFeriadosCache(feriadoDT);
            boolean trabajoDiaAnterior = false;
            boolean trabajoMismoDia = false;
            int horasDiasAnteriores = 0;
            int horasDiasPosteriores = 0;
            TrabajadoresTurnoEmbarque ultimoTTE = null;
            
            DateTime diaAnterior = diaAnteriorFeriado(feriadoDT);
            DateTime diaInicioAnteriores = diasAnterioresFeriado(feriadoDT, DIAS_ANTERIORES_FERIADO);
            DateTime diaFinPosteriores = diasPosterioresFeriado(feriadoDT, DIAS_POSTERIORES_FERIADO);

            for (TrabajadoresTurnoEmbarque tte : tteF.getTrabajadoresPeriodo(personal, diaInicioAnteriores.toDate(), diaFinPosteriores.toDate())){
                Date fechaPlanilla = tte.getPlanilla().getFecha();
                //Para contar las horas, solamente tengo que contar las jornadas habiles, las otras no se tienen en cuenta
                if (tte.getPlanilla().getTipo().getConceptoRecibo().getTipoValor().getId() == TipoValorConcepto.HORAS_HABILES){
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
                    ttesFeriados.add(crearTTEFeriadoFantasma(feriado.getFecha(), ultimoTTE));
                } else {
                    //Caso 2: si el estibador en los 10 días hábiles previos al feriado tiene trabajadas 36 hs hábiles,
                    if (horasDiasAnteriores >= 36){
                        ttesFeriados.add(crearTTEFeriadoFantasma(feriado.getFecha(), ultimoTTE));
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
        if (diaAnterior.getDayOfWeek() == DateTimeConstants.SUNDAY
            || Arrays.binarySearch(feriadosCache, df.format(diaAnterior.toDate())) >= 0){
            return diaAnteriorFeriado(diaAnterior);
        }
        return diaAnterior;
    }

    private DateTime diasAnterioresFeriado(DateTime feriadoDT, int diasAnterioresRestantes) {
        DateTime diaInicioAnteriores = feriadoDT.minusDays(1);
        if (diaInicioAnteriores.getDayOfWeek() == DateTimeConstants.SUNDAY
                || Arrays.binarySearch(feriadosCache, df.format(diaInicioAnteriores.toDate())) >= 0){
            return diasAnterioresFeriado(diaInicioAnteriores, diasAnterioresRestantes);
        } else {
            if (diasAnterioresRestantes > 1){
                return diasAnterioresFeriado(diaInicioAnteriores, diasAnterioresRestantes - 1);
            } else {
                return diaInicioAnteriores;
            }
        }
    }

    private DateTime diasPosterioresFeriado(DateTime feriadoDT, int diasPosterioresRestantes) {
        DateTime diaFinPosteriores = feriadoDT.plusDays(1);
        
        if (diaFinPosteriores.getDayOfWeek() == DateTimeConstants.SUNDAY
            || Arrays.binarySearch(feriadosCache, df.format(diaFinPosteriores.toDate())) >= 0){
            return diasPosterioresFeriado(diaFinPosteriores, diasPosterioresRestantes);
        } else {
            if (diasPosterioresRestantes > 1){
                return diasPosterioresFeriado(diaFinPosteriores, diasPosterioresRestantes - 1);
            } else {
                return diaFinPosteriores;
            }
        }
    }
    
    private void cargarFeriadosCache(DateTime feriado){
        if (feriadosCache == null) {
            List<Feriado> feriados = obtenerFeriados(feriado.minusYears(1).toDate(), feriado.plusYears(1).toDate());
            feriadosCache = new String[feriados.size()];
            for (int i = 0; i < feriados.size(); i++){
                feriadosCache[i] = df.format(feriados.get(i).getFecha());
            }
        } 
    }

    private TrabajadoresTurnoEmbarque crearTTEFeriadoFantasma(Date fechaFeriado, TrabajadoresTurnoEmbarque tte) {
        TurnoEmbarque teFeriado = new TurnoEmbarque();
        teFeriado.setTipo(tipoJornalF.find(com.orco.graneles.domain.salario.TipoJornal.BASICO));
        teFeriado.setFecha(fechaFeriado);
        getEntityManager().detach(teFeriado);
        TrabajadoresTurnoEmbarque tteFeriado = new TrabajadoresTurnoEmbarque();
        tteFeriado.setPersonal(tte.getPersonal());
        tteFeriado.setPlanilla(teFeriado);
        tteFeriado.setCategoria(tte.getCategoria());
        tteFeriado.setTarea(tte.getTarea());
        tteFeriado.setDelegado(Boolean.FALSE);
        tteFeriado.setDesde(tte.getDesde());
        tteFeriado.setHasta(tte.getHasta());
        TrabajadorTurnoEmbarqueVO tteVO = conceptoReciboF.calcularDiaTTE(tteFeriado, true);
        tteFeriado.setBruto(tteVO.getValorBruto());
        tteFeriado.setNeto(tteVO.getValorTurno());
        getEntityManager().detach(tteFeriado);
        return tteFeriado;
    }
    
}
