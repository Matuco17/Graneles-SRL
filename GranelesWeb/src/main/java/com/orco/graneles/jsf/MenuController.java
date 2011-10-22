/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author orco
 */
@ManagedBean(name = "menuController")
@RequestScoped
public class MenuController {

    public String libroSueldos(){
        return "/periodo/Periodo.xhtml";
    }
    
    
    
    
    /** Creates a new instance of MenuController */
    public MenuController() {
    }
    
}
