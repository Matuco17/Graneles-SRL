/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.fileExport;

import com.orco.graneles.domain.carga.TrabajadoresTurnoEmbarque;
import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.domain.salario.Sueldo;
import com.orco.graneles.vo.JornalVO;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author orco
 */
public class ResumenRemuneracionesXLS extends ExportadorXLSGenerico<Sueldo> {

    @Override
    protected String getTemplate() {
        return "resumenRemuneraciones";
    }

    public ResumenRemuneracionesXLS(Personal personal, Periodo desde, Periodo hasta, List<Sueldo> datos, String[] conceptos, BigDecimal[] totalConceptos) {
        super(datos);
        
        addBean("personal", personal);
        addBean("desde", (desde == null)? "" : desde.getDescripcion());
        addBean("hasta", (hasta == null)? "" : hasta.getDescripcion());
        addBean("conceptos", conceptos);
        addBean("totalConceptos", totalConceptos);
        addBean("cantidadConceptos", conceptos.length);
        addBean("sueldos", datos);
    }
    
    
}
