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
                        StringUtils.rightPad(sdo.getPersonal().getApellido().toUpperCase().replaceAll("Ñ", "N"), 30, " ")
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
        
        //Codigo de situacion (2)
        linea.append("01");
     
        //Codigo de Condicion (2)
        linea.append("01");
        
        //Codigo de Actividad (2)
        linea.append("49"); 
        
        //Codigo de zona
        linea.append("07");
        
        //Porcentaje Aporte Adicional SS (5)
        linea.append(" 0.00"); 
        
        //Codigo modalidad de contratacion (3)
        linea.append("012"); 
        
        //Codigo de Obra social (6)
        linea.append(sdo.getPersonal().getObraSocial().getCodigoAfip());
        
        //Cantidad de Adherentes (2)
        linea.append("00");
        
        //Remuneration Total (9)
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        
        //Remuneracion Imponible 1 (9)
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        
        //Asignaciones Familiares Pagadas (9)
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        //Importe Aporte Voluntario
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        //Importe Adicional OS
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        //Importe Excedentes Aportes SS
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        //Importe Excedentes Aportes OS
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        //Provincia Localidad (50)
        linea.append(StringUtils.rightPad("Buenos Aires - Resto de la Provincia", 50, " "));
        
        //Remuneracion Imponible 2
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        
        //Remuneracion Imponible 3
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        
        //Remuneracion Imponible 4
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        
        //Codigo Siniestrado (2)
        linea.append("00");
        
        //Marca Correspondiente Reducción (1)
        linea.append("F");
        
        //Capital de Recomposición LRT
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        //Tipo Empresa (1)
        linea.append("1"); //0 o 1 
        
        //Aporte Adicional OS
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        //Regimen 
        linea.append("1"); //0 o 1 
        
        //Situacion Revista 1 (2)
        linea.append("01");
        
        //Dia Inicio Situacion Revista 1 (2)
        linea.append("01");
        
        //Situacion Revista 2 (2)
        linea.append("00");
        
        //Dia Inicio Situacion Revista 2 (2)
        linea.append("00");
        
        //Situacion Revista 3 (2)
        linea.append("00");
        
        //Dia Inicio Situacion Revista 3 (2)
        linea.append("00");
        
        
        //Sueldo más adicionales Sueldo = Horas habiles monto
        linea.append(formatearImporte9(
                    SueldoFacade.obtenerMontoXConcepto(sdo, TipoValorConcepto.HORAS_HABILES).add(                    
                    SueldoFacade.obtenerMontoXConcepto(sdo, TipoValorConcepto.PAGOS_ACCIDENTADO_ART))));
        
        //SAC
        linea.append(formatearImporte9(SueldoFacade.obtenerMontoXConcepto(sdo, TipoValorConcepto.SAC)));
        
        //Horas Extras Suedo = Horas habiles monto
        linea.append(formatearImporte9(SueldoFacade.obtenerMontoXConcepto(sdo, TipoValorConcepto.HORAS_EXTRAS)));
        
        //Zona desfavorable
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        //Vacaciones
        linea.append(formatearImporte9(SueldoFacade.obtenerMontoXConcepto(sdo, TipoValorConcepto.VACACIONES)));
        
        //Cantidad dias trabajados (9)
        if (sdo.getPersonal().getTipoRecibo().getId().equals(TipoRecibo.HORAS)){
            int horas = SueldoFacade.obtenerCantidadXConcepto(sdo, TipoValorConcepto.HORAS_HABILES).intValue();
            horas += SueldoFacade.obtenerCantidadXConcepto(sdo, TipoValorConcepto.HORAS_EXTRAS).intValue();
            int dias = Math.round(horas / 6);
            dias += SueldoFacade.obtenerCantidadXConcepto(sdo, TipoValorConcepto.PAGOS_ACCIDENTADO_ART).intValue();
            
            dias = Math.min(dias, diasMes);
            
            //Seteo al menos 1 dia si no tiene sino no importa bien el archivo
            if (dias == 0){
                dias = 1;
            }
            
            linea.append(StringUtils.right("000000000" + 
                                    String.valueOf(dias)
                                 , 9));
        } else {
            linea.append(StringUtils.right("000000000" + 
                                    (SueldoFacade.obtenerCantidadXConcepto(sdo, TipoValorConcepto.DIAS_TRABAJO).toBigInteger()).toString()
                                    , 9));
        }
        
        //Remuneración Imponible 5
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        
        //Trabajador convensionado (1=Si 01=No)
        linea.append("0");
        
        //Remuneracion Imponible 6
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        
        //Tipo Operacion
        linea.append("0");
        
        //Adicionales
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        //Premios
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        //Remuneracion Dec 788/05 Remuneracion Imponible 8
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        
        //Remuneracion Imponible 7
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        
        //Cantidad Horas extras (3)
        linea.append(StringUtils.right("000" + 
                                    (SueldoFacade.obtenerCantidadXConcepto(sdo, TipoValorConcepto.HORAS_EXTRAS).toBigInteger()).toString()
                                    , 3));
        
        //Conceptos No Remunerativos
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        //Maternidad
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        //Rectificacion de Remuneracion
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        //Remuneracion Imponible 9
        linea.append(formatearImporte9(sdo.getTotalRemunerativo(true)));
        
        //Contribucion tarea diferencial
        linea.append(formatearImporte9(BigDecimal.ZERO));
        
        //Horas Trabajadas (3)
        linea.append("000"); //Obs: no pongo nada ya que lo tengo entre las horas extras y los dias trabajados
        
        //Seguro colectivo de vida Obligatorio (1)
        linea.append("1");
        
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
