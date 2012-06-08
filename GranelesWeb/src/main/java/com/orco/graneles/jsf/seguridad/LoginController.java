/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.jsf.seguridad;

import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.domain.seguridad.Usuario;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.seguridad.UsuarioFacade;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.faces.context.ExternalContext;
/**
 *
 * @author Administrador
 */
@ManagedBean(name = "loginController")
@SessionScoped
public class LoginController {

    private String username;
    private String password;
    private Usuario usuarioLogueado;
    
    @EJB
    private UsuarioFacade usuarioF;

    ExternalContext contexto;
    
    public String login(){
        try{
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            
            usuarioLogueado = usuarioF.find(username);
            
            if (usuarioLogueado != null){
                if (request.getUserPrincipal() != null){
                    request.logout();
                }
                
                request.login(username, password);     
                
                username = null;
                password = null;
                //context.getExternalContext().redirect((String)request.getAttribute("from"));
                return "/index?faces-redirect=true";
            } else {
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundleSeguridad").getString("UserPasswordLoginError"));
                usuarioLogueado = null;
            }
            
        } catch(Exception e){
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/BundleSeguridad").getString("UserPasswordLoginError"));
            usuarioLogueado = null;
            
        }
        //Si no ocurre nada satisfactorio, me quedo en la página de login
        return null;
    }
    
    public String logout(){
        try{
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            
            usuarioLogueado = null;
            username = null;
            password = null;
            
            request.logout();
            
            return "/index?faces-redirect=true";
            
        } catch(Exception e){
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/C").getString("LogoutError"));
            return null;
        }
    }
    
    public boolean esUsuario(){
        return getContexto().isUserInRole(Grupo.ROL_USUARIO);
    }
    
    public boolean esGerente(){
        return getContexto().isUserInRole(Grupo.ROL_GERENTE);
    }
    
    public boolean esAdministrador(){
        return getContexto().isUserInRole(Grupo.ROL_ADMINISTRADOR);
    }
    

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }
    
    public boolean isAutenticado(){
        return (this.usuarioLogueado != null);
    }

    public ExternalContext getContexto() {
        if (contexto == null)
            contexto = FacesContext.getCurrentInstance().getExternalContext();
        return contexto;
    }
 
    
    
    /** Creates a new instance of LoginController */
    public LoginController() {
    }
}
