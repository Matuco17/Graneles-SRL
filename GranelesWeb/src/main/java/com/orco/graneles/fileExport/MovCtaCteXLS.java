/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.orco.graneles.fileExport;

import com.orco.graneles.domain.facturacion.Empresa;
import com.orco.graneles.domain.facturacion.MovimientoCtaCte;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author groupon
 */
public class MovCtaCteXLS extends ExportadorXLSGenerico<MovimientoCtaCte> {

    public MovCtaCteXLS(List<MovimientoCtaCte> datos, Empresa empresa, BigDecimal totalDebito, BigDecimal totalCredito, BigDecimal saldo) {
        super(datos);
        
        addBean("empresa", empresa);
        addBean("totalDebito", totalDebito);
        addBean("totalCredito", totalCredito);
        addBean("saldo", saldo);
        addBean("movimientos", datos);
    }

    @Override
    protected String getTemplate() {
        return "resumenCtaCteDinero";
    }
    
    
}
