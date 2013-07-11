/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.fileExport;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.vo.JornalVO;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author orco
 */
public class JornalesXLS extends ExportadorXLSGenerico<JornalVO> {

    @Override
    protected String getTemplate() {
        return "jornales";
    }

    public JornalesXLS(Categoria categoria, Personal personal, Date desde, Date hasta, Boolean incluirFeriados, Integer totalHoras, BigDecimal totalBruto, List<JornalVO> datos) {
        super(datos);
        
        addBean("categoria", (categoria == null)? "Todas" : categoria.getDescripcion());
        addBean("cuil", (personal == null)? "Todos" : personal.getCuil());
        addBean("apellidoYNombre", (personal == null)? "" : personal.getApellido());
        addBean("apellidoYNombre", (personal == null)? "" : personal.getApellido());
        addBean("desde", desde);
        addBean("hasta", hasta);
        addBean("totalHoras", totalHoras);
        addBean("totalBruto", totalBruto);
        addBean("incluirFeriado", (incluirFeriados) ? "Si" : "No");
        addBean("jornales", datos);
    }
    
    
}
