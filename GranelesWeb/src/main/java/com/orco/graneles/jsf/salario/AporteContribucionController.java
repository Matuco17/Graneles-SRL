package com.orco.graneles.jsf.salario;

import com.orco.graneles.domain.salario.AporteContribucionConfiguracion;
import com.orco.graneles.domain.seguridad.Grupo;
import com.orco.graneles.jsf.util.JsfUtil;
import com.orco.graneles.model.salario.AporteConfiguracionConfiguracionFacade;

import java.io.Serializable;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

@ManagedBean(name = "aporteContribucionController")
@SessionScoped
public class AporteContribucionController implements Serializable {

    private DataModel items = null;
    @EJB
    private AporteConfiguracionConfiguracionFacade ejbFacade;
    
    private List<AporteContribucionConfiguracion> list;
    

    public AporteContribucionController() {
    }

    public void init() {
        items = null;
        list = null;
        JsfUtil.minimoRolRequerido(Grupo.ROL_GERENTE);
    }

    public List<AporteContribucionConfiguracion> getList() {
        if (list == null){
            list = ejbFacade.findAll();
            Collections.sort(list);
        }
        return list;
    }

    public DataModel getItems() {
        if (items == null){
            items = new ListDataModel(getList());
        }
        return items;
    }

    public String update(){
       try {
               
            for (AporteContribucionConfiguracion acc : getList()){
                 ejbFacade.edit(acc);
            }
            items = null;
            list = null;

            JsfUtil.addSuccessMessage("Porcentajes de Aportes y Contribuciones Guardado");
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleSalario").getString("PersistenceErrorOccured"));
        }
        return null;        
    }
    
}