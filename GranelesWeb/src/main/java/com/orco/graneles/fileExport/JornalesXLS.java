/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.fileExport;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Personal;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author orco
 */
public class JornalesXLS extends ExportadorXLSGenerico<TrabajadoresTurnoEmbarque> {

    @Override
    protected String getTemplate() {
        return "jornales";
    }

    public JornalesXLS(Categoria categoria, Personal personal, Date desde, Date hasta, Integer totalHoras, BigDecimal totalBruto, List<TrabajadoresTurnoEmbarque> datos) {
        super(datos);
        
        addBean("categoria", (categoria == null)? "Todas" : categoria.getDescripcion());
        addBean("cuil", (personal == null)? "Todos" : personal.getCuil());
        addBean("apellidoYNombre", (personal == null)? "" : personal.getApellido());
        addBean("apellidoYNombre", (personal == null)? "" : personal.getApellido());
        addBean("desde", desde);
        addBean("hasta", hasta);
        addBean("totalHoras", totalHoras);
        addBean("totalBruto", totalBruto);
        addBean("jornales", datos);
    }
    
    
}
