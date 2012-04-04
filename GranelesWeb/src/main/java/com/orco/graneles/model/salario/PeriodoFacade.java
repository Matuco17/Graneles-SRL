/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.miscelaneos.*;
import com.orco.graneles.domain.personal.Accidentado;
import com.orco.graneles.domain.personal.Personal;
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

    protected void generarSueldosSACyVacaciones(Periodo periodo, Map<Long, Sueldo> sueldosCalculados, Map<Integer, List<ConceptoRecibo>> conceptosHoras) {
        //Recorro todos los sueldos y veo si tengo que calcularles el SAC y Vacaciones
        boolean calcularSac = periodoConSAC(periodo);
        
        for (Sueldo s : sueldosCalculados.values()){
            //Le calculo el SAC y Vac si es periodo de SAC y Vac o el tipo fue dado de baja en este periodo
            if (calcularSac || calcularSacIndividual(s.getPersonal(), periodo)){
                
                s = generarSACyVacacionesIndividual(periodo, s, conceptosHoras);
                
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
                if (calcularSac || calcularSacIndividual(p, periodo)){
                    Sueldo sueldoSacNuevo = new Sueldo();
                    sueldoSacNuevo.setPeriodo(periodo);
                    sueldoSacNuevo.setPersonal(p);
                    sueldoSacNuevo.setItemsSueldoCollection(new ArrayList<ItemsSueldo>());

                    sueldoSacNuevo = generarSACyVacacionesIndividual(periodo, sueldoSacNuevo, conceptosHoras);

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
    protected boolean calcularSacIndividual(Personal personal, Periodo periodo){
        return (personal.getBaja() != null)
                    && personal.getBaja().after(periodo.getDesde())
                    && personal.getBaja().before(periodo.getHasta());
    }

    /**
     * Genera el Sac y las vacaciones para y lo Mergea con el sueldo pasado.
     * @param periodo
     * @param s
     * @param conceptosHoras
     * @return 
     */
    protected Sueldo generarSACyVacacionesIndividual(Periodo periodo, Sueldo s, Map<Integer, List<ConceptoRecibo>> conceptosHoras) {
        Date desde = obtenerDesdeSAC(periodo, s.getPersonal());
        Date hasta = obtenerHastaSAC(periodo, s.getPersonal());
        switch (s.getPersonal().getTipoRecibo().getId()){
            case TipoRecibo.HORAS:
                Sueldo sueldoSAC = sueldoF.sueldoSAC(periodo, desde, hasta, s.getPersonal(), conceptosHoras);
                Sueldo sueldoVacaciones = sueldoF.sueldoVacaciones(periodo, desde, hasta, s.getPersonal(), conceptosHoras);
                
                if (sueldoSAC.getItemsSueldoCollection() != null && sueldoSAC.getItemsSueldoCollection().size() > 0){
                    s = sueldoF.mergeSueldos(s, sueldoSAC);
                }
                if (sueldoVacaciones.getItemsSueldoCollection() != null && sueldoVacaciones.getItemsSueldoCollection().size() > 0){
                    s = sueldoF.mergeSueldos(s, sueldoVacaciones);
                }
                
                //TODO: AQUI AGREGO LOS CONCEPTOS DE ADELANTOS DE AGUINALDOS
                
                
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
                if (calcularSac || calcularSacIndividual(p, periodo)){
                    Sueldo sueldoSacNuevo = new Sueldo();
                    sueldoSacNuevo.setPeriodo(periodo);
                    sueldoSacNuevo.setPersonal(p);
                    sueldoSacNuevo.setItemsSueldoCollection(new ArrayList<ItemsSueldo>());

                    sueldoSacNuevo = generarSACyVacacionesIndividual(periodo, sueldoSacNuevo, conceptosHoras);
                    
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
                Sueldo sueldoSACyVac = sueldoF.sueldoSAC(null, desde.toDate(), hasta.toDate(), p, conceptosHoras);
                sueldoSACyVac = sueldoF.mergeSueldos(sueldoSACyVac, sueldoF.sueldoVacaciones(null, desde.toDate(), hasta.toDate(), p, conceptosHoras));

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
            
            proyecciones.put(a.getPersonal().getId(), proyeccion);
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
        Calendar calHoy = new GregorianCalendar();
        Calendar calInicio = new GregorianCalendar();
        
        calInicio.set(Calendar.DAY_OF_MONTH, 1);
        calInicio.set(Calendar.HOUR_OF_DAY, 0);
        calInicio.set(Calendar.MINUTE, 0);
        calInicio.set(Calendar.SECOND, 0);
        calInicio.set(Calendar.MILLISECOND, 0);
        if (calHoy.get(Calendar.MONTH) <= Calendar.JUNE){
            calInicio.set(Calendar.MONTH, Calendar.JANUARY);
        } else {
            calInicio.set(Calendar.MONTH, Calendar.JULY);
        }
        
        return calInicio.getTime();
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
                tte.setLibroSueldo(sueldoTTEAnterior);
                trabTurnoEmbarqueF.edit(tte);                
            } else {
                mapSueldosXIdPers.put(tte.getPersonal().getId(), sueldoTTE);
               
                sueldoF.create(sueldoTTE);
                tte.setLibroSueldo(sueldoTTE);
                periodo.getSueldoCollection().add(sueldoTTE);
                trabTurnoEmbarqueF.edit(tte);                
            }
            
        }
        
        return mapSueldosXIdPers;
    }
    
    private Map<Long, Sueldo> generarSueldosAccidentados(Periodo periodo, Map<Long, Sueldo> mapSueldoCreados, Map<Integer, List<ConceptoRecibo>> conceptosHoras){
        List<Accidentado> listaAcc = accidentadoF.getAccidentadosPeriodo(periodo.getDesde(), periodo.getHasta());
        
        
        for (Accidentado acc : listaAcc){
            Sueldo sueldoAcc = sueldoF.calcularSueldoAccidentado(periodo, acc, conceptosHoras);
            
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
    
    public List<Sueldo> obtenerSueldosAccidentados(Periodo periodo){
        List<Accidentado> listaAcc = accidentadoF.getAccidentadosPeriodo(periodo.getDesde(), periodo.getHasta());
        
        Map<Integer, List<ConceptoRecibo>> conceptosHoras = conceptoReciboF.obtenerConceptosXTipoRecibo(fixedListF.find(TipoRecibo.HORAS));
        
        Map<Long, Sueldo> mapSueldoCreados = new HashMap<Long, Sueldo>();
                
        for (Accidentado acc : listaAcc){
            Sueldo sueldoAcc = sueldoF.calcularSueldoAccidentado(periodo, acc, conceptosHoras);
            
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
            for (Sueldo s : periodo.getSueldoCollection()){
                for (TrabajadoresTurnoEmbarque tte : s.getTrabajadoresTurnoEmbarqueCollection()){
                    tte.setLibroSueldo(null);
                    trabTurnoEmbarqueF.edit(tte);
                }
            }

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
    
    
    /*
     * Constantes creadas por la generacion de sueldos a traves de la importacion utilizando valores de la base de datos
     * Puede ser que se tengan que cambiar una vez que cambien los conceptos
     */
    private static int ID_ConceptoSueldoBruto = 1;
    private static int ID_ConceptoJubilacion = 2;
    private static int ID_ConceptoObraSocial = 3;
    private static int ID_ConceptoSindicato = 4;
    private static int ID_ConceptoNoRemunerativo = 5;
    private static int ID_ConceptoDtoJudicial = 6;
    private static int ID_ConceptoFondoComp = 7;
    private static int ID_ConceptoSAC = 8;
    private static int ID_ConceptoVacaciones = 9;
    private static int ID_ConceptoHabil = 10;
    private static int ID_ConceptoExtra100 = 11;
    private static int ID_ConceptoExtra150 = 12;
    private static int ID_ConceptoExtra300 = 13;
    private static int CANTIDAD_CONCEPTOS = 13;
    
    
    
    public static final int MAXIMO_TIPOS_JORNALES_XLS = 8;
    public static final int TIPO_PLANILLA_EXTRA300 = 0;
    public static final int TIPO_PLANILLA_HABIL = 1;
    public static final int TIPO_PLANILLA_EXTRA100 = 2;
    public static final int TIPO_PLANILLA_EXTRA150 = 3;
    public static final int TIPO_PAGOFERI_ACCIDENTADO = 4;
    public static final int TIPO_PAGOFERI_FERIADO = 5;
    public static final int TIPO_PAGOFERI_VACACIONES = 6;
    public static final int TIPO_PAGOFERI_SAC = 7;
    
    /**
     * Carga los datos del período, cargando y actualizando los datos de los trabajadores
     * @param fileAltas archivo dbf con la tabla de trabajadores
     * @param filePlanilla archivo dbf con la tabla de planillas
     * @param fileCargaReg archivo dbf con la tabla de carga, trabajadores y planillas
     * @param filePagoFeri archivo dbf con los pago por accidente y demases
     * @param periodoCarga periodo al que se realiza la carga
     * @return periodo cargado y completado
     */
    public Periodo completarPeríodo(InputStream fileAltas, InputStream filePlanilla, InputStream fileCargaReg, InputStream filePagoFeri, Periodo periodoCarga){
        //limpio la lista de sueldos del periodo carga ya que se carga a traves del archivo y tiene que ser una operacion idempotente
        periodoCarga.setSueldoCollection(new ArrayList<Sueldo>());
        
        //Persisto el periodo
        persist(periodoCarga);
        
        //Creo el Mapa de Registros para el libro
        Map<String, CargaRegVO[]> mapRegistros = new HashMap<String, CargaRegVO[]>();
        
        //Obtengo la lista procesada y actualizada de personal con clave igual al cuil
        Map<String, Personal> mapPersonal = personalF.obtenerPersonalDesdeDBF(fileAltas);
        
        //Obtengo los Turnos embarque de la tabla de planillas
        Map<Long, TurnoEmbarqueExcelVO> mapTurnos = turnoEmbarqueF.embarquesDesdeExcel(filePlanilla, periodoCarga.getDesde(), periodoCarga.getHasta());
        
        //Obtengo los sueldos de las horas trabajadas y las agrego al map
        sueldoF.salariosPlanillaDesdeExcel(fileCargaReg, mapRegistros, mapTurnos);
        
        //Obtengo los sueldos de feriados, accidentados, valores de vacaciones y aguinaldos y se los agrego a los registros
        sueldoF.otrosConceptosDesdeExcel(filePagoFeri, mapRegistros, periodoCarga.getDesde(), periodoCarga.getHasta());
        
        
        //Actualizo los datos de todo el personal
        for (Map.Entry<String, Personal> personalEntry : mapPersonal.entrySet()){
            personalF.persist(personalEntry.getValue());
        }
        
        //Cargo los Conceptos de los recibos para no tener que ir realizando busquedas de más
        Map<Integer, ConceptoRecibo> mapConceptos = new HashMap<Integer, ConceptoRecibo>();
        //Como los conceptos son seguidos, los cargo con iteracion
        for (int i = 1; i <= CANTIDAD_CONCEPTOS; i++)
            mapConceptos.put(i, conceptoReciboF.find(i)) ;
        
        
        //Creo las entidades sueldo de acuerdo a los datos de Carga Reg levantados y sintetizados
        for (String cuilPersonal : mapPersonal.keySet()){
            //Levanto el registro (y si es necesario el pagoFeri)
            CargaRegVO[] registros = mapRegistros.get(cuilPersonal);
            
            BigDecimal[] valores = new BigDecimal[CANTIDAD_CONCEPTOS+1];
            BigDecimal[] cantidades = new BigDecimal[CANTIDAD_CONCEPTOS+1];
            for (int i = 0; i <= CANTIDAD_CONCEPTOS; i++){
                valores[i] = BigDecimal.ZERO;
                cantidades[i] = BigDecimal.ZERO;
            }
        
            
            
            //Si no encuentro registro (o PagoFeri) entonces lo paso de largo) sino creo el sueldo
            if (registros != null){ 
                Personal personalActual = mapPersonal.get(cuilPersonal);
                
                Sueldo sueldo = new Sueldo();
                sueldo.setPeriodo(periodoCarga);
                sueldo.setPersonal(personalActual);
                sueldo.setItemsSueldoCollection(new ArrayList<ItemsSueldo>());
                
                //Persisto el sueldo
                sueldoF.create(sueldo);
                
                /*
                 * Seteo las cantidades por defecto de los conceptos que no estan vinculados con planillas
                 */
                //Cantidad de Jubilacion
                cantidades[ID_ConceptoJubilacion] = mapConceptos.get(ID_ConceptoJubilacion).getValor();
                //Cantidad de Obra Social
                if (personalActual.getObraSocial() != null)
                    cantidades[ID_ConceptoObraSocial] = personalActual.getObraSocial().getAportes();
                //Cantidad de Sindicato
                if (personalActual.getCategoriaPrincipal() != null)
                    cantidades[ID_ConceptoSindicato] = personalActual.getCategoriaPrincipal().getSindicato().getPorcentaje();
                //Dto Judicial
                cantidades[ID_ConceptoDtoJudicial] = personalActual.getDescuentoJudicial();
                
                
                
                for (int i = 0; i < MAXIMO_TIPOS_JORNALES_XLS; i++){
                    //Agrego el sueldo bruto, Depende del tipo de jornal pasa a un conteo u otro
                    switch (i){
                        case TIPO_PLANILLA_HABIL:
                            valores[ID_ConceptoHabil] = valores[ID_ConceptoHabil].add(registros[i].getSueldoBruto());
                            cantidades[ID_ConceptoHabil] = cantidades[ID_ConceptoHabil].add(registros[i].getCantidadBruto());
                            break;
                        case TIPO_PLANILLA_EXTRA100:
                            valores[ID_ConceptoExtra100] = valores[ID_ConceptoExtra100].add(registros[i].getSueldoBruto());
                            cantidades[ID_ConceptoExtra100] = cantidades[ID_ConceptoExtra100].add(registros[i].getCantidadBruto());
                            break;
                        case TIPO_PLANILLA_EXTRA150:
                            valores[ID_ConceptoExtra150] = valores[ID_ConceptoExtra150].add(registros[i].getSueldoBruto());
                            cantidades[ID_ConceptoExtra150] = cantidades[ID_ConceptoExtra150].add(registros[i].getCantidadBruto());
                            break;
                        case TIPO_PLANILLA_EXTRA300:
                            valores[ID_ConceptoExtra300] = valores[ID_ConceptoExtra300].add(registros[i].getSueldoBruto());
                            cantidades[ID_ConceptoExtra300] = cantidades[ID_ConceptoExtra300].add(registros[i].getCantidadBruto());
                            break;
                        case TIPO_PAGOFERI_ACCIDENTADO:
                            valores[ID_ConceptoSueldoBruto] = valores[ID_ConceptoSueldoBruto].add(registros[i].getSueldoBruto());
                            cantidades[ID_ConceptoSueldoBruto] = cantidades[ID_ConceptoSueldoBruto].add(registros[i].getCantidadBruto());
                            break;
                        case TIPO_PAGOFERI_FERIADO:
                            valores[ID_ConceptoSueldoBruto] = valores[ID_ConceptoSueldoBruto].add(registros[i].getSueldoBruto());
                            cantidades[ID_ConceptoSueldoBruto] = cantidades[ID_ConceptoSueldoBruto].add(registros[i].getCantidadBruto());
                            break;
                        case TIPO_PAGOFERI_VACACIONES:
                            valores[ID_ConceptoVacaciones] = valores[ID_ConceptoVacaciones].add(registros[i].getSueldoBruto());
                            cantidades[ID_ConceptoVacaciones] = cantidades[ID_ConceptoVacaciones].add(registros[i].getCantidadBruto());
                            break;
                        case TIPO_PAGOFERI_SAC:
                            valores[ID_ConceptoSAC] = valores[ID_ConceptoSAC].add(registros[i].getSueldoBruto());
                            cantidades[ID_ConceptoSAC] = cantidades[ID_ConceptoSAC].add(registros[i].getCantidadBruto());
                            break;
                    }
                    
                    //Completo los otros conceptos
                    valores[ID_ConceptoJubilacion] = valores[ID_ConceptoJubilacion].add(registros[i].getJubilacion());
                    valores[ID_ConceptoObraSocial] = valores[ID_ConceptoObraSocial].add(registros[i].getObraSocial());
                    valores[ID_ConceptoSindicato] = valores[ID_ConceptoSindicato].add(registros[i].getSindicato());
                    valores[ID_ConceptoNoRemunerativo] = valores[ID_ConceptoNoRemunerativo].add(registros[i].getNoRemunerativo());
                    valores[ID_ConceptoDtoJudicial] = valores[ID_ConceptoDtoJudicial].add(registros[i].getDtoJudicial());
                    valores[ID_ConceptoFondoComp] = valores[ID_ConceptoFondoComp].add(registros[i].getFondoComp());
                }
                
                
                //Agrego los elementos calculados al sueldo
                for (int i = 1; i <= CANTIDAD_CONCEPTOS; i++){
                    if (valores[i].doubleValue() > 0.005){
                         itemSueldoF.crearItemSueldo(mapConceptos.get(i), cantidades[i], valores[i], sueldo);
                    }
                }
                
                
                //Guardo los cambios realizados
                sueldoF.edit(sueldo);
                periodoCarga.getSueldoCollection().add(sueldo);
            }
        }
        
        
        //Nuevamente persisto el periodo de carga ya que le sume los sueldos
        edit(periodoCarga);
        
        return periodoCarga;
    }


    
    
  
    
}
