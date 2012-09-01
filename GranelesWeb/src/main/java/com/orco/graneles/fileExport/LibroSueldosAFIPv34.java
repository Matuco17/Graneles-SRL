/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.fileExport;

import com.orco.graneles.domain.miscelaneos.TipoRecibo;
import com.orco.graneles.domain.salario.Sueldo;
import com.orco.graneles.domain.miscelaneos.TipoValorConcepto;
import com.orco.graneles.model.salario.SueldoFacade;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

/**
 *
 * @author orco
 */
public class LibroSueldosAFIPv34 extends ExportadorGenerico<Sueldo> {

    int diasMes = 30;
    
   
    @Override
    protected String convertirElementoEnLinea(Sueldo sdo) {
        StringBuilder linea = new StringBuilder();
        
        //Agrego uno por uno todos los campos con sus longitudes pertinetes en (x) indica la longitud fija de cada campo
        //CUIL sin guiones (11)
        linea.append(StringUtils.rightPad(sdo.getPersonal().getCuil().replaceAll("-", ""), 11));
        
        //Apellido y Nombre (30)
        linea.append(StringUtils.left(
                        StringUtils.rightPad(sdo.getPersonal().getApellido(), 30, " ")
                        ,30));
        
        //Conyuge (si T no F) (1)
        if (sdo.getPersonal().getEsposa()){
            linea.append("T");
        } else {
            linea.append("F");
        }
        
        
        //Cantidad de Hijos (2)
        if (sdo.getPersonal().getHijos() != null){
            linea.append(StringUtils.leftPad(sdo.getPersonal().getHijos().toString(), 2, "0"));
        } else {
            linea.append("00");
        }
        
        //Codigo de situacion (3)
        linea.append(" 1"); //2
        
        //Codigo de Condicion (3)
        linea.append(" 1"); //2
        
        //Codigo de Actividad (3)
        linea.append("49"); //2
        
        //Codigo de zona
        linea.append("07");//2 
        
        //Porcentaje Aporte Adicional SS (5)
        linea.append(" 0.00"); 
        
        //Porcentaje de Reduccion (3)
        linea.append(" ");//1 
        
        //Codigo modalidad de contratacion (3)
        linea.append("12"); //2 
        
        //Codigo de Obra social (6)
        linea.append(sdo.getPersonal().getObraSocial().getCodigoAfip());
        
        //Cantidad de Adherentes (2)
        linea.append("00");
        
        //Remuneration Total (15)
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        
        //Remuneracion Imponible Aportes (15)
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        
        linea.append(formatearImporte9(BigDecimal.ZERO));
        linea.append(formatearImporte9(BigDecimal.ZERO));
        linea.append(formatearImporte9(BigDecimal.ZERO));
        linea.append(formatearImporte9(BigDecimal.ZERO));
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        //Provincia Localidad (50)
        linea.append(StringUtils.rightPad("Buenos Aires - Resto de la Provincia", 50, " "));
        
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        
        linea.append("00F");
        
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        linea.append("1"); //0 o 1 
        
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        //TODO: por ahora le pongo un cero!!!!!!
        linea.append("0"); //0 o 1 
        
        linea.append(" 1 1 0 0 0 0");
        
        //Suedo = Horas habiles monto
        linea.append(formatearImporte9(
                    SueldoFacade.obtenerMontoXConcepto(sdo, TipoValorConcepto.HORAS_HABILES).add(                    
                    SueldoFacade.obtenerMontoXConcepto(sdo, TipoValorConcepto.PAGOS_ACCIDENTADO_ART))));
        
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        //Suedo = Horas habiles monto
        linea.append(formatearImporte9(SueldoFacade.obtenerMontoXConcepto(sdo, TipoValorConcepto.HORAS_EXTRAS)));
        
        linea.append(formatearImporte9(BigDecimal.ZERO));
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        linea.append("       "); //7
        
        if (sdo.getPersonal().getTipoRecibo().getId().equals(TipoRecibo.HORAS)){
            int horas = SueldoFacade.obtenerCantidadXConcepto(sdo, TipoValorConcepto.HORAS_HABILES).intValue();
            horas += SueldoFacade.obtenerCantidadXConcepto(sdo, TipoValorConcepto.HORAS_EXTRAS).intValue();
            int dias = Math.round(horas / 6);
            dias += SueldoFacade.obtenerCantidadXConcepto(sdo, TipoValorConcepto.PAGOS_ACCIDENTADO_ART).intValue();
            
            dias = Math.min(dias, diasMes);
            
            linea.append(StringUtils.right("  " + 
                                    String.valueOf(dias)
                                 , 2));
        } else {
            linea.append(StringUtils.right("  " + 
                                    (SueldoFacade.obtenerCantidadXConcepto(sdo, TipoValorConcepto.DIAS_TRABAJO).toBigInteger()).toString()
                                    , 2));
        }
        
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        
        linea.append("0");
        
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        
        linea.append(" ");
        
        linea.append(formatearImporte9(BigDecimal.ZERO));
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        linea.append(StringUtils.right("   " + 
                                    (SueldoFacade.obtenerCantidadXConcepto(sdo, TipoValorConcepto.HORAS_EXTRAS).toBigInteger()).toString()
                                    , 3));
        
        linea.append(formatearImporte9(BigDecimal.ZERO));
        linea.append(formatearImporte9(BigDecimal.ZERO));
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        return linea.toString();
    }

    public LibroSueldosAFIPv34(List<Sueldo> datos) {
        super(datos);
    }

    @Override
    public String generarArchivo(String nombreArchivo) {
        Collections.sort(datosAExportar, new ComparadorSueldos());
        
        if (datosAExportar != null && !datosAExportar.isEmpty()){
            diasMes = (new DateTime(datosAExportar.get(0).getPeriodo().getHasta())).getDayOfMonth();
        }
        
        return super.generarArchivo(nombreArchivo);
    }
    
    
    
    private class ComparadorSueldos implements Comparator<Sueldo>{

        @Override
        public int compare(Sueldo o1, Sueldo o2) {
            return o1.getPersonal().getCuil().compareToIgnoreCase(o2.getPersonal().getCuil());
        }
        
    }
    
}
