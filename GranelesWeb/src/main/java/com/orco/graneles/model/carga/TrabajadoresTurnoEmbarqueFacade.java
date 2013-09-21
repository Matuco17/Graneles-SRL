/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.carga;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.Feriado;
import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.domain.salario.SalarioBasico;
import com.orco.graneles.domain.salario.TipoJornal;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.salario.ConceptoReciboFacade;
import com.orco.graneles.model.salario.FeriadoFacade;
import com.orco.graneles.model.salario.TipoJornalFacade;
import com.orco.graneles.vo.JornalVO;
import com.orco.graneles.vo.TrabajadorTurnoEmbarqueVO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
/**
 *
 * @author orco
 */
@Stateless
public class TrabajadoresTurnoEmbarqueFacade extends AbstractFacade<TrabajadoresTurnoEmbarque> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    
    @EJB
    private ConceptoReciboFacade conceptoReciboF;
    @EJB
    private FeriadoFacade feriadoF;
    @EJB
    private TipoJornalFacade tipoJornalF;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TrabajadoresTurnoEmbarqueFacade() {
        super(TrabajadoresTurnoEmbarque.class);
    }
    
    public List<TrabajadoresTurnoEmbarque> getTrabajadoresPeriodo(Periodo periodo){
      return getEntityManager().createNamedQuery("TrabajadoresTurnoEmbarque.findXPeriodo", TrabajadoresTurnoEmbarque.class)
                  .setParameter("desde", periodo.getDesde())
                  .setParameter("hasta", periodo.getHasta())              
                  .getResultList();
    }   
    
    public List<TrabajadoresTurnoEmbarque> getTrabajadoresPeriodo(Date desde, Date hasta){
      return getEntityManager().createNamedQuery("TrabajadoresTurnoEmbarque.findXPeriodo", TrabajadoresTurnoEmbarque.class)
                  .setParameter("desde", desde)
                  .setParameter("hasta", hasta)              
                  .getResultList();
    }   
    
    public List<TrabajadoresTurnoEmbarque> getTrabajadoresPeriodo(Personal personal, Date desde, Date hasta){
      return getEntityManager().createNamedQuery("TrabajadoresTurnoEmbarque.findXPeriodoYPersonal", TrabajadoresTurnoEmbarque.class)
                  .setParameter("personal", personal)
                  .setParameter("desde", desde)
                  .setParameter("hasta", hasta)
                  .getResultList();
    }    
    
    
    
    /**
     * Metodo que devuelve el último trabajo realizado por el personal correpondiente
     * @param personal
     * @param fecha fecha límite para tener en cuenta el trabajo realizado
     * @return 
     */
    public TrabajadoresTurnoEmbarque getUltimoTrabajoRealizado(Personal personal, Date fecha){
        List<TrabajadoresTurnoEmbarque> listaTrabajos = getEntityManager().createNamedQuery("TrabajadoresTurnoEmbarque.findXPersonalFechaDesc", TrabajadoresTurnoEmbarque.class)
                                                    .setParameter("personal", personal)
                                                    .setParameter("fecha", fecha)
                                                    .getResultList();
        
        if (listaTrabajos != null && listaTrabajos.size() > 0){
            return listaTrabajos.get(0);
        } else {
            return null;
        }
        
    }
    
    public List<JornalVO> getJornales(Categoria categoria, Personal personal, Date desde, Date hasta, Boolean incluirFeriados){
        List<JornalVO> jornalesVOs = new ArrayList<JornalVO>();
        
        for (TrabajadoresTurnoEmbarque tte : getTrabajadores(categoria, personal, desde, hasta)){
            jornalesVOs.add(new JornalVO(tte, null, null));
        }
        
        if (incluirFeriados){
            TipoJornal tjBasico = tipoJornalF.find(TipoJornal.BASICO);
            
            for (Feriado f : feriadoF.obtenerFeriados(desde, hasta)){
                for (Map.Entry<Personal, TrabajadoresTurnoEmbarque> tte : feriadoF.obtenerTrabajadoresIncluidos(f.getFecha()).entrySet()){
                    boolean agregarTTE = true;
                    if (categoria != null && categoria.getId() != tte.getValue().getCategoria().getId()){
                        agregarTTE = false;
                    }
                    if (agregarTTE && personal != null && personal.getId() != tte.getValue().getPersonal().getId()){
                        agregarTTE = false;
                    }
                    if (agregarTTE){
                        jornalesVOs.add(new JornalVO(tte.getValue(), f, tjBasico));
                    }
                }
            }
        }
        
        return jornalesVOs;
    }
    
    /**
     * Busca los tte de acuerdo a los parametros seleccionados, los primeros 2 son opcionales
     * @param categora
     * @param personal
     * @param desde
     * @param hasta
     * @return 
     */
    public List<TrabajadoresTurnoEmbarque> getTrabajadores(Categoria categoria, Personal personal, Date desde, Date hasta){
        return getEntityManager().createNamedQuery("TrabajadoresTurnoEmbarque.findXFechasCatPers", TrabajadoresTurnoEmbarque.class)
                .setParameter("personal", personal)
                .setParameter("categoria", categoria)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList();
    }
    
    /**
     * Metodo que recalcula todos los sueldos en el sistema
     */
    public void recalcularSueldos(){
        List<TrabajadoresTurnoEmbarque> ttes = findAll();
        
        for (TrabajadoresTurnoEmbarque tte : ttes){
            TrabajadorTurnoEmbarqueVO tteVO = conceptoReciboF.calcularDiaTTE(tte, true);
            
            if (tte.getBruto().doubleValue() - tteVO.getValorBruto().doubleValue() > 0.01){
                tte.setBruto(tteVO.getValorBruto());
                tte.setNeto(tteVO.getValorTurno());
                
                    String s = tte.getPersonal().getCuil();
                        s+= "," + tte.getBruto().toString();
                        s+= "," + String.valueOf(tteVO.getValorBruto().toString());
                        s+= "," + String.valueOf(tte.getPlanilla().getNroPlanilla()) + "(" + String.valueOf(tte.getPlanilla().getId()) + ")";
                        s+= "," + tte.getPlanilla().getEmbarque().getCodigo() + "(" + String.valueOf(tte.getPlanilla().getEmbarque().getId()) + ")";
                        s+= "," + tte.getTarea().getDescripcion() + "," + tte.getCategoria() + "," + tte.getDelegado().toString();
                        s+= "," + tte.getPlanilla().getFecha().toString();
                        System.out.println(s);                        
                   
                
            }
            
            
            //edit(tte);
        }
    }
    
    /**
     * Metodo que recalcula los sueldos de acuerdo al salario basico
     * @param sb 
     */
    public void recalcularSueldos(SalarioBasico sb){
        List<TrabajadoresTurnoEmbarque> ttes = 
                getEntityManager().createNamedQuery("TrabajadoresTurnoEmbarque.findXSalarioBasico", TrabajadoresTurnoEmbarque.class)
                    .setParameter("desde", sb.getDesde())
                    .setParameter("hasta", sb.getHasta())
                    .setParameter("tarea", sb.getTarea())
                    .setParameter("categoria", sb.getCategoria())
                    .getResultList();
                
        
        for (TrabajadoresTurnoEmbarque tte : ttes){
            TrabajadorTurnoEmbarqueVO tteVO = conceptoReciboF.calcularDiaTTE(tte, true);
            
            tte.setBruto(tteVO.getValorBruto());
            tte.setNeto(tteVO.getValorTurno());
            
            edit(tte);
        }
    }
    
}
