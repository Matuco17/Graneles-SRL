/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.fileExport;

import com.orco.graneles.domain.salario.Sueldo;
import com.orco.graneles.domain.miscelaneos.TipoValorConcepto;
import com.orco.graneles.model.salario.SueldoFacade;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author orco
 */
public class LibroSueldosAFIP extends ExportadorGenerico<Sueldo> {


    
   
    @Override
    protected String convertirElementoEnLinea(Sueldo sdo) {
        StringBuilder linea = new StringBuilder();
        
        //Agrego uno por uno todos los campos con sus longitudes pertinetes en (x) indica la longitud fija de cada campo
        //CUIL sin guiones (11)
        linea.append(StringUtils.rightPad(sdo.getPersonal().getCuil().replaceAll("-", ""), 11));
        
        int i = 0;
        
        //Apellido y Nombre (30)
        linea.append(StringUtils.left(
                        StringUtils.rightPad(sdo.getPersonal().getApellido(), 30, " ")
                        ,30));
        
        //Conyuge (si 1 no 0) (1)
        if (sdo.getPersonal().getEsposa()){
            linea.append("1");
        } else {
            linea.append("0");
        }
        
        
        //Cantidad de Hijos (2)
        if (sdo.getPersonal().getHijos() != null){
            linea.append(StringUtils.leftPad(sdo.getPersonal().getHijos().toString(), 2, "0"));
        } else {
            linea.append("00");
        }
        
        //Codigo de situacion (3)
        linea.append("001"); 
        
        //Codigo de Condicion (3)
        linea.append("001"); 
        
        //Codigo de Actividad (3)
        linea.append("049"); 
        
        //Codigo de zona
        linea.append("000"); 
        
        //Porcentaje Aporte Adicional SS (5)
        linea.append("00000"); 
        
        //Porcentaje de Reduccion (3)
        linea.append("000"); 
        
        //Codigo modalidad de contratacion (3)
        linea.append("012"); 
        
        //Codigo de Obra social (6)
        linea.append(sdo.getPersonal().getObraSocial().getCodigoAfip());
        
        //Cantidad de Adherentes (2)
        linea.append("00");
        
        //Remuneration Total (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true)));
        
        //Remuneracion Imponible Aportes (15)
        linea.append(formatearImporte15(sdo.getTotalSueldoNeto(true)));
        
        //Remuneracion Imponible Contribuciones (15)
        linea.append(formatearImporte15(sdo.getTotalSueldoNeto(true)));
        
        //Asignaciones Familiares Pagadas (15)
        linea.append(formatearImporte15(BigDecimal.ZERO));
        
        //Importe Aporte SIJP (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true).multiply(new BigDecimal(0.11f)))); 
        
        //Importe aporte INSSJP (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true).multiply(new BigDecimal(0.03f)))); 
        
        //Importe aporte Adicional SS (15)
        linea.append(formatearImporte15(BigDecimal.ZERO)); 
        
        //Importe aporte Voluntario (15)
        linea.append(formatearImporte15(BigDecimal.ZERO));
        
        //Importe Excedentes Aportes SS (15)
        linea.append(formatearImporte15(BigDecimal.ZERO)); 
        
        //Importe Neto Total Aportes SS (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true).multiply(new BigDecimal(0.143f)))); 
        
        //Importe aporte OS (15)
        linea.append(formatearImporte15(SueldoFacade.obtenerMontoXConcepto(sdo, TipoValorConcepto.OBRA_SOCIAL))); 
        
        //Importe Adicional OS (15)
        linea.append(formatearImporte15(BigDecimal.ZERO)); 
        
        //Importe Aporte ANSSAL (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true).multiply(new BigDecimal(0.003f))));
        
        //Importe Excedente Aportes OS (15)
        linea.append(formatearImporte15(BigDecimal.ZERO));
        
        //Importe Total Aportes OS (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true).multiply(new BigDecimal(0.027f))));
        
        //Importe Contribucion SIJP (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true).multiply(new BigDecimal(0.1017f)))); 
        
        //Importe Contribucion INSSJP (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true).multiply(new BigDecimal(0.015f)))); 
        
        //Importe Contribucion FNE (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true).multiply(new BigDecimal(0.0089f))));
        
        //Importe Contribuion Asig Familiares (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true).multiply(new BigDecimal(0.0444f)))); 
        
        //Importe Total Contribuciones (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true).multiply(new BigDecimal(0.176f)))); 
        
        //Importe Contribicion OS (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true).multiply(new BigDecimal(0.054f))));
        
        //Importe Contribucion ANSSAL (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true).multiply(new BigDecimal(0.006f)))); 
        
        //Provincia Localidad (50)
        linea.append(StringUtils.rightPad("BUENOS AIRES-RESTO PROVINCIA DE BUENOS AIRES", 50, " "));
        
        //Importe Total Contribuciones OS (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true).multiply(new BigDecimal(0.054f)))); 
        
        //Codigo de Siniestrado (2)
        linea.append("00");
       
        //Marca de Correspondiente Reduccion (1)
        linea.append("0"); 
        
        //Remuneracion Imponible 3 (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true))); 
        
        //Remuneracion Imponible 4 (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true)));
        
        //Aporte adicional OS (15)
        linea.append(formatearImporte15(BigDecimal.ZERO)); 
        
        //Capital de recomposición LRT (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true).multiply(new BigDecimal(0.00001f)))); 
        
        //Tipo Emplesa (1)
        linea.append("0"); 
        
        //Tipo Regimen (1)
        linea.append("0"); 
        
        //Renatre (15)
        linea.append(formatearImporte15(BigDecimal.ZERO)); 
        
        //Dias Trabajados (2)
        linea.append(StringUtils.right("00" + 
                                (SueldoFacade.obtenerCantidadXConcepto(sdo, TipoValorConcepto.DIAS_TRABAJO).toBigInteger()
                            .add(SueldoFacade.obtenerCantidadXConcepto(sdo, TipoValorConcepto.HORAS_HABILES).toBigInteger().divide(new BigInteger("6")))).toString()           ,
                                2));
        
        //Hs Extra Monto
        linea.append(formatearImporte15(SueldoFacade.obtenerMontoXConcepto(sdo, TipoValorConcepto.HORAS_EXTRAS))); //TODO: VER COMO COMPLETAR Hs Extra Monto
        
        //SAC
        linea.append(formatearImporte15(SueldoFacade.obtenerMontoXConcepto(sdo, TipoValorConcepto.SAC)));
        
        //Sueldo Adic
        linea.append(formatearImporte15(BigDecimal.ZERO));
        
        //Vacaciones (15)
        linea.append(formatearImporte15(SueldoFacade.obtenerMontoXConcepto(sdo, TipoValorConcepto.VACACIONES)));
        
        //Zona Desfavorable (15)
        linea.append(formatearImporte15(BigDecimal.ZERO));
        
        //Situacion 1 (3)
        linea.append("ACT");
        
        //Situacion 2 (3)
        linea.append("   ");
        
        //Situacion 3 (3)
        linea.append("   "); 
        
        //Dia 1 (2)
        linea.append("01"); 
        
        //Dia 2 (2)
        linea.append("00"); 
        
        //Dia 3 (2)
        linea.append("00"); 
        
        //Remuneracion Imponible 5 (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true))); 
        
        //Marca Convencionado
        linea.append(" "); 
               
        //Dto1253OS (15)
        linea.append(formatearImporte15(BigDecimal.ZERO)); //TODO: VER COMO COMPLETAR Dto1273OS
        
        //Dto1253INSSJP (15)
        linea.append(formatearImporte15(BigDecimal.ZERO)); //TODO: VER COMO COMPLETAR Dto1273INSSJP
        
        //Remuneracion Imponible 6 (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true))); 
        
        //Aporte diferencial SIJIP (15)
        linea.append(formatearImporte15(BigDecimal.ZERO));
        
        //Tipo Operacion (1)
        linea.append(" ");
        
        //Adicionales (15)
        linea.append(formatearImporte15(BigDecimal.ZERO)); 
        
        //Premios (15)
        linea.append(formatearImporte15(BigDecimal.ZERO)); 
        
        //Cantidad Horas Extras (3)
        linea.append(StringUtils.right("000" +
                                SueldoFacade.obtenerCantidadXConcepto(sdo, TipoValorConcepto.HORAS_EXTRAS).toBigInteger().toString(),
                                3)); 
        
        //Sueldo Dto 788_05 Rem 8 (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true).multiply(new BigDecimal(0.000012f)))); 
        
        //Aportes SS Dto 788_05 (15)
        linea.append(formatearImporte15(BigDecimal.ZERO)); //TODO: VER COMO COMPLETAR Aportes SS Dto 788_05
        
        //Remuneracion Imponible 7 (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true))); 
        
        //Aportes Res 33_41_SSS (15)
        linea.append(formatearImporte15(BigDecimal.ZERO)); //TODO: VER COMO COMPLETAR Aportes Res 33_41_SSS
        
        //Remuneracion Imponible 8 (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true))); 
        
        //Conceptos no Remunerativos (15)
        linea.append(formatearImporte15(BigDecimal.ZERO));
        
        //Rectificacion de Remuneracion (15)
        linea.append(formatearImporte15(BigDecimal.ZERO)); 
        
        //Maternidad (15)
        linea.append(formatearImporte15(BigDecimal.ZERO)); 
        
        //Remuneracion Imponible 9 (15)
        linea.append(formatearImporte15(sdo.getTotalRemunerativo(true))); 
        
        //Cantidad de Horas Trabajadas (3)
        linea.append(StringUtils.right("000" + 
                                (SueldoFacade.obtenerCantidadXConcepto(sdo, TipoValorConcepto.HORAS_EXTRAS)
                                .add(SueldoFacade.obtenerCantidadXConcepto(sdo, TipoValorConcepto.HORAS_HABILES))              
                                ).toBigInteger().toString(), 3)); //TODO: VER COMO CALCULAR Cantidad Horas trabajadas, verificar si eso que dice importe es correcto ya que me parece q es incorrecto
        
        //Porcentaje Tarea Dif (15)
        linea.append(formatearImporte15(BigDecimal.ZERO)); 
                
        //Importe Contribucion Tarea Dif (15)
        linea.append(formatearImporte15(BigDecimal.ZERO)); 
        
        //Importe Contribucion Tarea Dif Compen (15)
        linea.append(formatearImporte15(BigDecimal.ZERO)); 
        
        return linea.toString();
    }

    public LibroSueldosAFIP(List<Sueldo> datos) {
        super(datos);
    }
    
    
    
}
