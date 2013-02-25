/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.miscelaneos.TipoValorConcepto;
import com.orco.graneles.domain.salario.ItemsSueldo;
import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.domain.salario.Sueldo;
import com.orco.graneles.model.salario.ConceptoReciboFacade;
import com.orco.graneles.vo.DescompisicionMoneda;
import com.orco.graneles.vo.TrabajadorConBrutoVO;
import java.math.BigDecimal;
import java.util.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Clase que genera el reporte de libro de sueldos
 * @author orco
 */
public class ListadoSacYVacaciones extends ReporteGenerico {
    
    private List<TrabajadorConBrutoVO> dataSource;
    private Periodo periodo;
    
    @Override
    protected String[] getUrlImagenes() {
        return new String[]{"logoReducido.jpg"};
    }
    
    public ListadoSacYVacaciones(Periodo periodo, List<Sueldo> sueldosFiltrados, ConceptoReciboFacade conceptoReciboF){
        this.periodo = periodo;
        dataSource = new ArrayList<TrabajadorConBrutoVO>();
        
        //Realizo los calculos para la descomposicion de la moneda por categoria
        Map<Integer, DescompisicionMoneda> descomposicionXCategoria = new HashMap<Integer, DescompisicionMoneda>();
        
        //Agrego todos los items del periodo para el reporte
        for(Sueldo s : sueldosFiltrados){
            BigDecimal totalSACyVac = BigDecimal.ZERO;
            
            for (ItemsSueldo is : s.getItemsSueldoCollection()){
                if (is.getConceptoRecibo().getTipoValor().getId().equals(TipoValorConcepto.SAC)
                        || is.getConceptoRecibo().getTipoValor().getId().equals(TipoValorConcepto.VACACIONES)){
                    totalSACyVac = totalSACyVac.add(is.getValorCalculado());
                }
            }
            
            if (totalSACyVac.doubleValue() > 0.01) {
                TrabajadorConBrutoVO tbVO = new TrabajadorConBrutoVO(s);
                dataSource.add(tbVO);
                
                //Agrego el valor a la descomposicion
                DescompisicionMoneda desc = descomposicionXCategoria.get(tbVO.getCategoriaId());
                if (desc == null){
                    desc = new DescompisicionMoneda(BigDecimal.ZERO);
                }
                BigDecimal brutoADescomponer = tbVO.getSac().add(tbVO.getVacaciones());
                double netoADescomponer = conceptoReciboF.calcularNeto(s.getPersonal(), brutoADescomponer.doubleValue());
                
                desc.agregarDescomposicion(new DescompisicionMoneda(new BigDecimal(netoADescomponer)));
                descomposicionXCategoria.put(tbVO.getCategoriaId(), desc);
            }
        }
            
                
        //Ordeno antes de devolver todo
        Collections.sort(dataSource, new ComparadorItems());
        
        //Asigno la descomposicion calculadao
        for (TrabajadorConBrutoVO tbVO : dataSource){
            tbVO.setDescomposicionTotal(descomposicionXCategoria.get(tbVO.getCategoriaId()));
        }
        
    }

    @Override
    public String obtenerReportePDF() {
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(dataSource);
        
        return printGenerico(ds, "ListadoSacYVac", "ListadoSacYVacaciones_"+ periodo.getDescripcion());
    }
    
    
    private class ComparadorItems implements Comparator<TrabajadorConBrutoVO>{
        
        @Override
        public int compare(TrabajadorConBrutoVO o1, TrabajadorConBrutoVO o2) {
            if (o1.getCategoriaId().equals(o2.getCategoriaId())){
                return o1.getApellidoYNombre().compareToIgnoreCase(o2.getApellidoYNombre());
            } else {
                return o1.getCategoriaDescripcion().compareToIgnoreCase(o2.getCategoriaDescripcion());          
            }
        }
    } 
    
}
