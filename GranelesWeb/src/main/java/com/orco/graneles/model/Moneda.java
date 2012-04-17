/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.orco.graneles.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 *
 * @author orco
 */
public class Moneda extends BigDecimal {

    public Moneda(BigInteger val) {
        super(val);
        this.setScale(2, RoundingMode.HALF_UP);
    }

    public Moneda(String val) {
        super(val);
        this.setScale(2, RoundingMode.HALF_UP);
    }

    public Moneda(char[] in) {
        super(in);
        this.setScale(2, RoundingMode.HALF_UP);
    }

    public Moneda(double val) {
        super(val);
        this.setScale(2, RoundingMode.HALF_UP);
    }

    public Moneda(int val) {
        super(val);
        this.setScale(2, RoundingMode.HALF_UP);
    }

    public Moneda(long val) {
        super(val);
        this.setScale(2, RoundingMode.HALF_UP);
    }

}
