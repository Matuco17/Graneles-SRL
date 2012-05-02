/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.miscelaneos.*;
import com.orco.graneles.domain.personal.Accidentado;
import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.ObraSocial;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.Moneda;
import com.orco.graneles.model.NegocioException;
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
import javax.ejb.EJB;
import javax.persistence.NoResultException;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author orco
 */
@Stateless
public class LibroExcelFacade  {
    
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @EJB
    private FixedListFacade fixedListF;
    
    @EJB
    private PersonalFacade personalF;

    protected EntityManager getEntityManager() {
        return em;
    }

    public LibroExcelFacade() {
        
    }
    
    private Map<Integer, FixedList> fxLs;
    
    protected Map<Integer, FixedList> getFxLs(){
        if (fxLs == null){
            fxLs = new HashMap<Integer, FixedList>();
            
            for (FixedList fl : fixedListF.findAll()){
                fxLs.put(fl.getId(), fl);
            }
        }
        return fxLs;
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
        if (periodoCarga.getId() != null){
            em.merge(periodoCarga);
        } else {
            em.persist(periodoCarga);
        }
        
        try {
            
            //Creo el Mapa de Registros para el libro
            Map<String, CargaRegVO[]> mapRegistros = new HashMap<String, CargaRegVO[]>();

            //Obtengo la lista procesada y actualizada de personal con clave igual al cuil
            //Map<String, Personal> mapPersonal = obtenerPersonalDesdeDBF(fileAltas);

            Map<String, Personal> mapPersonal = new HashMap<String, Personal>();
            for (Personal p : personalF.findAll()){
                mapPersonal.put(p.getCuil(), p);
            }
            
            
            
            //Obtengo los Turnos embarque de la tabla de planillas
            Map<Long, TurnoEmbarqueExcelVO> mapTurnos = embarquesDesdeExcel(filePlanilla, periodoCarga.getDesde(), periodoCarga.getHasta());

            //Obtengo los sueldos de las horas trabajadas y las agrego al map
            salariosPlanillaDesdeExcel(fileCargaReg, mapRegistros, mapTurnos);

            //Obtengo los sueldos de feriados, accidentados, valores de vacaciones y aguinaldos y se los agrego a los registros
            otrosConceptosDesdeExcel(filePagoFeri, mapRegistros, periodoCarga.getDesde(), periodoCarga.getHasta());


            //Actualizo los datos de todo el personal
            for (Map.Entry<String, Personal> personalEntry : mapPersonal.entrySet()){
                //em.persist(personalEntry.getValue());
            }

            //Cargo los Conceptos de los recibos para no tener que ir realizando busquedas de más
            Map<Integer, ConceptoRecibo> mapConceptos = new HashMap<Integer, ConceptoRecibo>();
            //Como los conceptos son seguidos, los cargo con iteracion
            for (int i = 1; i <= CANTIDAD_CONCEPTOS; i++)
                mapConceptos.put(i, em.find(ConceptoRecibo.class, i)) ;


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
                    em.persist(sueldo);

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
                             crearItemSueldo(mapConceptos.get(i), cantidades[i], valores[i], sueldo);
                        }
                    }


