/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.carga.*;
import com.orco.graneles.domain.facturacion.Empresa;
import com.orco.graneles.model.carga.CargaTurnoCargasFacade;
import com.orco.graneles.vo.ResumenExportadorVO;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author orco
 */
public class DeclaracionJuradaExportador extends ReporteGenerico {

    private List<ResumenExportadorVO> dataSource;
    private EmbarqueCargador embarqueCargador;
       
    private CargaTurnoCargasFacade cargaTurnoCargasF;

    public DeclaracionJuradaExportador(EmbarqueCargador embarqueCargador, CargaTurnoCargasFacade ctcFacade) {
        this.embarqueCargador = embarqueCargador;
        this.cargaTurnoCargasF = ctcFacade;
        
        dataSource = new ArrayList<ResumenExportadorVO>();
        Map<Integer, ResumenExportadorVO> mapDataSource = new HashMap<Integer, ResumenExportadorVO>();
        Set<Mercaderia> mercaderiasCargador = new HashSet<Mercaderia>();
        BigDecimal totalCargas = BigDecimal.ZERO;
        
        for (CargaTurnoCargas ctc : cargaTurnoCargasF.obtenerCargas(embarqueCargador.getCargador(), embarqueCargador.getEmbarque())){
            if (ctc.getCarga().compareTo(BigDecimal.ZERO) > 0
                && (embarqueCargador.getMercaderia() == null
                    || (embarqueCargador.getMercaderia().equals(ctc.getMercaderiaBodega()))
                )){
                ResumenExportadorVO rExpVO = mapDataSource.get(ctc.getNroBodega());
                if (rExpVO == null){
                    rExpVO = new ResumenExportadorVO(ctc.getNroBodega(), embarqueCargador, ctc.getMercaderiaBodega());
                }

                mercaderiasCargador.add(ctc.getMercaderiaBodega());
                rExpVO.setCarga(rExpVO.getCarga().add(ctc.getCarga()));
                totalCargas = totalCargas.add(ctc.getCarga());


                mapDataSource.put(ctc.getNroBodega(), rExpVO);
            }
        }
        
        //No realizo la busqueda x base ya que la tengo todo precargado
        //Busco por cada cargaTurnoCarga del exportador en cuestion
        /*
        for (TurnoEmbarque te : embarque.getTurnoEmbarqueCollection()){
            for (CargaTurno ct : te.getCargaTurnoCollection()){
                if (ct.getCargador().equals(exportador)){
                    for (CargaTurnoCargas ctc : ct.getCargasCollection()){
                        if (ctc.getCarga().compareTo(BigDecimal.ZERO) > 0){
                            ResumenExportadorVO rExpVO = mapDataSource.get(ctc.getNroBodega());
                            if (rExpVO == null){
                                rExpVO = new ResumenExportadorVO(ctc.getNroBodega(), embarque, exportador, ctc.getMercaderiaBodega());
                            }
                            
                            mercaderiasCargador.add(ctc.getMercaderiaBodega());
                            rExpVO.setCarga(rExpVO.getCarga().add(ctc.getCarga()));
                            totalCargas = totalCargas.add(ctc.getCarga());
                            
                            
                            mapDataSource.put(ctc.getNroBodega(), rExpVO);
                        }
                    }
                }
            }
        }*/
        
        //termino de generar la lista y paso los valores de carga a kilos
        totalCargas = totalCargas.multiply(new BigDecimal(1000L));
        
        for (ResumenExportadorVO rExpVO : mapDataSource.values()){
            rExpVO.setMercaderiasCargadas(mercaderiasCargador);
            rExpVO.setCarga(rExpVO.getCarga().multiply(new BigDecimal(1000L)));
            rExpVO.setTotalCarga(totalCargas);
            dataSource.add(rExpVO);
        }
        
        Collections.sort(dataSource);
    }
    
    @Override
    public String obtenerReportePDF() {
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(dataSource);
        
        //Pongo el logo de la empresa
        try {
            FileInputStream imagen = new FileInputStream(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/uploadedFiles/logosEmpresas/" + embarqueCargador.getCargador().getId()));
            params.put("logoEmpresa", imagen);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  catch (IOException e){
            e.printStackTrace();
        }
                
                
        return printGenerico(ds, "DeclaracionJuradaExportador", "JURAR_" + embarqueCargador.getEmbarque().getCodigo() + "_" + embarqueCargador.getCargador().getNombre());
    }
    
}
