/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orco.graneles.reports;

import com.orco.graneles.domain.salario.Periodo;
import com.orco.graneles.domain.salario.ItemsSueldo;
import com.orco.graneles.domain.salario.ConceptoRecibo;
import com.orco.graneles.domain.salario.Sueldo;
import com.orco.graneles.domain.personal.Personal;
import com.orco.graneles.domain.personal.Categoria;
import com.orco.graneles.domain.miscelaneos.FixedList;
import com.lowagie.text.pdf.AcroFields.Item;
import com.orco.graneles.domain.*;
import com.orco.graneles.domain.carga.Embarque;
import com.orco.graneles.domain.miscelaneos.EstadoCivil;
import com.orco.graneles.domain.miscelaneos.TipoConceptoRecibo;
import com.orco.graneles.domain.miscelaneos.TipoDocumento;
import com.orco.graneles.domain.miscelaneos.TipoRecibo;
import com.orco.graneles.domain.miscelaneos.TipoValorConcepto;
import com.orco.graneles.vo.ResumenCargaEmbarqueVO;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

/**
 * Clase encargada de contener métodos de creación de entidades dummies 
 * útiles para Tests y para la importación desde Jasper Reports
 * @author orco
 */
public class DummiesFactory {
    
    private static Random rnd;
    
    private static int obtenerRandom(){
        if (rnd == null){
            rnd = new Random();
        }
        return rnd.nextInt();
    }

    
    public static Collection<ItemsSueldo> itemsSueldoPeriodo(){
        List<ItemsSueldo> items = new ArrayList<ItemsSueldo>();
        
        //Agrego todos los items del periodo para el reporte
        Periodo per = crearPeriodo();
        for(Sueldo s : per.getSueldoCollection())
            for (ItemsSueldo is : s.getItemsSueldoCollection())
                items.add(is);
                
        //Ordeno antes de devolver todo
        //Collections.sort(items);
        
        return items;
    }
    
    public static List<ResumenCargaEmbarqueVO> crearResumenEmbarque(){
        List<ResumenCargaEmbarqueVO> resumenes = new ArrayList<ResumenCargaEmbarqueVO>();
        resumenes.add(new ResumenCargaEmbarqueVO(new Embarque()));
        return resumenes;
    }
    
    public static Periodo crearPeriodo(){
        Periodo periodoDummy = new Periodo();
        
        Calendar calHoy = new GregorianCalendar();
        Calendar calDesde = new GregorianCalendar();
        calDesde.set(Calendar.DAY_OF_MONTH, 1);
        Calendar calHasta = new GregorianCalendar();
        calHasta.set(Calendar.DAY_OF_MONTH, calHasta.getMaximum(Calendar.DAY_OF_MONTH));
        
        //Creo el periodo
        periodoDummy.setDescripcion(String.valueOf(calHoy.get(Calendar.YEAR)) + "-" + String.valueOf(calHoy.get(Calendar.MONTH) + 1));
        periodoDummy.setDesde(calDesde.getTime());
        periodoDummy.setHasta(calHasta.getTime());
        periodoDummy.setFolioLibro("Folio:" + String.valueOf(obtenerRandom()));
        periodoDummy.setNroPrimeraHoja(BigInteger.ONE);
        periodoDummy.setSueldoCollection(new ArrayList<Sueldo>());
              
        //Agrego 10 items de sueldo
        for (int i=0; i<9; i++){
            Sueldo s = crearSueldo();
            s.setPeriodo(periodoDummy);
            periodoDummy.getSueldoCollection().add(s);
        }
       
        
        return periodoDummy;
    }
    
    public static Collection<Sueldo> crearListaSueldos(){
        return crearPeriodo().getSueldoCollection();
    }
    
