/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.vo;

import java.math.BigDecimal;

public class NumberToStringConverter {

  private static String[] _grupos = 
    { "", "millon","billon","trillon"  };
  
  private static String[] _unidades = 
    { "","un","dos","tres","cuatro","cinco","seis","siete","ocho","nueve" };
  
  private static String[] _decena1 = 
    { "","once","doce","trece","catorce","quince",
      "dieciseis","diecisiete","dieciocho","diecinueve" };
  private static String[] _decenas = 
    { "","diez","veinte","treinta","cuarenta","cincuenta",
      "sesenta","setenta","ochenta","noventa"};
  
  private static String[] _centenas = 
    { "","cien","doscientos","trescientos","cuatrocientos",
      "quinientos","seiscientos","setecientos","ochocientos","novecientos"};
 
  public static String millarATexto( int n ) {
    if (n == 0)
      return "";
      
    int centenas = n / 100;
    n = n % 100;
    int decenas = n / 10;
    int unidades = n % 10;
    
    String sufijo = "";
    
    if ( decenas == 0 && unidades != 0 ) 
      sufijo = _unidades[unidades];
    
    if ( decenas == 1 && unidades != 0 )
      sufijo = _decena1[unidades];
    
    if ( decenas == 2 && unidades != 0 )
      sufijo   = "veinti"+_unidades[unidades];
    
    if ( unidades == 0) 
      sufijo = _decenas[decenas];
    
    if ( decenas > 2 && unidades != 0)
      sufijo = _decenas[decenas] + " y " + _unidades[unidades];
    
    if (centenas != 1)
      return _centenas[centenas] + " " + sufijo;
    
    if ( unidades == 0 && decenas == 0)
      return "cien";
    
    return "ciento "+sufijo; 
  }
  
  public static String numeroACastellano( long n  ){
    String resultado = "";
    boolean esNegativo = false;
    
    if (n < 0){
        esNegativo = true;
        n = (-1) * n;
    }
    
    int grupo = 0;
    while ( n != 0 && grupo < _grupos.length ) {
      long fragmento = n % 1000000;
      int millarAlto = (int) (fragmento / 1000);
      int millarBajo = (int) (fragmento % 1000);
      n = n / 1000000;
      
      String nombreGrupo = _grupos[grupo];
      if (fragmento > 1 && grupo > 0)
        nombreGrupo += "es";
      
      
      if ((millarAlto != 0) || (millarBajo != 0)) {
        if (millarAlto > 1)
           resultado = millarATexto(millarAlto) + " mil " + 
                       millarATexto(millarBajo) + " " +
                       nombreGrupo + " " +
                       resultado;
                    
        
        if (millarAlto == 0) 
          resultado = millarATexto(millarBajo) + " " +
                      nombreGrupo + " "+
                      resultado;
                      
        if (millarAlto == 1)
           resultado = "mil " + millarATexto(millarBajo) + " " +
                       nombreGrupo + " " +
                       resultado;
      }
      grupo++;
    }
    
    if (esNegativo){
        resultado = "menos " + resultado;
    }
    
    return resultado;
  }
  
  public static String decimalACastellano(BigDecimal valor, int decimales, String separador, String finalizador){
      return (numeroACastellano(valor.longValue()) 
             + separador 
             + numeroACastellano(
                        (new Double(
                            Math.round(
                                valor.doubleValue() 
                                    * (Math.pow(10, decimales))
                            ) % (Math.pow(10, decimales)))
                        ).longValue())
             + finalizador).trim();
  }
  
  
}


