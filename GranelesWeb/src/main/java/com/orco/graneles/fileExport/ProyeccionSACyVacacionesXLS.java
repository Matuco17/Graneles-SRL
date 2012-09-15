/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.fileExport;

import com.orco.graneles.vo.ProyeccionSacVacYAdelantosVO;
import java.util.List;

/**
 *
 * @author orco
 */
public class ProyeccionSACyVacacionesXLS extends ExportadorXLSGenerico<ProyeccionSacVacYAdelantosVO> {

    @Override
    protected String getTemplate() {
        return "proyeccionSACyVacaciones";
    }

    public ProyeccionSACyVacacionesXLS(Integer semestre, Integer anio, List<ProyeccionSacVacYAdelantosVO> datos) {
        super(datos);
        
        addBean("nroPeriodo", semestre);
        addBean("anio", anio);
        addBean("proyecciones", datos);
    }
    
    
}
