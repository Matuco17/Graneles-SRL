/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.miscelaneos.*;
import com.orco.graneles.domain.personal.Accidentado;
import com.orco.graneles.domain.personal.ObraSocial;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.personal.Sindicato;
import com.orco.graneles.domain.salario.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.carga.TrabajadoresTurnoEmbarqueFacade;
import com.orco.graneles.model.carga.TurnoEmbarqueFacade;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import com.orco.graneles.model.personal.AccidentadoFacade;
import com.orco.graneles.model.personal.PersonalFacade;
import com.orco.graneles.vo.AporteContribucionVO;
import com.orco.graneles.vo.CargaRegVO;
import com.orco.graneles.vo.ProyeccionSacVacYAdelantosVO;
import com.orco.graneles.vo.TurnoEmbarqueExcelVO;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.persistence.NoResultException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
/**
 *
 * @author orco
 */
@Stateless
public class PeriodoFacade extends AbstractFacade<Periodo> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    
    @EJB
    private PersonalFacade personalF;
    @EJB
    private TurnoEmbarqueFacade turnoEmbarqueF;
    @EJB
    private SueldoFacade sueldoF;
    @EJB
    private ItemsSueldoFacade itemSueldoF;
    @EJB
    private ConceptoReciboFacade conceptoReciboF;
    @EJB
    private TrabajadoresTurnoEmbarqueFacade trabTurnoEmbarqueF;
    @EJB
    private AccidentadoFacade accidentadoF;
    @EJB
    private FixedListFacade fixedListF;
    @EJB
    private AdelantoFacade adelantoF;
    @EJB
    private AporteConfiguracionConfiguracionFacade aporteContribucionConfigF;
    @EJB
    private FeriadoFacade feriadoF;

    protected void generarSueldosSACyVacaciones(Periodo periodo, Map<Long, Sueldo> sueldosCalculados, Map<Integer, List<ConceptoRecibo>> conceptosHoras) {
        //Recorro todos los sueldos y veo si tengo que calcularles el SAC y Vacaciones
        boolean calcularSac = periodoConSAC(periodo);
        
        for (Sueldo s : sueldosCalculados.values()){
            //Le calculo el SAC y Vac si es periodo de SAC y Vac o el tipo fue dado de baja en este periodo
            if (calcularSac || calcularSacIndividual(s.getPersonal(), periodo, null)){
                
                s = generarSACyVacacionesIndividual(periodo, s, conceptosHoras, true, false, true, true, null);
                
                sueldoF.persist(s);
            }
        }
        
        Logger.getLogger(PeriodoFacade.class.getName()).log(Level.INFO, null, "{" + (new Date()).toString() + "} " 
                    + "SAC y Vacaciones de los trabajadores involucrados en el periodo generado");
        System.out.println("{" + (new Date()).toString() + "} " 
                    + "SAC y Vacaciones de los trabajadores involucrados en el periodo generado");
        
        //Ahora solo falta encontrar los sueldos de todos los empleados que participaron en el semestre y no formaron parte del Mes en cuestion
        for(Personal p : personalF.findAll()){
            if (!sueldosCalculados.keySet().contains(p.getId())){
                if (calcularSac || calcularSacIndividual(p, periodo, null)){
                    Sueldo sueldoSacNuevo = new Sueldo();
                    sueldoSacNuevo.setPeriodo(periodo);
                    sueldoSacNuevo.setPersonal(p);
                    sueldoSacNuevo.setItemsSueldoCollection(new ArrayList<ItemsSueldo>());

                    sueldoSacNuevo = generarSACyVacacionesIndividual(periodo, sueldoSacNuevo, conceptosHoras, true, false, true, true, null);

                    if (sueldoSacNuevo.getItemsSueldoCollection() != null && sueldoSacNuevo.getItemsSueldoCollection().size() > 0){
                        sueldoF.create(sueldoSacNuevo);
                        periodo.getSueldoCollection().add(sueldoSacNuevo);
                    }  
                }
            }
        }
        
         Logger.getLogger(PeriodoFacade.class.getName()).log(Level.INFO, null, "{" + (new Date()).toString() + "} " 
                    + "SAC y Vacaciones de los trabajadores NO involucrados en el periodo generado");
         System.out.println("{" + (new Date()).toString() + "} " 
                    + "SAC y Vacaciones de los trabajadores NO involucrados en el periodo generado");
        
        
    }
    
    /**
     * Evalua si el personal debe tener calculado el SAC sin evaluar si esta en un periodo de SAC en cuestion
     * @param personal
     * @param periodo
     * @return 
     */
    public boolean calcularSacIndividual(Personal personal, Periodo periodo, Accidentado accidente){
        //TODO: falta agregar q 3es periodo con sac
        int mesPeriodo = (new DateTime(periodo.getDesde())).getMonthOfYear();
        boolean debeCalcularSAC = (mesPeriodo == DateTimeConstants.JUNE || mesPeriodo == DateTimeConstants.DECEMBER);
        
        //Si la persona no esta de baja y se dio justo de baja este mes.
        if (!debeCalcularSAC){
            debeCalcularSAC = (personal.getBaja() != null)
                    && personal.getBaja().after(periodo.getDesde())
                    && personal.getBaja().before(periodo.getHasta());
        }
        //Otro caso es si dejo de ser accidentado en este periodo
        if (!debeCalcularSAC){
            debeCalcularSAC = accidentadoF.finalizoAccidenteEnPeriodo(accidente, periodo);
        }
        /*
        if (!debeCalcularSAC){
            List<Accidentado> accidentes = accidentadoF.getAccidentadosPeriodoYPersonal(periodo.getDesde(), periodo.getHasta(), personal);
            for (Accidentado acc : accidentes){
                debeCalcularSAC = debeCalcularSAC || accidentadoF.finalizoAccidenteEnPeriodo(acc, periodo);
            }
        }
        */
        return debeCalcularSAC;
    }

    /**
     * Genera el Sac y las vacaciones para y lo Mergea con el sueldo pasado.
     * @param periodo
     * @param s
     * @param conceptosHoras
     * @return 
     */
    protected Sueldo generarSACyVacacionesIndividual(Periodo periodo, Sueldo s, Map<Integer, List<ConceptoRecibo>> conceptosHoras, boolean incluirHoras, boolean incluirAccidente, boolean incluirAdelanto, boolean incluirFeriado, Date hastaFijo) {
        Date desde = obtenerDesdeSAC(periodo, s.getPersonal());
        Date hasta = (hastaFijo == null)? obtenerHastaSAC(periodo, s.getPersonal()) : hastaFijo;
        switch (s.getPersonal().getTipoRecibo().getId()){
            case TipoRecibo.HORAS:

                Sueldo sueldoSAC = sueldoF.sueldoSAC(periodo, desde, hasta, s.getPersonal(), conceptosHoras, incluirHoras, incluirAccidente, incluirFeriado);
                Sueldo sueldoVacaciones = sueldoF.sueldoVacaciones(periodo, desde, hasta, s.getPersonal(), conceptosHoras, incluirHoras, incluirAccidente, incluirFeriado);
                
                if (sueldoSAC.getItemsSueldoCollection() != null && sueldoSAC.getItemsSueldoCollection().size() > 0){
                    s = sueldoF.mergeSueldos(s, sueldoSAC);
                }
                if (sueldoVacaciones.getItemsSueldoCollection() != null && sueldoVacaciones.getItemsSueldoCollection().size() > 0){
                    s = sueldoF.mergeSueldos(s, sueldoVacaciones);
                }
                
                if (incluirAdelanto) {
                    //Agrego los adelantos correspondientes a todo el periodo
                    sueldoF.agregarAdelanto(s, true, false);
                }
                
                break;
            case TipoRecibo.MENSUAL:
                //TODO: COMPLETAR
                
                break;
        }
        return s;
    }
    
    
    
    protected EntityManager getEntityManager() {
        return em;
    }

    public PeriodoFacade() {
        super(Periodo.class);
    }
    
     /**
     * Busca el período en cuestión, si no lo encuentra devuelve el período correcto pero todavía no guardado
     * @param anio anio del período
     * @param mes mes del período
     * @return periodo resulto
     */
    public Periodo verPeriodo(int anio, int mes){
        Periodo per = null;
        
        //Extraigo el dia Desde y el día Hasta
        Calendar calDesde = new GregorianCalendar();
        calDesde.set(Calendar.YEAR, anio);
        calDesde.set(Calendar.MONTH, mes -1);
        calDesde.set(Calendar.DAY_OF_MONTH, 1);
      
        Calendar calHasta = new GregorianCalendar();
        calHasta.set(Calendar.YEAR, anio);
        calHasta.set(Calendar.MONTH, mes -1);
        calHasta.set(Calendar.DAY_OF_MONTH, calHasta.getActualMaximum(Calendar.DAY_OF_MONTH));
        
        //Busco el período, si no lo encuentro entonces lo dejo en nulo
        try {
          per = getEntityManager().createNamedQuery("Periodo.findByDesdeHasta", Periodo.class)
                  .setParameter("desde", calDesde.getTime())
                  .setParameter("hasta", calHasta.getTime())
                  .getSingleResult();
        } catch (NoResultException e) {
          per = null;
        }
        
               
        //Si es nulo entonces devuelvo el periodo seleccionado nuevo para ser completado
        if (per == null){
            per = new Periodo();
            per.setDescripcion(String.valueOf(anio) + "-" + String.valueOf(mes));
            per.setDesde(calDesde.getTime());
            per.setHasta(calHasta.getTime());
            //TODO: Falta obtener el nro de Tomo (libro) y la hoja desde el historial
        }
        return per;
    }
    
    public List<Sueldo> obtenerSueldosSacYVac(Periodo periodo){
        List<Sueldo> sueldos = new ArrayList<Sueldo>();
        
        boolean calcularSac = periodoConSAC(periodo);
        Map<Integer, List<ConceptoRecibo>> conceptosHoras = conceptoReciboF.obtenerConceptosXTipoRecibo(fixedListF.find(TipoRecibo.HORAS));
                
        for (Personal p : personalF.findAll()){
            //Por ahora salteo los empleados mensuales
            if (p.getTipoRecibo().getId() == TipoRecibo.HORAS){
                if (calcularSac || calcularSacIndividual(p, periodo, null)){
                    Sueldo sueldoSacNuevo = new Sueldo();
                    sueldoSacNuevo.setPeriodo(periodo);
                    sueldoSacNuevo.setPersonal(p);
                    sueldoSacNuevo.setItemsSueldoCollection(new ArrayList<ItemsSueldo>());

                    sueldoSacNuevo = generarSACyVacacionesIndividual(periodo, sueldoSacNuevo, conceptosHoras, true, false , true, true, null);
                    
                    sueldos.add(sueldoSacNuevo);
               }
            }
        }
        return sueldos;
    }
    
    
    public List<Sueldo> obtenerSueldosAccidentadosSacYVac(Periodo periodo){
        List<Sueldo> sueldos = new ArrayList<Sueldo>();
        
        Map<Integer, List<ConceptoRecibo>> conceptosHoras = conceptoReciboF.obtenerConceptosXTipoRecibo(fixedListF.find(TipoRecibo.HORAS));
                
        for (Personal p : personalF.findAll()){
            //Por ahora salteo los empleados mensuales
            if (p.getTipoRecibo().getId() == TipoRecibo.HORAS){
                    Sueldo sueldoSacNuevo = new Sueldo();
                    sueldoSacNuevo.setPeriodo(periodo);
                    sueldoSacNuevo.setPersonal(p);
                    sueldoSacNuevo.setItemsSueldoCollection(new ArrayList<ItemsSueldo>());

                    //TODO: CAMBIAR el ultimo por true
                    sueldoSacNuevo = generarSACyVacacionesIndividual(periodo, sueldoSacNuevo, conceptosHoras, false, true, false, true, periodo.getHasta());
                    
                    if (sueldoSacNuevo.getItemsSueldoCollection() != null && sueldoSacNuevo.getItemsSueldoCollection().size() > 0){
                        sueldos.add(sueldoSacNuevo);    
                    } 
            }
        }
        return sueldos;
    }
      
    public List<ProyeccionSacVacYAdelantosVO> obtenerProyecciones(int semestre, int anio){
        DateTime desde = null;
        DateTime hasta = null;
        Map<Long, ProyeccionSacVacYAdelantosVO> proyecciones = new HashMap<Long, ProyeccionSacVacYAdelantosVO>();
        
        if (semestre == 1){
            desde = new DateTime(anio, DateTimeConstants.JANUARY, 1, 0, 0);
            hasta = new DateTime(anio, DateTimeConstants.JUNE, 30, 23, 59);
        } else {
            desde = new DateTime(anio, DateTimeConstants.JULY, 1, 0, 0);
            hasta = new DateTime(anio, DateTimeConstants.DECEMBER, 31, 23, 59);
        }
        
        Map<Integer, List<ConceptoRecibo>> conceptosHoras = conceptoReciboF.obtenerConceptosXTipoRecibo(fixedListF.find(TipoRecibo.HORAS));
        
        for (Personal p : personalF.findAll()){
            //Por ahora salteo los empleados mensuales
            if (p.getTipoRecibo().getId() == TipoRecibo.HORAS){
                Sueldo sueldoSACyVac = sueldoF.sueldoSAC(null, desde.toDate(), hasta.toDate(), p, conceptosHoras, true, false, true);
                sueldoSACyVac = sueldoF.mergeSueldos(sueldoSACyVac, sueldoF.sueldoVacaciones(null, desde.toDate(), hasta.toDate(), p, conceptosHoras, true, false, true));

                ProyeccionSacVacYAdelantosVO currentProyeccion = new ProyeccionSacVacYAdelantosVO(p);

                for (ItemsSueldo is : sueldoSACyVac.getItemsSueldoCollection()){
                    switch (is.getConceptoRecibo().getTipo().getId()){
                        case TipoConceptoRecibo.REMUNERATIVO:
                            currentProyeccion.setProyeccionBruto(currentProyeccion.getProyeccionBruto().add(is.getValorCalculado()));
                            currentProyeccion.setProyeccionNeto(currentProyeccion.getProyeccionNeto().add(is.getValorCalculado()));
                            break;
                        case TipoConceptoRecibo.DEDUCTIVO:
                            currentProyeccion.setProyeccionNeto(currentProyeccion.getProyeccionNeto().subtract(is.getValorCalculado()));
                            break;
                        case TipoConceptoRecibo.NO_REMUNERATIVO:
                            currentProyeccion.setProyeccionNeto(currentProyeccion.getProyeccionNeto().add(is.getValorCalculado()));
                            break;
                    }
                }

                if (currentProyeccion.getProyeccionBruto().doubleValue() > 0){
                    currentProyeccion.setProyeccionNetoConAdelantos(BigDecimal.ZERO.add(currentProyeccion.getProyeccionNeto()));
                    proyecciones.put(p.getId(), currentProyeccion);
                }
            }
        }
        
        //Completo los adelantos en cada fila
        for (Adelanto a : adelantoF.obtenerAdelantos(desde.toDate(), hasta.toDate())){
            ProyeccionSacVacYAdelantosVO proyeccion = proyecciones.get(a.getPersonal().getId());
            if (proyeccion == null){
                proyeccion = new ProyeccionSacVacYAdelantosVO(a.getPersonal());
            }
            
            proyeccion.setTotalAdelantos(proyeccion.getTotalAdelantos().add(a.getValor()));
            proyeccion.setProyeccionNetoConAdelantos(proyeccion.getProyeccionNetoConAdelantos().subtract(a.getValor()));
            
            proyecciones.put(a.getPersonal().getId(), proyeccion);
        }
        
        //Completo el ultimo TTE
        List<TrabajadoresTurnoEmbarque> ttes = trabTurnoEmbarqueF.getTrabajadoresPeriodo(desde.toDate(), hasta.toDate());
        for (TrabajadoresTurnoEmbarque tte : ttes){
            ProyeccionSacVacYAdelantosVO proyeccion = proyecciones.get(tte.getPersonal().getId());
            proyeccion.setUltimoTTE(tte);
        }
        
        List<ProyeccionSacVacYAdelantosVO> result = new ArrayList<ProyeccionSacVacYAdelantosVO>(proyecciones.values());
        Collections.sort(result);
        
        return result;
    }
    
    /**
     * Metodo que obtiene el primer día del periodo semestral en que se encunetra el sistema
     * @return 
     */
    public Date obtenerFechaInicioPeriodoSemestralActual(){
        return obtenerFechaInicioPeriodoSemestral(new Date());
    }
   
      /**
     * Obtiene la fecha inicio de un semestre de acuerdo a la fecha de periodo pasado
     * @param fecha
     * @return 
     */
    public Date obtenerFechaInicioPeriodoSemestral(Date fecha){
        DateTime dtFecha = new DateTime(fecha);
        DateTime dtInicio = null;
        
        if (dtFecha.getMonthOfYear() <= DateTimeConstants.JUNE){
            dtInicio = new DateTime(dtFecha.getYear(), DateTimeConstants.JANUARY, 1, 0, 0);
        } else {
            dtInicio = new DateTime(dtFecha.getYear(), DateTimeConstants.JULY, 1, 0, 0);
        }
        
        return dtInicio.toDate();
    }
    
    /**
     * Obtiene la fecha fin de un semestre de acuerdo a la fecha de periodo pasado
     * @param fecha
     * @return 
     */
    public Date obtenerFechaFinPeriodoSemestral(Date fecha){
        DateTime dtFecha = new DateTime(fecha);
        DateTime dtInicio = null;
        
        if (dtFecha.getMonthOfYear() <= DateTimeConstants.JUNE){
            dtInicio = new DateTime(dtFecha.getYear(), DateTimeConstants.JUNE, 30, 0, 0);
        } else {
            dtInicio = new DateTime(dtFecha.getYear(), DateTimeConstants.DECEMBER, 31, 0, 0);
        }
        
        return dtInicio.toDate();
    }
    
    
    private Map<Long, Sueldo> generarSueldosTTE(Periodo periodo, Map<Integer, List<ConceptoRecibo>> conceptosHoras){
        List<TrabajadoresTurnoEmbarque> listaTTE = trabTurnoEmbarqueF.getTrabajadoresPeriodo(periodo);
        Map<Long, Sueldo> mapSueldosXIdPers = new HashMap<Long, Sueldo>();
        
        //Por cada uno de los turnos trabajados realizo las operaciones
        for(TrabajadoresTurnoEmbarque tte : listaTTE){
            Sueldo sueldoTTE = sueldoF.calcularSueldoTTE(periodo, tte, conceptosHoras);
            
            //Hago el merge de sueldos y realizo la actualizacion del TTE para que quede registrado que tiene sueldo asignado
            Sueldo sueldoTTEAnterior = mapSueldosXIdPers.get(tte.getPersonal().getId());
            if (sueldoTTEAnterior != null){
                mapSueldosXIdPers.put(tte.getPersonal().getId(), sueldoF.mergeSueldos(sueldoTTEAnterior, sueldoTTE));
               
                sueldoF.edit(sueldoTTEAnterior);
                trabTurnoEmbarqueF.edit(tte);                
            } else {
                mapSueldosXIdPers.put(tte.getPersonal().getId(), sueldoTTE);
               
                sueldoF.create(sueldoTTE);
                periodo.getSueldoCollection().add(sueldoTTE);
                trabTurnoEmbarqueF.edit(tte);                
            }
            
        }
        
        return mapSueldosXIdPers;
    }
    
    private Map<Long, Sueldo> generarSueldosFeriados(Periodo periodo, Map<Long, Sueldo> mapSueldoCreados, Map<Integer, List<ConceptoRecibo>> conceptosHoras){
        List<Feriado> listaFeriados = feriadoF.obtenerFeriados(periodo.getDesde(), periodo.getHasta());
        
        for (Feriado feriado : listaFeriados){
            Collection<TrabajadoresTurnoEmbarque> ttesFeriado = feriadoF.obtenerTrabajadoresIncluidos(feriado.getFecha()).values();
            
            for (TrabajadoresTurnoEmbarque tteFeriado : ttesFeriado){
                Sueldo sueldoFeriado = sueldoF.calcularSueldoFeriado(tteFeriado, periodo, conceptosHoras);
                                
                //Hago el merge de sueldos y realizo la actualizacion del TTE para que quede registrado que tiene sueldo asignado
                Sueldo sueldoCreadoAnterior = mapSueldoCreados.get(tteFeriado.getPersonal().getId());
                if (sueldoCreadoAnterior != null){
                    mapSueldoCreados.put(tteFeriado.getPersonal().getId(), sueldoF.mergeSueldos(sueldoCreadoAnterior, sueldoFeriado));

                    sueldoF.edit(sueldoCreadoAnterior);
                } else {
                    mapSueldoCreados.put(tteFeriado.getPersonal().getId(), sueldoFeriado);

                    sueldoF.create(sueldoFeriado);
                    periodo.getSueldoCollection().add(sueldoFeriado);
                }                
            }
        }
        
        
        return mapSueldoCreados;
    }
    
    private Map<Long, Sueldo> generarSueldosAccidentados(Periodo periodo, Map<Long, Sueldo> mapSueldoCreados, Map<Integer, List<ConceptoRecibo>> conceptosHoras){
        List<Accidentado> listaAcc = accidentadoF.getAccidentadosPeriodo(periodo.getDesde(), periodo.getHasta());
        
        
        for (Accidentado acc : listaAcc){
            Sueldo sueldoAcc = sueldoF.calcularSueldoAccidentado(periodo, acc, conceptosHoras, true, true);
            
            //Hago el merge de sueldos y realizo la actualizacion del TTE para que quede registrado que tiene sueldo asignado
            Sueldo sueldoCreadoAnterior = mapSueldoCreados.get(acc.getPersonal().getId());
            if (sueldoCreadoAnterior != null){
                mapSueldoCreados.put(acc.getPersonal().getId(), sueldoF.mergeSueldos(sueldoCreadoAnterior, sueldoAcc));
               
                sueldoF.edit(sueldoCreadoAnterior);
            } else {
                mapSueldoCreados.put(acc.getPersonal().getId(), sueldoAcc);
               
                sueldoF.create(sueldoAcc);
                periodo.getSueldoCollection().add(sueldoAcc);
            }
        }
       
        return mapSueldoCreados;
    }
    
    public List<Sueldo> obtenerSueldosAccidentados(Periodo periodo, boolean incluirSacYVac, boolean incluirAdelanto){
        List<Accidentado> listaAcc = accidentadoF.getAccidentadosPeriodo(periodo.getDesde(), periodo.getHasta());
        
        Map<Integer, List<ConceptoRecibo>> conceptosHoras = conceptoReciboF.obtenerConceptosXTipoRecibo(fixedListF.find(TipoRecibo.HORAS));
        
        Map<Long, Sueldo> mapSueldoCreados = new HashMap<Long, Sueldo>();
                
        for (Accidentado acc : listaAcc){
            Sueldo sueldoAcc = sueldoF.calcularSueldoAccidentado(periodo, acc, conceptosHoras, incluirSacYVac, incluirAdelanto);
            
            //Hago el merge de sueldos y realizo la actualizacion del TTE para que quede registrado que tiene sueldo asignado
            Sueldo sueldoCreadoAnterior = mapSueldoCreados.get(acc.getPersonal().getId());
            if (sueldoCreadoAnterior != null){
                mapSueldoCreados.put(acc.getPersonal().getId(), sueldoF.mergeSueldos(sueldoCreadoAnterior, sueldoAcc));
            } else {
                mapSueldoCreados.put(acc.getPersonal().getId(), sueldoAcc);
            }
        }
       
        return new ArrayList<Sueldo>(mapSueldoCreados.values());
    }
    /*
    public List<Sueldo> obtenerSueldosAccidentadosSacYVac(Periodo periodo){
        List<Accidentado> listaAcc = accidentadoF.getAccidentadosPeriodo(periodo.getDesde(), periodo.getHasta());
        
        Map<Integer, List<ConceptoRecibo>> conceptosHoras = conceptoReciboF.obtenerConceptosXTipoRecibo(fixedListF.find(TipoRecibo.HORAS));
        
        Map<Long, Sueldo> mapSueldoCreados = new HashMap<Long, Sueldo>();
                
        for (Accidentado acc : listaAcc){
            Sueldo sueldoAcc = sueldoF.calcularSueldoAccidentado(periodo, acc, conceptosHoras, true, true);
            
            //Hago el merge de sueldos y realizo la actualizacion del TTE para que quede registrado que tiene sueldo asignado
            Sueldo sueldoCreadoAnterior = mapSueldoCreados.get(acc.getPersonal().getId());
            if (sueldoCreadoAnterior != null){
                mapSueldoCreados.put(acc.getPersonal().getId(), sueldoF.mergeSueldos(sueldoCreadoAnterior, sueldoAcc));
            } else {
                mapSueldoCreados.put(acc.getPersonal().getId(), sueldoAcc);
            }
        }
       
        return new ArrayList<Sueldo>(mapSueldoCreados.values());
    }
    */
    
    private Collection<Sueldo> generarSueldosMensuales(Periodo periodo){
        //List<Personal> listaMens = personalF.getPersonalMensualActivo();
  
        /*
          Para cada Mensual 
    Por cada Mensual ver cuanto le correspodne de remunerativo total y despues
	buscar todas las deducciones
        buscar todos los no remunerativos
    generar los valores del sueldo, y likearlo al Acc
    guardar
    * 
    */
      return new ArrayList<Sueldo>();  
    }
    
    /**
     * Genera los sueldos del periodo seleccionado
     * @param periodo 
     */
    public void generarSueldosPeriodo(Periodo periodo){
            
        Logger.getLogger(PeriodoFacade.class.getName()).log(Level.SEVERE, null, "{" + (new Date()).toString() + "} " 
                + "Inicio generacion de sueldos Periodo: " + periodo.getId() + " - " + periodo.getDescripcion());
        System.out.println("{" + (new Date()).toString() + "} " 
              + "Inicio generacion de sueldos Periodo: " + periodo.getId() + " - " + periodo.getDescripcion());

        Map<Integer, List<ConceptoRecibo>> conceptosHoras = conceptoReciboF.obtenerConceptosXTipoRecibo(fixedListF.find(TipoRecibo.HORAS));

        Logger.getLogger(PeriodoFacade.class.getName()).log(Level.SEVERE, null, "{" + (new Date()).toString() + "} " 
                + "Levanto los conceptos de horas");
        System.out.println("{" + (new Date()).toString() + "} " 
                + "Levanto los conceptos de horas");

        //Debo setear todos las relaciones con sueldos para remover las FK que compliquen sobre elementos a no borrar (y si existen otras entidades)
        if (periodo.getSueldoCollection() != null){
            getEntityManager().flush();
        }

        //limpio la lista de sueldos del periodo carga ya que se carga nuevamente y tiene que ser una operacion idempotente
        periodo.setSueldoCollection(new ArrayList<Sueldo>());


        //Persisto el periodo
        persist(periodo);

        getEntityManager().flush();


        Logger.getLogger(PeriodoFacade.class.getName()).log(Level.SEVERE, null, "{" + (new Date()).toString() + "} " 
                + "Reseteada del periodo");
        System.out.println("{" + (new Date()).toString() + "} " 
                + "Reseteada del periodo");


        Map<Long, Sueldo> sueldosCalculados = generarSueldosTTE(periodo, conceptosHoras);

        Logger.getLogger(PeriodoFacade.class.getName()).log(Level.SEVERE, null, "{" + (new Date()).toString() + "} " 
                + "Sueldos de los Trabajadores de Turno generado");
        System.out.println("{" + (new Date()).toString() + "} " 
                + "Sueldos de los Trabajadores de Turno generado");

        sueldosCalculados = generarSueldosFeriados(periodo, sueldosCalculados, conceptosHoras);
        
        Logger.getLogger(PeriodoFacade.class.getName()).log(Level.SEVERE, null, "{" + (new Date()).toString() + "} " 
                + "Sueldos de Feriados generado");
        System.out.println("{" + (new Date()).toString() + "} " 
                + "Sueldos de Feriados generado");

        
        sueldosCalculados = generarSueldosAccidentados(periodo, sueldosCalculados, conceptosHoras);

        Logger.getLogger(PeriodoFacade.class.getName()).log(Level.SEVERE, null, "{" + (new Date()).toString() + "} " 
                + "Sueldos de los Accidentados generado");
        System.out.println("{" + (new Date()).toString() + "} " 
                + "Sueldos de los Accidentados generado");

        //TODO: FALTA GENERAR LOS SUELDOS MENSUALES

        generarSueldosSACyVacaciones(periodo, sueldosCalculados, conceptosHoras);

        Logger.getLogger(PeriodoFacade.class.getName()).log(Level.SEVERE, null, "{" + (new Date()).toString() + "} " 
                + "Sac y Vacaciones Generado");
        System.out.println("{" + (new Date()).toString() + "} " 
                + "Sac y Vacaciones Generado");


        persist(periodo);  

        getEntityManager().flush();

        //TODO: REALIZAR OTRAS MODIFICACIONES A OTRAS ENTIDADES QUE NO TENGAN QUE VER DIRECTAMENTE CON EL PERIODO PERO QUE AL CERRARSE SE BLOQUEAN
        Logger.getLogger(PeriodoFacade.class.getName()).log(Level.SEVERE, null, "{" + (new Date()).toString() + "} " 
                + "Fin del Proceso de Generacion de sueldos");
        System.out.println("{" + (new Date()).toString() + "} " 
                + "Fin del Proceso de Generacion de sueldos");
            
    }
    
    public Date obtenerDesdeSAC(Periodo periodo, Personal personal){
        DateTime desdePeriodo = new DateTime(periodo.getDesde());
        DateTime resultDesde = null;
        if (desdePeriodo.getMonthOfYear() <= DateTimeConstants.JUNE){
            resultDesde = new DateTime(desdePeriodo.getYear(), DateTimeConstants.JANUARY, 1, 0, 0);
        } else {
            resultDesde = new DateTime(desdePeriodo.getYear(), DateTimeConstants.JULY, 1, 0, 0);
        }
        
        //Si el tipo ingreso despúes del periodo, entonces lo tomo desde ese momento
        if (personal.getIngreso() != null && periodo.getDesde().before(personal.getIngreso())){
            resultDesde = new DateTime(personal.getIngreso());
        }
        
        return resultDesde.toDate();
    }
    
    public Date obtenerHastaSAC(Periodo periodo, Personal personal){
        DateTime hastaPeriodo = new DateTime(periodo.getDesde());
        DateTime resultHasta = null;
        if (hastaPeriodo.getMonthOfYear() <= DateTimeConstants.JUNE){
            resultHasta = new DateTime(hastaPeriodo.getYear(), DateTimeConstants.JUNE, 30, 23, 59);
        } else {
            resultHasta = new DateTime(hastaPeriodo.getYear(), DateTimeConstants.DECEMBER, 31, 23, 59);
        }
        
        //Si el tipo ingreso despúes del periodo, entonces lo tomo desde ese momento
        if (personal.getBaja() != null
            && personal.getBaja().before(periodo.getHasta())){
            resultHasta = new DateTime(personal.getBaja());
        }
        
        return resultHasta.toDate();
    }
    
    
    
    public boolean periodoConSAC(Periodo periodo){
        DateTime desde = new DateTime(periodo.getDesde());
        
        return (desde.getMonthOfYear() == DateTimeConstants.JUNE 
            || desde.getMonthOfYear() == DateTimeConstants.DECEMBER);
    }

    private static final String AC_GRUPO_SEG_SOCIAL = "Seguridad Social";
    private static final String AC_GRUPO_OBRA_SOCIAL = "Obras Sociales";
    private static final String AC_GRUPO_SINDICATO = "Sindicatos";
    
    
    public List<AporteContribucionVO> generarReporteAportesYContribuciones(Periodo periodo){
        List<AporteContribucionVO> acVO = new ArrayList<AporteContribucionVO>();
        
        //Seccion 1: Total de remuneraciones (bruto)
        BigDecimal totalBruto = BigDecimal.ZERO;
        for (Sueldo s : periodo.getSueldoCollection()){
            for (ItemsSueldo is : s.getItemsSueldoCollection()){
                if (is.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.REMUNERATIVO)){
                    totalBruto = totalBruto.add(is.getValorCalculado());
                }                   
            }
        }
        AporteContribucionVO aporteRemuneracion = new AporteContribucionVO(1, null, "Remuneraciones");
        aporteRemuneracion.setAporte(totalBruto);
        aporteRemuneracion.setPeriodo(periodo);
        acVO.add(aporteRemuneracion);
        
        //Seccion 2: 
        //Grupo Seguridad Social : Deducciones (Aportes y Contribuciones) Sin obra social
        //Grupo Obras Sociales : Aportes y contribuciones obras sociales
        //Grupo Sindicatos : Aportes de los sindicatos
        
        //Seguridad Social
        List<AporteContribucionConfiguracion> acConfigs = aporteContribucionConfigF.findAll();
        Collections.sort(acConfigs);
        for (AporteContribucionConfiguracion acc : acConfigs){
            AporteContribucionVO acVOSS = new AporteContribucionVO(2, AC_GRUPO_SEG_SOCIAL, acc.getTipoValor().getDescripcion());
            if (acc.getAporte() != null){
                acVOSS.setAporte(totalBruto.multiply(acc.getAporte().divide(new BigDecimal(100))));
            }
            if (acc.getContribucion() != null){
                acVOSS.setContribucion(totalBruto.multiply(acc.getContribucion().divide(new BigDecimal(100))));
            }
            acVO.add(acVOSS);
        }
        
        
        //Obras sociales y sindicatos
        Map<Integer, AporteContribucionVO> acVOObraSocial = new HashMap<Integer, AporteContribucionVO>();
        Map<Integer, AporteContribucionVO> acVOSindicato = new HashMap<Integer, AporteContribucionVO>();
        for (Sueldo s : periodo.getSueldoCollection()){
            for (ItemsSueldo is : s.getItemsSueldoCollection()){
                if (is.getConceptoRecibo().getTipo().getId().equals(TipoConceptoRecibo.DEDUCTIVO)){
                    switch (is.getConceptoRecibo().getTipoValor().getId()){
                        case TipoValorConcepto.OBRA_SOCIAL :
                            ObraSocial osPersona = is.getSueldo().getPersonal().getObraSocial();
                            
                            AporteContribucionVO acVOOSPersona = acVOObraSocial.get(osPersona.getId());
                            if (acVOOSPersona == null){
                                acVOOSPersona = new AporteContribucionVO(2, AC_GRUPO_OBRA_SOCIAL, osPersona.getDescripcion());
                            }
                            acVOOSPersona.setAporte(acVOOSPersona.getAporte().add(is.getValorCalculado()));
                            acVOOSPersona.setContribucion(new BigDecimal(
                                    acVOOSPersona.getContribucion().doubleValue() + 
                                    (is.getValorCalculado().doubleValue() * osPersona.getContribucion().doubleValue() / osPersona.getAportes().doubleValue())
                                    ));
                            acVOObraSocial.put(osPersona.getId(), acVOOSPersona);                            
                            break;
                        case TipoValorConcepto.SINDICATO :
                            Sindicato sindPersona = is.getSueldo().getPersonal().getCategoriaPrincipal().getSindicato();
                            
                            AporteContribucionVO acVOSindPersona = acVOSindicato.get(sindPersona.getId());
                            if (acVOSindPersona == null){
                                acVOSindPersona = new AporteContribucionVO(2, AC_GRUPO_SINDICATO, sindPersona.getDescripcion());
                            }
                            acVOSindPersona.setAporte(acVOSindPersona.getAporte().add(is.getValorCalculado()));
                          
                            acVOSindicato.put(sindPersona.getId(), acVOSindPersona); 
                            
                            break;                            
                    }
                }                   
            }
        }
        
        acVO.addAll(acVOObraSocial.values());
        acVO.addAll(acVOSindicato.values());
        
        return acVO;
    }
    
    
    
}
