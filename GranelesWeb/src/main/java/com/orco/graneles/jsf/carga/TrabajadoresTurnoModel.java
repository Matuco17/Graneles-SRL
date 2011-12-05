/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.jsf.carga;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.vo.TrabajadorTurnoEmbarqueVO;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author orco
 */
public class TrabajadoresTurnoModel extends ListDataModel<TrabajadorTurnoEmbarqueVO> implements SelectableDataModel<TrabajadorTurnoEmbarqueVO> {

    public TrabajadoresTurnoModel(){
    }
    
    public TrabajadoresTurnoModel(List<TrabajadorTurnoEmbarqueVO> data){
        super(data);
    }
    
    @Override
    public Object getRowKey(TrabajadorTurnoEmbarqueVO t) {
        if (t != null && t.getTte().getPersonal() != null)
            return t.getTte().getPersonal().getCuil();
        return null;
    }

    @Override
    public TrabajadorTurnoEmbarqueVO getRowData(String rowKey) {
        List<TrabajadorTurnoEmbarqueVO> trabajadores = (List<TrabajadorTurnoEmbarqueVO>) getWrappedData();
        
        for(TrabajadorTurnoEmbarqueVO tteVO : trabajadores)
            if (tteVO.getTte().getPersonal().getCuil().equalsIgnoreCase(rowKey))
                return tteVO;
        
        return null;
    }
    
    public int getRowIndex(TrabajadorTurnoEmbarqueVO t){
        List<TrabajadorTurnoEmbarqueVO> trabajadores = (List<TrabajadorTurnoEmbarqueVO>) getWrappedData();
        
        for(int i = 0; i < trabajadores.size(); i++)
            if (trabajadores.get(i).getTte().getPersonal().getCuil().equalsIgnoreCase((String) getRowKey(t)))
                return i;
                
        
        return -1;
    }
    
    
    
}
