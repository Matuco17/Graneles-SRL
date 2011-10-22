/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.jsf.carga;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author orco
 */
public class TrabajadoresTurnoModel extends ListDataModel<TrabajadoresTurnoEmbarque> implements SelectableDataModel<TrabajadoresTurnoEmbarque> {

    public TrabajadoresTurnoModel(){
    }
    
    public TrabajadoresTurnoModel(List<TrabajadoresTurnoEmbarque> data){
        super(data);
    }
    
    @Override
    public Object getRowKey(TrabajadoresTurnoEmbarque t) {
        if (t != null && t.getPersonal() != null)
            return t.getPersonal().getCuil();
        return null;
    }

    @Override
    public TrabajadoresTurnoEmbarque getRowData(String rowKey) {
        List<TrabajadoresTurnoEmbarque> trabajadores = (List<TrabajadoresTurnoEmbarque>) getWrappedData();
        
        for(TrabajadoresTurnoEmbarque tte : trabajadores)
            if (tte.getPersonal().getCuil().equalsIgnoreCase(rowKey))
                return tte;
        
        return null;
    }
    
    public int getRowIndex(TrabajadoresTurnoEmbarque t){
        List<TrabajadoresTurnoEmbarque> trabajadores = (List<TrabajadoresTurnoEmbarque>) getWrappedData();
        
        for(int i = 0; i < trabajadores.size(); i++)
            if (trabajadores.get(i).getPersonal().getCuil().equalsIgnoreCase((String) getRowKey(t)))
                return i;
                
        
        return -1;
    }
    
    
    
}