    public static Sueldo crearSueldo(){
        Sueldo sueldoDummy = new Sueldo();
        
        sueldoDummy.setNroRecibo(obtenerRandom());
        sueldoDummy.setPersonal(crearPersonal());
        sueldoDummy.setItemsSueldoCollection(new ArrayList<ItemsSueldo>());
        sueldoDummy.setId(new Long(obtenerRandom()));
        
        //Creo los Items del sueldo
        //Item Remunerativo
        ItemsSueldo itemDummy1 = new ItemsSueldo();
        itemDummy1.setConceptoRecibo(crearConceptoRecibo(TipoConceptoRecibo.REMUNERATIVO));
        BigDecimal valor1 = new BigDecimal(obtenerRandom());
        itemDummy1.setValorCalculado(valor1);
        itemDummy1.setValorIngresado(valor1);
        itemDummy1.setSueldo(sueldoDummy);
        sueldoDummy.getItemsSueldoCollection().add(itemDummy1);
        
        //Item Deductivo 1
        ItemsSueldo itemDummy2 = new ItemsSueldo();
        itemDummy2.setConceptoRecibo(crearConceptoRecibo(TipoConceptoRecibo.DEDUCTIVO));
        itemDummy2.setValorCalculado(valor1.multiply(new BigDecimal(0.11)));
        itemDummy2.setValorIngresado(valor1.multiply(new BigDecimal(0.11)));
        itemDummy2.setSueldo(sueldoDummy);
        sueldoDummy.getItemsSueldoCollection().add(itemDummy2);
        
        //Item Deductivo 2
        ItemsSueldo itemDummy3 = new ItemsSueldo();
        itemDummy3.setConceptoRecibo(crearConceptoRecibo(TipoConceptoRecibo.DEDUCTIVO));
        itemDummy3.setValorCalculado(valor1.multiply(new BigDecimal(0.04)));
        itemDummy3.setValorIngresado(valor1.multiply(new BigDecimal(0.04)));
        itemDummy3.setSueldo(sueldoDummy);
        sueldoDummy.getItemsSueldoCollection().add(itemDummy3);
        
        //Item No Remunerativo
        ItemsSueldo itemDummy4 = new ItemsSueldo();
        itemDummy4.setConceptoRecibo(crearConceptoRecibo(TipoConceptoRecibo.NO_REMUNERATIVO));
        BigDecimal valor4 = new BigDecimal(obtenerRandom());
        itemDummy4.setValorCalculado(valor4);
        itemDummy4.setValorIngresado(valor4);
        itemDummy4.setSueldo(sueldoDummy);
        sueldoDummy.getItemsSueldoCollection().add(itemDummy4);
        
        
        return sueldoDummy;
    }
        
    public static Personal crearPersonal(){
        Personal personalDummy = new Personal();
        
        personalDummy.setApellido("Apellido" + obtenerRandom());
        personalDummy.setCategoriaPrincipal(crearCategoria());
        personalDummy.setDocumento(String.valueOf(obtenerRandom()));
        personalDummy.setCuil("20-" + personalDummy.getDocumento() + "-0");
        personalDummy.setFechaNacimiento(new Date());
        personalDummy.setDomicilio("CalleNNN " + obtenerRandom());
        personalDummy.setLocalidad("Localidad" + obtenerRandom());
        personalDummy.setIngreso(new Date());
        personalDummy.setRegistro(String.valueOf(obtenerRandom()));
        personalDummy.setTipoRecibo(crearFixedList(TipoRecibo.HORAS, "Horas"));
        personalDummy.setTipoDocumento(crearFixedList(TipoDocumento.DNI, "DNI"));
        personalDummy.setEstadoCivil(crearFixedList(EstadoCivil.SOLTERO, "Soltero"));
        
        return personalDummy;
    }
    
    public static ConceptoRecibo crearConceptoRecibo(int idTipoConcepto){
        ConceptoRecibo conceptoDummy = new ConceptoRecibo();
        conceptoDummy.setCalculado(Boolean.FALSE);
        conceptoDummy.setConcepto("Concepto:" + obtenerRandom());
        conceptoDummy.setOrden(obtenerRandom());
        conceptoDummy.setTipo(crearFixedList(idTipoConcepto, "TipoConcepto" + idTipoConcepto));
        conceptoDummy.setTipoRecibo(crearFixedList(TipoRecibo.HORAS, "Horas"));
        conceptoDummy.setTipoValor(crearFixedList(TipoValorConcepto.FIJO, "Fijo"));
        
        return conceptoDummy;
    }
    
    public static Categoria crearCategoria(){
        Categoria categoriaDummy = new Categoria();
        categoriaDummy.setDescripcion("DescCategoria" + obtenerRandom());
        return categoriaDummy;
    }
    
    public static FixedList crearFixedList(int valor, String texto){
        FixedList flDummy = new FixedList();
        flDummy.setId(valor);
        flDummy.setDescripcion(texto);
        return flDummy;
    }
    
    
}
