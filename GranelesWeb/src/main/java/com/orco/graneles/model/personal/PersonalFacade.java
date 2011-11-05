/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.model.personal;

import com.orco.graneles.domain.miscelaneos.EstadoCivil;
import com.orco.graneles.domain.miscelaneos.EstadoPersonal;
import com.orco.graneles.domain.miscelaneos.TipoDocumento;
import com.orco.graneles.domain.miscelaneos.TipoRecibo;
import com.orco.graneles.domain.personal.Personal;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.orco.graneles.model.AbstractFacade;
import com.orco.graneles.model.miscelaneos.FixedListFacade;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.persistence.NoResultException;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
/**
 *
 * @author orco
 */
@Stateless
public class PersonalFacade extends AbstractFacade<Personal> {
    @PersistenceContext(unitName = "com.orco_GranelesWeb_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @EJB
    private FixedListFacade fixedListFacade;
    @EJB
    private ObraSocialFacade obraSocialFacade;
    @EJB
    private CategoriaFacade categoriaFacade;
  
    
    protected EntityManager getEntityManager() {
        return em;
    }

    public PersonalFacade() {
        super(Personal.class);
    }
    
    public List<Personal> getPersonalMensualActivo(){
        return getEntityManager().createNamedQuery("Personal.findByTipoReciboYActivo", Personal.class)
                            .setParameter("tipoRecibo", fixedListFacade.find(TipoRecibo.MENSUAL))
                            .setParameter("estado", fixedListFacade.find(EstadoPersonal.ACTIVO))
                            .getResultList();        
    }
    
    /**
     * Lee de la tabla dbf y genera un map con la lista del personal, existente o no
     * @param tablaDBF tabla dbf del sistema legacy
     * @return Map con clave igual al cuil
     */
    public Map<String, Personal> obtenerPersonalDesdeDBF(InputStream tablaDBF){
        Map<String, Personal> mapPersonal = new HashMap<String, Personal> ();
        String cuilsErroneos = "";
        try
        {
            //Creo el libro y selecciono la primera hoja
            HSSFWorkbook workBook = new HSSFWorkbook(tablaDBF);
            HSSFSheet hssfSheet = workBook.getSheetAt(0);

            //Itero sobre las filas
            Iterator<Row> filaIterator = hssfSheet.rowIterator();
            filaIterator.next(); //Avanzo una fila ya que es la primera con los titulos de la tabla
            while (filaIterator.hasNext())
            {
                Row filaActual =  filaIterator.next();
                
                //Obtengo el cuil y de ahi empiezo a buscar
                String cuil = filaActual.getCell(6).getStringCellValue();
                
                Personal personalActual = null;
                try {
                    personalActual = getEntityManager().createNamedQuery("Personal.findByCuil", Personal.class)
                          .setParameter("cuil", cuil)
                          .getSingleResult();
                } catch (NoResultException e) {
                    personalActual = new Personal();
                    personalActual.setCuil(cuil);
                } catch (Exception e) {
                    personalActual = new Personal();
                    personalActual.setCuil(cuil);
                }
               
                
                try {
                    
                
                    //NroAfiliado
                    if (filaActual.getCell(0) != null)
                        personalActual.setNroAfiliado(Integer.parseInt(filaActual.getCell(0).getStringCellValue().replaceAll(" ", "")));

                    //NroDocumento
                    if (filaActual.getCell(1) != null)
                        personalActual.setDocumento(filaActual.getCell(1).getStringCellValue());

                    //Tipo de Documento
                    //TODO: CONTROLAR SI LOS TIPOS DE DOCUMENTOS SON LOS MISMOS
                    if (filaActual.getCell(2) != null) {
                        String tipoDocExcel = filaActual.getCell(2).getStringCellValue();
                        if (tipoDocExcel.equals("1") || tipoDocExcel.equals("D")){
                            personalActual.setTipoDocumento(fixedListFacade.find(TipoDocumento.DNI));
                        } else if (tipoDocExcel.equals("2")) {
                            personalActual.setTipoDocumento(fixedListFacade.find(TipoDocumento.LC));
                        } else if (tipoDocExcel.equals("3")) {
                            personalActual.setTipoDocumento(fixedListFacade.find(TipoDocumento.LE));
                        } else if (tipoDocExcel.equals("4") || tipoDocExcel.equals("X")) {
                            personalActual.setTipoDocumento(fixedListFacade.find(TipoDocumento.PASAPORTE));
                        }
                    }

                    //Fecha Ingreso
                    if (filaActual.getCell(3) != null)
                        personalActual.setIngreso(filaActual.getCell(3).getDateCellValue());

                    //Apellido y Nombres (se lo asigno a todos los apellidos)
                    //TODO: VER SI SE PUEDE EXTRAER CORRECTAMENTE EL NOMBRE DEL TIPO
                    if (filaActual.getCell(4) != null)
                        personalActual.setApellido(filaActual.getCell(4).getStringCellValue());

                    //Estado Civil
                    if (filaActual.getCell(5) != null){
                        String estadoCivilExcel = filaActual.getCell(5).getStringCellValue().toUpperCase();
                        if (estadoCivilExcel.equals("C")){
                            personalActual.setEstadoCivil(fixedListFacade.find(EstadoCivil.CASADO));
                        } else if (estadoCivilExcel.equals("S")){
                            personalActual.setEstadoCivil(fixedListFacade.find(EstadoCivil.SOLTERO));
                        } else if (estadoCivilExcel.equals("D")){
                            personalActual.setEstadoCivil(fixedListFacade.find(EstadoCivil.DIVORCIADO));
                        } else if (estadoCivilExcel.equals("V")){
                            personalActual.setEstadoCivil(fixedListFacade.find(EstadoCivil.VIUDO));
                        } 
                    }

                    //Registro
                    if (filaActual.getCell(7) != null)
                        personalActual.setRegistro(String.valueOf((new Double(filaActual.getCell(7).getNumericCellValue()).intValue())));

                    //SINDICADO (8)
                    if (filaActual.getCell(8) != null)
                        if (filaActual.getCell(8).getStringCellValue().equals("S")){
                            personalActual.setSindicato(Boolean.TRUE);
                        } else {
                            personalActual.setSindicato(Boolean.FALSE);
                        }
                    
                    //Direccion
                    if (filaActual.getCell(9) != null)
                        personalActual.setDomicilio(filaActual.getCell(9).getStringCellValue());

                    //Fecha Nacimiento
                    if (filaActual.getCell(10) != null)
                        personalActual.setFechaNacimiento(filaActual.getCell(10).getDateCellValue());

                    //Estado
                    if (filaActual.getCell(12) != null){
                        String activoJubiladoExcel = filaActual.getCell(12).getStringCellValue().toUpperCase();
                        if (activoJubiladoExcel.equals("A")){
                            personalActual.setEstado(fixedListFacade.find(EstadoPersonal.ACTIVO));
                        } else if (activoJubiladoExcel.equals("J")){
                            personalActual.setEstado(fixedListFacade.find(EstadoPersonal.JUBILADO));
                        }
                    }

                    //Categoria Principal
                    if (filaActual.getCell(13) != null)
                        personalActual.setCategoriaPrincipal(categoriaFacade.find(Integer.parseInt(filaActual.getCell(13).getStringCellValue())));

                    //Cuidad
                    if (filaActual.getCell(14) != null)
                        personalActual.setLocalidad(filaActual.getCell(14).getStringCellValue());

                    //Obra Social
                    if (filaActual.getCell(15) != null)
                        personalActual.setObraSocial(obraSocialFacade.find((new Double(filaActual.getCell(15).getNumericCellValue())).intValue()));

                    //Esposa
                    if (filaActual.getCell(16) != null)
                        if (filaActual.getCell(16).getStringCellValue().equals("S")){
                        personalActual.setEsposa(Boolean.TRUE);
                        } else {
                            personalActual.setEsposa(Boolean.FALSE);
                        }

                    //Hijos
                    if (filaActual.getCell(17) != null)
                        personalActual.setHijos((new Double(filaActual.getCell(17).getNumericCellValue())).intValue());

                    //Prenatal
                    if (filaActual.getCell(18) != null)
                        if (filaActual.getCell(18).getStringCellValue().equals("S")){
                        personalActual.setPrenatal(Boolean.TRUE);
                        } else {
                            personalActual.setPrenatal(Boolean.FALSE);
                        }

                    //Escolaridad
                    if (filaActual.getCell(19) != null)
                        personalActual.setEscolaridad((new Double(filaActual.getCell(19).getNumericCellValue())).intValue());

                    //Cta Bancaria
                    if (filaActual.getCell(20) != null)
                        personalActual.setCuentaBancaria(filaActual.getCell(20).getStringCellValue());
                    
                    //AFJP (21)
                    if (filaActual.getCell(8) != null)
                        if (filaActual.getCell(8).getStringCellValue().equals("S")){
                            personalActual.setAfjp(Boolean.TRUE);
                        } else {
                            personalActual.setAfjp(Boolean.FALSE);
                        }
                    
                    
                    //Tipo Recibo
                    if (filaActual.getCell(22) != null)
                        if (filaActual.getCell(22).getStringCellValue().equals("S")){
                            personalActual.setTipoRecibo(fixedListFacade.find(TipoRecibo.MENSUAL));
                        } else {
                            personalActual.setTipoRecibo(fixedListFacade.find(TipoRecibo.HORAS));
                        }

                    //Descuento Judicial
                    if (filaActual.getCell(0) != null)
                        personalActual.setDescuentoJudicial(new BigDecimal(new Double(filaActual.getCell(23).getNumericCellValue())));

                    
                    //TODO: FALTA VER CON FAMILIA (11)
                    
                } catch (Exception e) {
                    cuilsErroneos += cuil + ", ";
                }
                
                //Agrego el personal al map
                mapPersonal.put(cuil, personalActual);
            }
        }
        catch (Exception e)
        {
            mapPersonal = null;
        }

        
        
        return mapPersonal;
    }
    
    
}
