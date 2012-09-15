/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.vo;

import java.math.BigDecimal;

/**
 *
 * @author orco
 */
public class DescompisicionMoneda {
    private BigDecimal valor;
    
    private int p100;
    private int p50;
    private int p20;
    private int p10;
    private int p5;
    private int p2;
    private int p1;
    private int c50;
    private int c25;
    private int c10;
    private int c5;

    public DescompisicionMoneda(BigDecimal valor){
        this.valor = valor;
        
        //Genero la descomposicion
        this.p100 = descomponer(new BigDecimal(100.00), valor);
        valor = valor.subtract((new BigDecimal(100.00).multiply(new BigDecimal(this.p100))));
        
        this.p50 = descomponer(new BigDecimal(50.00), valor);
        valor = valor.subtract((new BigDecimal(50.00).multiply(new BigDecimal(this.p50))));
        
        this.p20 = descomponer(new BigDecimal(20.00), valor);
        valor = valor.subtract((new BigDecimal(20.00).multiply(new BigDecimal(this.p20))));
        
        this.p10 = descomponer(new BigDecimal(10.00), valor);
        valor = valor.subtract((new BigDecimal(10.00).multiply(new BigDecimal(this.p10))));
        
        this.p5 = descomponer(new BigDecimal(5.00), valor);
        valor = valor.subtract((new BigDecimal(5.00).multiply(new BigDecimal(this.p5))));
        
        this.p2 = descomponer(new BigDecimal(2.00), valor);
        valor = valor.subtract((new BigDecimal(2.00).multiply(new BigDecimal(this.p2))));
        
        this.p1 = descomponer(new BigDecimal(1.00), valor);
        valor = valor.subtract((new BigDecimal(1.00).multiply(new BigDecimal(this.p1))));
        
        this.c50 = descomponer(new BigDecimal(0.50), valor);
        valor = valor.subtract((new BigDecimal(0.50).multiply(new BigDecimal(this.c50))));
        
        this.c25 = descomponer(new BigDecimal(0.25), valor);
        valor = valor.subtract((new BigDecimal(0.25).multiply(new BigDecimal(this.c25))));
        
        this.c10 = descomponer(new BigDecimal(0.10), valor);
        valor = valor.subtract((new BigDecimal(0.10).multiply(new BigDecimal(this.c10))));
        
        this.c5 = descomponer(new BigDecimal(0.05), valor);
        valor = valor.subtract((new BigDecimal(0.05).multiply(new BigDecimal(this.c5))));
        
        //Realizo el redondeo
        if (valor.doubleValue() > 0.01){
            if (this.c5 == 0){
                this.c5 = 1;
            } else {
                this.c5 = 0;
                this.c10 += 1;
            }
        }
        
    }
    
    private int descomponer(BigDecimal unidad, BigDecimal valor){
        int cantidad = 0;
        while (unidad.doubleValue() < valor.doubleValue()){
            valor = valor.subtract(unidad);
            cantidad++;
        }
        return cantidad;
    }
    
    public void agregarDescomposicion(DescompisicionMoneda desc){
        if (desc.valor.doubleValue() > 0){
            this.valor = valor.add(desc.valor);
            this.p100 += desc.p100;
            this.p50 += desc.p50;
            this.p20 += desc.p20;
            this.p10 += desc.p10;
            this.p5 += desc.p5;
            this.p2 += desc.p2;
            this.p1 += desc.p1;
            this.c50 += desc.c50;
            this.c25 += desc.c25;
            this.c10 += desc.c10;
            this.c5 += desc.c5;    
        }                
    }
    
    public BigDecimal getValorTotalDescomposicion(){
        double suma = p100*100 + p50*50 + p20*20 + p10*10 + p5*5 + p2*2 + p1 + c50*0.5 + c25*0.25 + c10*0.1 + c5*0.05;
        return new BigDecimal(suma);    
    }
    
    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    
    public int getP100() {
        return p100;
    }

    public void setP100(int p100) {
        this.p100 = p100;
    }

    public int getP50() {
        return p50;
    }

    public void setP50(int p50) {
        this.p50 = p50;
    }

    public int getP20() {
        return p20;
    }

    public void setP20(int p20) {
        this.p20 = p20;
    }

    public int getP10() {
        return p10;
    }

    public void setP10(int p10) {
        this.p10 = p10;
    }

    public int getP5() {
        return p5;
    }

    public void setP5(int p5) {
        this.p5 = p5;
    }

    public int getP2() {
        return p2;
    }

    public void setP2(int p2) {
        this.p2 = p2;
    }

    public int getP1() {
        return p1;
    }

    public void setP1(int p1) {
        this.p1 = p1;
    }

    public int getC50() {
        return c50;
    }

    public void setC50(int c50) {
        this.c50 = c50;
    }

    public int getC25() {
        return c25;
    }

    public void setC25(int c25) {
        this.c25 = c25;
    }

    public int getC10() {
        return c10;
    }

    public void setC10(int c10) {
        this.c10 = c10;
    }

    public int getC5() {
        return c5;
    }

    public void setC5(int c5) {
        this.c5 = c5;
    }

    
}
