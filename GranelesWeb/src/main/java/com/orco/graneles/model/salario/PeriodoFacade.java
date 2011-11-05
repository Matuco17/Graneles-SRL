/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.miscelaneos.*;
import com.orco.graneles.domain.personal.Accidentado;
import com.orco.graneles.domain.salario.ConceptoRecibo;
import com.orco.graneles.domain.salario.ItemsSueldo;
import com.orco.graneles.domain.salario.Periodo;
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
import com.orco.graneles.vo.TurnoEmbarqueExcelVO;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import javax.ejb.EJB;
import javax.persistence.NoResultException;
/**
 *
 * @author orco
 */
@Stateless
public class PeriodoFacade extends AbstractFacade<Periodo> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @EJB
    private PersonalFacade personalFacade;
    @EJB
    private TurnoEmbarqueFacade teFacade;
    @EJB
    private SueldoFacade sueldoFacade;
    @EJB
    private ItemsSueldoFacade itemSueldoFacade;
    @EJB
    private ConceptoReciboFacade conceptoReciboFacade;
    @EJB
    private TrabajadoresTurnoEmbarqueFacade tteFacade;
    @EJB
    private AccidentadoFacade accFacade;
    @EJB
    private FixedListFacade fxlFacade;
    @EJB
    private SalarioBasicoFacade sbFacade;
    
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
   
    private void generarSueldosTTE(Periodo periodo){
        List<TrabajadoresTurnoEmbarque> listaTTE = tteFacade.getTrabajadoresPeriodo(periodo);
        Map<Integer, List<ConceptoRecibo>> conceptos = conceptoReciboFacade.obtenerConceptosXTipoRecibo(fxlFacade.find(TipoRecibo.HORAS));
        Map<Integer, FixedList> mapAdicTarea = fxlFacade.findByListaMap(AdicionalTarea.ID_LISTA);
        
        
        //Por cada uno de los turnos trabajados realizo las operaciones
        for(TrabajadoresTurnoEmbarque tte : listaTTE){
            double totalBruto = 0; //Valor total del bruto para que se termine de cerrar esto
            Sueldo sueldoTTE = new Sueldo();
            sueldoTTE.setPeriodo(periodo);
            sueldoTTE.setPersonal(tte.getPersonal());
            
            
            
            //Por cada tipo de Concepto Remunerativo
            for (ConceptoRecibo cRemunerativo : conceptos.get(TipoConceptoRecibo.REMUNERATIVO)){
                if (cRemunerativo.getTipo().getId().equals(TipoValorConcepto.DIAS_TRABAJO)){ //Esto significa que debo realizar el calculo del total bruto con el salario basico
                    //Obtengo el salario correspondiente al tte
                    SalarioBasico salario = sbFacade.obtenerSalarioActivo(tte.getTarea(), tte.getCategoria(), tte.getPlanilla().getFecha());
                    
                    //Obtengo el valor del bruto ya que depende si trabajo 6 o 3 horas (y el salario está en valor de horas
                    double basicoBruto = salario.getBasico().doubleValue() / 6 * tte.getHoras().doubleValue();
                    double totalConcepto = basicoBruto; //resultado de la suma del concepto
                    
                    
                    //Realizo el agregado de los modificadores de tarea
                    if (tte.getTarea().getInsalubre()){
                        totalConcepto += basicoBruto * mapAdicTarea.get(AdicionalTarea.INSALUBRE).getValorDefecto().doubleValue();
                    }
                    if (tte.getTarea().getPeligrosa()){
                        totalConcepto += basicoBruto * mapAdicTarea.get(AdicionalTarea.PELIGROSA).getValorDefecto().doubleValue();
                    }
                    if (tte.getTarea().getPeligrosa2()){
                        totalConcepto += basicoBruto * mapAdicTarea.get(AdicionalTarea.PELIGROSA2).getValorDefecto().doubleValue();
                    }
                    if (tte.getTarea().getProductiva()){
                        totalConcepto += basicoBruto * mapAdicTarea.get(AdicionalTarea.PRODUCTIVA).getValorDefecto().doubleValue();
                    }
                    
                    //Ahora aplico el valor del modificador del tipo de jornal
                    totalConcepto += totalConcepto * tte.getPlanilla().getTipo().getPorcExtraBruto().doubleValue() / 100;
                    totalConcepto += basicoBruto * tte.getPlanilla().getTipo().getPorcExtraBasico().doubleValue() / 100;
                    
                    //Agrego el valor del total del concepto al valor del total del bruto
                    totalBruto += totalConcepto;
                    
                    //Una vez que tengo el valor de esta hora, lo agrego
                    
                    
                    
                }
            }
            
            
            
            //TODO: MERGEAR EL SUELDO POR SI EXISTE UNO ANTERIOR (CREAR METODO YA QUE SE VA A USAR EN VS LADOS)
        }
        
        
        
        
        /*
          Por cada tTE en listaTTE
	sB = Saco El sueldo basico
		Tengo que tener en cuenta el valor de la tarea
		+ la suma de los modificadores de la tarea (insalub)
		+ el modificador por dia (tipo de dia al 50%, al 100% etc) (tipo jornal)
	jb = Saco la jubilacion (% buscado por el tipo sobre el sB)
	os = Saco la obra social (% buscado por el tipo sobre el sB)
	sind = Saco el sindicato (% buscado por el tipo sobre el sB)
	dtoJud = saco el desc Judicial (% buscado por la trabajador sobre el sB)
        noRemunerativo = algun otro concepto que se escape
       //Ver si esto se busca de la siguiente manera
          buscar todos los remunerativos
          buscar todas las deducciones
          buscar todos los no remunerativos
       generar los valores del sueldo, y linkearlo al TTE
       guardar
        */
        
    }
    
    private void generarSueldosAccidentados(Periodo periodo){
        List<Accidentado> listaAcc = accFacade.getAccidentadosPeriodo(periodo);
        
        /*
        Para cada Acc 
    Por cada Acc ver cuanto le correspodne de remunerativo total y despues
	buscar todas las deducciones
        buscar todos los no remunerativos
    generar los valores del sueldo, y likearlo al Acc
    guardar
    * 
    */
    }
    
    private void generarSueldosMensuales(Periodo periodo){
        List<Personal> listaMens = personalFacade.getPersonalMensualActivo();
  
        /*
          Para cada Mensual 
    Por cada Mensual ver cuanto le correspodne de remunerativo total y despues
	buscar todas las deducciones
        buscar todos los no remunerativos
    generar los valores del sueldo, y likearlo al Acc
    guardar
    * 
    */
             
    }
    
    
    public void generarSueldosPeriodo(Periodo periodo){
        generarSueldosAccidentados(periodo);
        generarSueldosMensuales(periodo);
        generarSueldosTTE(periodo);
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
        Map<String, Personal> mapPersonal = personalFacade.obtenerPersonalDesdeDBF(fileAltas);
        
        //Obtengo los Turnos embarque de la tabla de planillas
        Map<Long, TurnoEmbarqueExcelVO> mapTurnos = teFacade.embarquesDesdeExcel(filePlanilla, periodoCarga.getDesde(), periodoCarga.getHasta());
        
        //Obtengo los sueldos de las horas trabajadas y las agrego al map
        sueldoFacade.salariosPlanillaDesdeExcel(fileCargaReg, mapRegistros, mapTurnos);
        
        //Obtengo los sueldos de feriados, accidentados, valores de vacaciones y aguinaldos y se los agrego a los registros
        sueldoFacade.otrosConceptosDesdeExcel(filePagoFeri, mapRegistros, periodoCarga.getDesde(), periodoCarga.getHasta());
        
        
        //Actualizo los datos de todo el personal
        for (Map.Entry<String, Personal> personalEntry : mapPersonal.entrySet()){
            personalFacade.persist(personalEntry.getValue());
        }
        
        //Cargo los Conceptos de los recibos para no tener que ir realizando busquedas de más
        Map<Integer, ConceptoRecibo> mapConceptos = new HashMap<Integer, ConceptoRecibo>();
        //Como los conceptos son seguidos, los cargo con iteracion
        for (int i = 1; i <= CANTIDAD_CONCEPTOS; i++)
            mapConceptos.put(i, conceptoReciboFacade.find(i)) ;
        
        
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
                sueldoFacade.create(sueldo);
                
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
                        crearItemSueldo(mapConceptos, cantidades[i], valores[i], sueldo, i);
                    }
                }
                
                
                //Guardo los cambios realizados
                sueldoFacade.edit(sueldo);
                periodoCarga.getSueldoCollection().add(sueldo);
            }
        }
        
        
        //Nuevamente persisto el periodo de carga ya que le sume los sueldos
        edit(periodoCarga);
        
        return periodoCarga;
    }

    /**
     * Crea un itemSueldo para el concepto y el valor pedido
     */
    private void crearItemSueldo(Map<Integer, ConceptoRecibo> mapConceptos,BigDecimal cantidad, BigDecimal valor, Sueldo sueldo, int idConcepto) {
        //Item Sueldo Bruto
        ItemsSueldo itemBruto = new ItemsSueldo();
        itemBruto.setConceptoRecibo(mapConceptos.get(idConcepto));
        itemBruto.setValorCalculado(valor);
        itemBruto.setValorIngresado(valor);
        itemBruto.setCantidad(cantidad);
        itemBruto.setSueldo(sueldo);
        sueldo.getItemsSueldoCollection().add(itemBruto);
    }
    
    
  
    
}