                    //Guardo los cambios realizados
                    em.merge(sueldo);
                    periodoCarga.getSueldoCollection().add(sueldo);
                }
            }


            //Nuevamente persisto el periodo de carga ya que le sume los sueldos
            em.merge(periodoCarga);
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return periodoCarga;
    }

    
    /**
     * Crea un itemSueldo para el concepto y el valor pedido
     */
    public void crearItemSueldo(ConceptoRecibo concepto,BigDecimal cantidad, BigDecimal valor, Sueldo sueldo) {
        //Item Sueldo Bruto
        ItemsSueldo is = new ItemsSueldo();
        is.setConceptoRecibo(concepto);
        is.setValorCalculado(valor);
        is.setValorIngresado(valor);
        is.setCantidad(cantidad);
        is.setSueldo(sueldo);
        if (sueldo.getItemsSueldoCollection() == null)
            sueldo.setItemsSueldoCollection(new ArrayList<ItemsSueldo>());
        sueldo.getItemsSueldoCollection().add(is);
    }
    
    /**
     * Devuelve un map de turnos teniendo como clave el nro de embarque (nro de planilla originalmente) del excel
     * @param archivoXLS archivo excel
     * @param desde fecha limite
     * @param hasta fecha limite
     * @return map con los turnos que cumplen con las fechas siempre que tengan limites, sino se devuelven todos
     */
    public Map<Long, TurnoEmbarqueExcelVO> embarquesDesdeExcel(InputStream archivoXLS, Date desde, Date hasta){
        Map<Long, TurnoEmbarqueExcelVO> turnos = new HashMap<Long, TurnoEmbarqueExcelVO>();
        
        try {
            //Creo el libro y selecciono la primera hoja
            HSSFWorkbook workBook = new HSSFWorkbook(archivoXLS);
            HSSFSheet hssfSheet = workBook.getSheetAt(0);

            //Itero sobre las filas
            Iterator<Row> filaIterator = hssfSheet.rowIterator();
            filaIterator.next(); //Avanzo una fila ya que es la primera con los titulos de la tabla
            while (filaIterator.hasNext())
            {
                Row filaActual =  filaIterator.next();
                
                //Obtengo la fecha del embarque
                Date fechaJornalExcel = filaActual.getCell(4).getDateCellValue();
                
                //Pregunto si tiene limites, si no tiene siempre se agrega sino solo la fecha q entre entre los limites
                if ((desde == null) || 
                    (desde != null && hasta != null && (fechaJornalExcel.compareTo(desde) >= 0) && (fechaJornalExcel.compareTo(hasta) <= 0))){
                    
                    TurnoEmbarqueExcelVO turnoActual = new TurnoEmbarqueExcelVO();
                    
                    //ID del embarque (Planilla)
                    turnoActual.setPlanilla((new Double(filaActual.getCell(0).getNumericCellValue()).longValue()));
                    
                    //Seteo la fecha tambien
                    turnoActual.setFechaJornada(fechaJornalExcel);
                    
                    //Seteo el tipo de Jornal
                    turnoActual.setTipoJornal((new Double(filaActual.getCell(5).getNumericCellValue()).intValue()));
                    
                    turnos.put(turnoActual.getPlanilla(), turnoActual);
                }
            }
        } catch (Exception e) {
            turnos = null;
            e.printStackTrace();
            throw new NegocioException(e.getMessage());
        }
        
        
        
        return turnos;
    }

    
      /**
     * Agrega al map de Registros la suma de los embarques de cada uno de los trabajadores
     * @param archivoXLS archivo con los registros
     * @param registros mapeo de los registros (parametro de salida)
     * @param planillas planillas que participan la busqueda de las horas trabajadas
     */
    public void salariosPlanillaDesdeExcel(InputStream archivoXLS, Map<String, CargaRegVO[]> registros, Map<Long, TurnoEmbarqueExcelVO> planillas){
        
        try {
            //Creo el libro y selecciono la primera hoja
            HSSFWorkbook workBook = new HSSFWorkbook(archivoXLS);
            HSSFSheet hssfSheet = workBook.getSheetAt(0);

            //Itero sobre las filas
            Iterator<Row> filaIterator = hssfSheet.rowIterator();
            filaIterator.next(); //Avanzo una fila ya que es la primera con los titulos de la tabla
            while (filaIterator.hasNext())
            {
                Row filaActual =  filaIterator.next();
                
                //Obtengo la planilla
                Long planilla = (new Double(filaActual.getCell(7).getNumericCellValue())).longValue();
                
                //SPregunto si tiene limites, si no tiene siempre se agrega sino solo la fecha q entre entre los limites
                if ((planillas == null) || 
                    (planillas.keySet().contains(planilla))){
                    
                    String cuilExcel = filaActual.getCell(3).getStringCellValue();
                    //Verifico si está la carga, si está entonces le sumo al anterior, sino le creo uno nuevo
                    CargaRegVO[] registroActual = obtenerRegistrosDelEmpleado(registros, cuilExcel);
                    
                    //Selecciono el tipo del PagoFeri
                    int tipoJornal = planillas.get(planilla).getTipoJornal(); 
                    
                    //Cantidad del bruto
                    registroActual[tipoJornal].setCantidadBruto(registroActual[tipoJornal].getCantidadBruto().add(new Moneda(new Double(filaActual.getCell(5).getNumericCellValue()))));
                                       
                    //Sueldo bruto
                    registroActual[tipoJornal].setSueldoBruto(registroActual[tipoJornal].getSueldoBruto().add(new Moneda(new Double(filaActual.getCell(6).getNumericCellValue()))));
                    
                    //Jubilacion
                    registroActual[tipoJornal].setJubilacion(registroActual[tipoJornal].getJubilacion().add(new Moneda(new Double(filaActual.getCell(19).getNumericCellValue()))));
                    
                    //Obra social
                    registroActual[tipoJornal].setObraSocial(registroActual[tipoJornal].getObraSocial().add(new Moneda(new Double(filaActual.getCell(20).getNumericCellValue()))));
                    
                    //Fondo Comp
                    registroActual[tipoJornal].setFondoComp(registroActual[tipoJornal].getFondoComp().add(new Moneda(new Double(filaActual.getCell(21).getNumericCellValue()))));
                    
                    //Sindicato
                    registroActual[tipoJornal].setSindicato(registroActual[tipoJornal].getSindicato().add(new Moneda(new Double(filaActual.getCell(22).getNumericCellValue()))));
                    
                    //No Remunerativo
                    registroActual[tipoJornal].setNoRemunerativo(registroActual[tipoJornal].getNoRemunerativo().add(new Moneda(new Double(filaActual.getCell(23).getNumericCellValue()))));
                    
                    //Dto Judicial
                    registroActual[tipoJornal].setDtoJudicial(registroActual[tipoJornal].getDtoJudicial().add(new Moneda(new Double(filaActual.getCell(24).getNumericCellValue()))));
                  
                    //Agrego el registro al mapa de registros
                    registros.put(cuilExcel, registroActual);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    } 
        
    /**
     * Devuelve un map con los registros individuales de las personas en el tiempo cumplimentado dependiendo del tipo
     * @param archivoXLS archivo excel
     * @param desde fecha limite
     * @param hasta fecha limite
     * @return map con los registros que cumplen con las fechas siempre que tengan limites, sino se devuelven todos
     */
    public void otrosConceptosDesdeExcel(InputStream archivoXLS, Map<String, CargaRegVO[]> registros, Date desde, Date hasta){
        
        try {
            //Creo el libro y selecciono la primera hoja
            HSSFWorkbook workBook = new HSSFWorkbook(archivoXLS);
            HSSFSheet hssfSheet = workBook.getSheetAt(0);

            //Itero sobre las filas
            Iterator<Row> filaIterator = hssfSheet.rowIterator();
            filaIterator.next(); //Avanzo una fila ya que es la primera con los titulos de la tabla
            while (filaIterator.hasNext())
            {
                Row filaActual =  filaIterator.next();
                
                //Obtengo la fecha del registro, si es valida entonces la proceso sino no
                Date fechaPagoExcel = filaActual.getCell(0).getDateCellValue();
                                
                //Pregunto si tiene limites, si no tiene siempre se agrega sino solo la fecha q entre entre los limites
                if ((desde == null) || 
                    (desde != null && hasta != null && (fechaPagoExcel.compareTo(desde) >= 0) && (fechaPagoExcel.compareTo(hasta) <= 0))){
                    
                    final String cuilExcel = filaActual.getCell(1).getStringCellValue();
                    CargaRegVO[] registroActual = obtenerRegistrosDelEmpleado(registros, cuilExcel);
                    
                    //Selecciono el tipo del PagoFeri
                    int tipoPagoFeri = new Double(filaActual.getCell(2).getNumericCellValue()).intValue();
                                     
                    //Cantidad
                    registroActual[tipoPagoFeri].setCantidadBruto(registroActual[tipoPagoFeri].getCantidadBruto().add(new Moneda(new Double(filaActual.getCell(11).getNumericCellValue()))));
                    
                    //Sueldo bruto
                    registroActual[tipoPagoFeri].setSueldoBruto(registroActual[tipoPagoFeri].getSueldoBruto().add(new Moneda(new Double(filaActual.getCell(5).getNumericCellValue()))));
                    
                    //Jubilacion
                    registroActual[tipoPagoFeri].setJubilacion(registroActual[tipoPagoFeri].getJubilacion().add(new Moneda(new Double(filaActual.getCell(7).getNumericCellValue()))));
                    
                    //Obra social
                    registroActual[tipoPagoFeri].setObraSocial(registroActual[tipoPagoFeri].getObraSocial().add(new Moneda(new Double(filaActual.getCell(10).getNumericCellValue()))));
                    
                    //Fondo Comp
                    registroActual[tipoPagoFeri].setFondoComp(registroActual[tipoPagoFeri].getFondoComp().add(new Moneda(new Double(filaActual.getCell(9).getNumericCellValue()))));
                    
                    //Sindicato
                    registroActual[tipoPagoFeri].setSindicato(registroActual[tipoPagoFeri].getSindicato().add(new Moneda(new Double(filaActual.getCell(8).getNumericCellValue()))));
                    
                    //No Remunerativo
                    if (filaActual.getCell(15) != null)
                        registroActual[tipoPagoFeri].setNoRemunerativo(registroActual[tipoPagoFeri].getNoRemunerativo().add(new Moneda(new Double(filaActual.getCell(15).getNumericCellValue()))));
                    
                    //Dto Judicial
                    if (filaActual.getCell(16) != null)
                        registroActual[tipoPagoFeri].setDtoJudicial(registroActual[tipoPagoFeri].getDtoJudicial().add(new BigDecimal(new Double(filaActual.getCell(16).getNumericCellValue()))));
                  
                    //Agrego el registro al mapa de registros
                    registros.put(cuilExcel, registroActual);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    /**
     * OBtiene el registro del map del empleado utilizando su cuil. Si no se encuentra crea un registro nuevo y limpio
     * @param registros
     * @param cuilExcel
     * @return 
     */
    private CargaRegVO[] obtenerRegistrosDelEmpleado(Map<String, CargaRegVO[]> registros, final String cuilExcel) {
        //Verifico si está la carga, si está entonces le sumo al anterior, sino le creo uno nuevo
        CargaRegVO[] registroActual = registros.get(cuilExcel) ;
        if (registroActual == null){
            registroActual = new CargaRegVO[LibroExcelFacade.MAXIMO_TIPOS_JORNALES_XLS];
            //CUIL del personal
            for (int i=0; i< LibroExcelFacade.MAXIMO_TIPOS_JORNALES_XLS; i++){
                registroActual[i] = new CargaRegVO();
                registroActual[i].setCuilPersonal(cuilExcel);
            }
        }
        return registroActual;
    }
  
    
     /**
     * Lee de la tabla dbf y genera un map con la lista del personal, existente o no
     * @param tablaDBF tabla dbf del sistema legacy
     * @return Map con clave igual al cuil
     */
    public Map<String, Personal> obtenerPersonalDesdeDBF(InputStream tablaDBF){
        Map<String, Personal> mapPersonal = new HashMap<String, Personal> ();
        String cuilsErroneos = "";
        try
        {
            //Creo el libro y selecciono la primera hoja
            HSSFWorkbook workBook = new HSSFWorkbook(tablaDBF);
            HSSFSheet hssfSheet = workBook.getSheetAt(0);

            //Itero sobre las filas
            Iterator<Row> filaIterator = hssfSheet.rowIterator();
            filaIterator.next(); //Avanzo una fila ya que es la primera con los titulos de la tabla
            while (filaIterator.hasNext())
            {
                Row filaActual =  filaIterator.next();
                
                //Obtengo el cuil y de ahi empiezo a buscar
                String cuil = filaActual.getCell(6).getStringCellValue();
                
                Personal personalActual = null;
                try {
                    personalActual = getEntityManager().createNamedQuery("Personal.findByCuil", Personal.class)
                          .setParameter("cuil", cuil)
                          .getSingleResult();
                } catch (NoResultException e) {
                    personalActual = new Personal();
                    personalActual.setCuil(cuil);
                } catch (Exception e) {
                    personalActual = new Personal();
                    personalActual.setCuil(cuil);
                }
               
                
                try {
                    
                
                    //NroAfiliado
                    if (filaActual.getCell(0) != null)
                        personalActual.setNroAfiliado(Integer.parseInt(filaActual.getCell(0).getStringCellValue().replaceAll(" ", "")));

                    //NroDocumento
                    if (filaActual.getCell(1) != null)
                        personalActual.setDocumento(filaActual.getCell(1).getStringCellValue());

                    //Tipo de Documento
                    //TODO: CONTROLAR SI LOS TIPOS DE DOCUMENTOS SON LOS MISMOS
                    if (filaActual.getCell(2) != null) {
                        String tipoDocExcel = filaActual.getCell(2).getStringCellValue();
                        if (tipoDocExcel.equals("1") || tipoDocExcel.equals("D")){
                            personalActual.setTipoDocumento(getFxLs().get(TipoDocumento.DNI));
                        } else if (tipoDocExcel.equals("2")) {
                            personalActual.setTipoDocumento(getFxLs().get(TipoDocumento.LC));
                        } else if (tipoDocExcel.equals("3")) {
                            personalActual.setTipoDocumento(getFxLs().get(TipoDocumento.LE));
                        } else if (tipoDocExcel.equals("4") || tipoDocExcel.equals("X")) {
                            personalActual.setTipoDocumento(getFxLs().get(TipoDocumento.PASAPORTE));
                        }
                    }
                    
                    //Fecha Ingreso
                    if (filaActual.getCell(3) != null)
                        personalActual.setIngreso(filaActual.getCell(3).getDateCellValue());

                    //Apellido y Nombres (se lo asigno a todos los apellidos)
                    //TODO: VER SI SE PUEDE EXTRAER CORRECTAMENTE EL NOMBRE DEL TIPO
                    if (filaActual.getCell(4) != null)
                        personalActual.setApellido(filaActual.getCell(4).getStringCellValue());

                    //Estado Civil
                    if (filaActual.getCell(5) != null){
                        String estadoCivilExcel = filaActual.getCell(5).getStringCellValue().toUpperCase();
                        if (estadoCivilExcel.equals("C")){
                            personalActual.setEstadoCivil(getFxLs().get(EstadoCivil.CASADO));
                        } else if (estadoCivilExcel.equals("S")){
                            personalActual.setEstadoCivil(getFxLs().get(EstadoCivil.SOLTERO));
                        } else if (estadoCivilExcel.equals("D")){
                            personalActual.setEstadoCivil(getFxLs().get(EstadoCivil.DIVORCIADO));
                        } else if (estadoCivilExcel.equals("V")){
                            personalActual.setEstadoCivil(getFxLs().get(EstadoCivil.VIUDO));
                        } 
                    }

                    //Registro
                    if (filaActual.getCell(7) != null)
                        personalActual.setRegistro(String.valueOf((new Double(filaActual.getCell(7).getNumericCellValue()).intValue())));

                    //SINDICADO (8)
                    if (filaActual.getCell(8) != null)
                        if (filaActual.getCell(8).getStringCellValue().equals("S")){
                            personalActual.setSindicato(Boolean.TRUE);
                        } else {
                            personalActual.setSindicato(Boolean.FALSE);
                        }
                    
                    //Direccion
                    if (filaActual.getCell(9) != null)
                        personalActual.setDomicilio(filaActual.getCell(9).getStringCellValue());

                    //Fecha Nacimiento
                    if (filaActual.getCell(10) != null)
                        personalActual.setFechaNacimiento(filaActual.getCell(10).getDateCellValue());

                    //Estado
                    if (filaActual.getCell(12) != null){
                        String activoJubiladoExcel = filaActual.getCell(12).getStringCellValue().toUpperCase();
                        if (activoJubiladoExcel.equals("A")){
                            personalActual.setEstado(getFxLs().get(EstadoPersonal.ACTIVO));
                        } else if (activoJubiladoExcel.equals("J")){
                            personalActual.setEstado(getFxLs().get(EstadoPersonal.JUBILADO));
                        }
                    }

                    //Categoria Principal
                    if (filaActual.getCell(13) != null)
                        personalActual.setCategoriaPrincipal(em.find(Categoria.class,  Integer.parseInt(filaActual.getCell(13).getStringCellValue())));

                    //Cuidad
                    if (filaActual.getCell(14) != null)
                        personalActual.setLocalidad(filaActual.getCell(14).getStringCellValue());

                    //Obra Social
                    if (filaActual.getCell(15) != null)
                        personalActual.setObraSocial(em.find(ObraSocial.class, (new Double(filaActual.getCell(15).getNumericCellValue())).intValue()));

                    //Esposa
                    if (filaActual.getCell(16) != null)
                        if (filaActual.getCell(16).getStringCellValue().equals("S")){
                        personalActual.setEsposa(Boolean.TRUE);
                        } else {
                            personalActual.setEsposa(Boolean.FALSE);
                        }

                    //Hijos
                    if (filaActual.getCell(17) != null)
                        personalActual.setHijos((new Double(filaActual.getCell(17).getNumericCellValue())).intValue());

                    //Prenatal
                    if (filaActual.getCell(18) != null)
                        if (filaActual.getCell(18).getStringCellValue().equals("S")){
                        personalActual.setPrenatal(Boolean.TRUE);
                        } else {
                            personalActual.setPrenatal(Boolean.FALSE);
                        }

                    //Escolaridad
                    if (filaActual.getCell(19) != null)
                        personalActual.setEscolaridad((new Double(filaActual.getCell(19).getNumericCellValue())).intValue());

                    //Cta Bancaria
                    if (filaActual.getCell(20) != null)
                        personalActual.setCuentaBancaria(filaActual.getCell(20).getStringCellValue());
                    
                    //AFJP (21)
                    if (filaActual.getCell(8) != null)
                        if (filaActual.getCell(8).getStringCellValue().equals("S")){
                            personalActual.setAfjp(Boolean.TRUE);
                        } else {
                            personalActual.setAfjp(Boolean.FALSE);
                        }
                    
                    
                    //Tipo Recibo
                    if (filaActual.getCell(22) != null)
                        if (filaActual.getCell(22).getStringCellValue().equals("S")){
                            personalActual.setTipoRecibo(getFxLs().get(TipoRecibo.MENSUAL));
                        } else {
                            personalActual.setTipoRecibo(getFxLs().get(TipoRecibo.HORAS));
                        }

                    //Descuento Judicial
                    if (filaActual.getCell(0) != null)
                        personalActual.setDescuentoJudicial(new Moneda(new Double(filaActual.getCell(23).getNumericCellValue())));

                    
                    //TODO: FALTA VER CON FAMILIA (11)
                    
                } catch (Exception e) {
                    cuilsErroneos += cuil + ", ";
                }
                
                //Agrego el personal al map
                mapPersonal.put(cuil, personalActual);
            }
        }
        catch (Exception e)
        {
            mapPersonal = null;
        }

        
        
        return mapPersonal;
    }
    
}
