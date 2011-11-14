/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.salario;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.miscelaneos.AdicionalTarea;
import com.orco.graneles.domain.miscelaneos.FixedList;
import com.orco.graneles.domain.miscelaneos.TipoConceptoRecibo;
import com.orco.graneles.domain.miscelaneos.TipoValorConcepto;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.ConceptoRecibo;
import com.orco.graneles.domain.salario.SalarioBasico;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
/**
 *
 * @author orco
 */
@Stateless
public class ConceptoReciboFacade extends AbstractFacade<ConceptoRecibo> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @EJB
    private SalarioBasicoFacade salarioBasicoF;
    @EJB
    private FixedListFacade fixedListF;
    
    
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConceptoReciboFacade() {
        super(ConceptoRecibo.class);
    }
    
    /**
     * Map distribuido desde el tipo de concepto del recibo con las lista de los conceptos activos del tipo de recibo
     * @param tipoRecibo
     * @return 
     */
    public Map<Integer, List<ConceptoRecibo>> obtenerConceptosXTipoRecibo(FixedList tipoRecibo){
        List<ConceptoRecibo> conceptosEncontrados = getEntityManager().createNamedQuery("ConceptoRecibo.findByTipoRecibo", ConceptoRecibo.class)
                                                        .setParameter("tipoRecibo", tipoRecibo)
                                                        .setParameter("versionActiva", true)
                                                        .getResultList();
        Map<Integer, List<ConceptoRecibo>> result = new HashMap<Integer, List<ConceptoRecibo>>();
        
        for(ConceptoRecibo c : conceptosEncontrados){
            if (result.get(c.getTipo().getId()) == null)
                result.put(c.getTipo().getId(), new ArrayList<ConceptoRecibo>());
            
            result.get(c.getTipo().getId()).add(c);
        }
        
        return result;
    }
    
    /**
     * Obtiene los conceptos de tal tipo de Concepto y tal tipo de Recibo de sueldo
     * @param tipoRecibo
     * @param tipoConcepto
     * @return 
     */
    public List<ConceptoRecibo> obtenerConceptos(FixedList tipoRecibo, FixedList tipoConcepto){
        return getEntityManager().createNamedQuery("ConceptoRecibo.findByTipoReciboYTipoConcepto", ConceptoRecibo.class)
                                   .setParameter("tipoRecibo", tipoRecibo)
                                   .setParameter("tipo", tipoConcepto)
                                   .setParameter("versionActiva", true)
                                   .getResultList();
        
    }
    
    /**
     * Metodo que calcula el valor total del dia trabajado para el Trabajador
     * @param tte
     * @param mapAdicTarea
     * @return 
     */
    public double calcularDiaTrabajadoTTE(TrabajadoresTurnoEmbarque tte, boolean incluirAdicionales) {
        Map<Integer, FixedList> mapAdicTarea = fixedListF.findByListaMap(AdicionalTarea.ID_LISTA);
        
        //Esto significa que debo realizar el calculo del total bruto con el salario basico
        //Obtengo el salario correspondiente al tte
        SalarioBasico salario = salarioBasicoF.obtenerSalarioActivo(tte.getTarea(), tte.getCategoria(), tte.getPlanilla().getFecha());
        //Obtengo el valor del bruto ya que depende si trabajo 6 o 3 horas (y el salario está en valor de horas
        double basicoBruto = salario.getBasico().doubleValue() / 6 * tte.getHoras().doubleValue();
        double totalConcepto = basicoBruto; //resultado de la suma del concepto
        //Realizo el agregado de los modificadores de tarea
        if (incluirAdicionales){
            if (tte.getTarea().getInsalubre()){
                totalConcepto += basicoBruto * (mapAdicTarea.get(AdicionalTarea.INSALUBRE).getValorDefecto().doubleValue() / 100);
            }
            if (tte.getTarea().getPeligrosa()){
                totalConcepto += basicoBruto * (mapAdicTarea.get(AdicionalTarea.PELIGROSA).getValorDefecto().doubleValue() / 100);
            }
            if (tte.getTarea().getPeligrosa2()){
                totalConcepto += basicoBruto * (mapAdicTarea.get(AdicionalTarea.PELIGROSA2).getValorDefecto().doubleValue() / 100);
            }
            if (tte.getTarea().getProductiva()){
                totalConcepto += basicoBruto * (mapAdicTarea.get(AdicionalTarea.PRODUCTIVA).getValorDefecto().doubleValue() / 100);
            }
        }
        //Ahora aplico el valor del modificador del tipo de jornal
        totalConcepto += totalConcepto * tte.getPlanilla().getTipo().getPorcExtraBruto().doubleValue() / 100;
        totalConcepto += basicoBruto * tte.getPlanilla().getTipo().getPorcExtraBasico().doubleValue() / 100;
        return totalConcepto;
    }
    
    /**
     * Metodo que devuelve el valor calculado de acuerdo al concepto, puede ser un simple porcentaje o algo + complejo
     * @param concepto
     * @param totalBruto
     * @return 
     */
    public double calcularValorConcepto(ConceptoRecibo concepto, double totalBruto, Personal personal){
        if (concepto.getTipoValor().getId() == TipoValorConcepto.FIJO){
            return concepto.getValor().doubleValue();
        } else if (concepto.getTipoValor().getId() == TipoValorConcepto.PORCENTUAL
                || concepto.getTipoValor().getId() == TipoValorConcepto.JUBILACION
                || concepto.getTipoValor().getId() == TipoValorConcepto.OBRA_SOCIAL){
            return totalBruto * concepto.getValor().doubleValue() / 100;
        } else if (concepto.getTipoValor().getId() == TipoValorConcepto.SINDICATO) {
            if (personal.getSindicato()){
                double porcSindicato = concepto.getValor().doubleValue();
                if (personal.getCategoriaPrincipal().getSindicato() != null){
                    porcSindicato = personal.getCategoriaPrincipal().getSindicato().getPorcentaje().doubleValue();
                }
                return totalBruto * porcSindicato / 100;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
    
    
}
