/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.carga;

import com.orco.graneles.domain.carga.CargaTurno;
import com.orco.graneles.domain.carga.CargaTurnoCargas;
import com.orco.graneles.domain.carga.Embarque;
import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.carga.TurnoEmbarque;
import com.orco.graneles.domain.miscelaneos.TipoConceptoRecibo;
import com.orco.graneles.domain.miscelaneos.TipoRecibo;
import com.orco.graneles.domain.salario.ConceptoRecibo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.Moneda;
import com.orco.graneles.model.NegocioException;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import com.orco.graneles.model.salario.ConceptoReciboFacade;
import com.orco.graneles.vo.TrabajadorTurnoEmbarqueVO;
import com.orco.graneles.vo.TurnoEmbarqueExcelVO;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
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
public class TurnoEmbarqueFacade extends AbstractFacade<TurnoEmbarque> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @EJB
    ConceptoReciboFacade conceptoReciboF;
    @EJB
    FixedListFacade fixedListF;
    
    
    protected EntityManager getEntityManager() {
        return em;
    }

    public TurnoEmbarqueFacade() {
        super(TurnoEmbarque.class);
    }

    @Override
    public void persist(TurnoEmbarque entity) {
        //Precalculo los datos
        
        //Total Embarcado
        BigDecimal totalEmbarcado = BigDecimal.ZERO;
        
        for (CargaTurno ct : entity.getCargaTurnoCollection()){
            for (CargaTurnoCargas ctc : ct.getCargasCollection()){
                totalEmbarcado = totalEmbarcado.add(ctc.getCarga());
            }
        }
        entity.setTotalEmbarcado(totalEmbarcado);
        
        //Total Bruto
        BigDecimal totalBruto = BigDecimal.ZERO;
        
        for (TrabajadorTurnoEmbarqueVO tteVO : obtenerTteVos(entity)){
            totalBruto = totalBruto.add(tteVO.getValorBruto());
        }
        entity.setTotalBruto(totalBruto);
        
        super.persist(entity);
    }
    
    /**
     * Obtiene el Turno de Embarque por el nro de planilla
     * @param nroPlanilla
     * @return 
     */
    public TurnoEmbarque obtenerTurnoEmbarque(Integer nroPlanilla){
        List<TurnoEmbarque> turnos = getEntityManager().createNamedQuery("TurnoEmbarque.findByNroPlanilla", TurnoEmbarque.class)
                .setParameter("nroPlanilla", nroPlanilla)
                .getResultList();
        
        if (turnos.size() > 0){
            return turnos.get(0);
        } else {
            return null;
        }
    }
    
    /**
     * Obtiene las ultimas planillas
     * @param cantidad
     * @return 
     */
    public List<TurnoEmbarque> getRecientes(Integer cantidad){
        return getEntityManager().createNamedQuery("TurnoEmbarque.findAllInverse", TurnoEmbarque.class)
                .setMaxResults(cantidad)
                .getResultList();
    }
    
    public TurnoEmbarque crearNuevoTurnoEmbarque(Embarque embarque){
        TurnoEmbarque te = new TurnoEmbarque();
        te.setEmbarque(embarque);
        te.setFecha(new Date());
        
        //Nro de Planilla
        //Debo buscar el maximo nro de planilla existente y le agrego 1
        Integer maximo = null;
        try {
            maximo = getEntityManager().createQuery("SELECT max(te.nroPlanilla) FROM TurnoEmbarque te ", Integer.class)
                                        .getSingleResult();
        } catch (NoResultException e) {            
            maximo = 0;
        }
        te.setNroPlanilla(maximo + 1);
        
        
        return te;
    }
    
    /**
     * Devuelve una lista con los Trabajdores de la planilla con la informacion de sueldos completada
     * @param planilla
     * @return 
     */
    public List<TrabajadorTurnoEmbarqueVO> obtenerTteVos(TurnoEmbarque planilla) {
        List<TrabajadorTurnoEmbarqueVO> trabajadores = new ArrayList<TrabajadorTurnoEmbarqueVO>();
        
        Map<Integer, List<ConceptoRecibo>> conceptosHoras = conceptoReciboF.obtenerConceptosXTipoRecibo(fixedListF.find(TipoRecibo.HORAS));
        
        
        for(TrabajadoresTurnoEmbarque tte : planilla.getTrabajadoresTurnoEmbarqueCollection()){
            TrabajadorTurnoEmbarqueVO tteVO = conceptoReciboF.calcularDiaTTE(tte, true);
            
            BigDecimal valorNeto = BigDecimal.ZERO;
            valorNeto = valorNeto.add(tteVO.getValorBruto());
            
            
            if (conceptosHoras.get(TipoConceptoRecibo.DEDUCTIVO) != null){
                for (ConceptoRecibo cr : conceptosHoras.get(TipoConceptoRecibo.DEDUCTIVO)){
                    double cantidadConcepto = conceptoReciboF.calcularValorConcepto(cr, tteVO.getValorBruto().doubleValue(), tte.getPersonal());
                    valorNeto = valorNeto.subtract(new Moneda(cantidadConcepto));
                }
            }
                
            BigDecimal noRemunerativo = BigDecimal.ZERO;
            
            if (conceptosHoras.get(TipoConceptoRecibo.NO_REMUNERATIVO) != null){
                for (ConceptoRecibo cr : conceptosHoras.get(TipoConceptoRecibo.NO_REMUNERATIVO)){
                    double cantidadConcepto = conceptoReciboF.calcularValorConcepto(cr, tteVO.getValorBruto().doubleValue(), tte.getPersonal());
                    noRemunerativo = noRemunerativo.add(new Moneda(cantidadConcepto));
                    valorNeto = valorNeto.add(new Moneda(cantidadConcepto));
                }
            }
            
            tteVO.setDecreto(noRemunerativo);
            tteVO.setValorTurno(valorNeto);
            
            trabajadores.add(tteVO);
        }
        
        return trabajadores;
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
    
}
