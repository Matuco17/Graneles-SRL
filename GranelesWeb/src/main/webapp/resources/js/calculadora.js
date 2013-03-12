function actualizarCalculadora(idTipoJornal, idTarea){
    var cantidad = jQuery("[name*=cant_tj" + idTipoJornal + "_tr" + idTarea + "]").val().replace(",", ".");
    var bruto = jQuery("[name*=bruto_tj" + idTipoJornal + "_tr" + idTarea + "]").val();
    var total = parseFloat(cantidad) * parseFloat(bruto);
    jQuery("[name*=total_tj" + idTipoJornal + "_tr" + idTarea + "]").val(roundNumber(total, 2));
    
    sumarTipoJornal(idTipoJornal);
    sumarTotales();
    return total;
}

function sumarTotales(){
    var total = 0.0;
    
    jQuery("[name*=totalTJ_]").each(function(idx, element){
        total += parseFloat(jQuery(element).val());
    });
    
    jQuery("[name*=totalGeneral]").val(roundNumber(total, 2));
    
    var porcAdministracion = parseFloat(jQuery("[name*=porcentajeAdministracion]").val()); 
    var totalLeyesSociales = total * porcAdministracion / 100;
    
    jQuery("[name*=totalLeyesSociales]").val(roundNumber(totalLeyesSociales, 2));
    
    jQuery("[name*=totalFinal]").val(roundNumber(total + totalLeyesSociales, 2));
    
}

function sumarTipoJornal(idTipoJornal){
    var total = 0.0;
    
    jQuery("[name*=total_tj" + idTipoJornal + "_tr]").each(function(idx, element){
        total += parseFloat(jQuery(element).val());
    });
    
    jQuery("[name*=totalTJ_" + idTipoJornal + "]").val(roundNumber(total, 2));
}

function sumarTarea(idTarea){
    var total = 0.0;
    
    jQuery("[name*=_tr" + idTarea + "]").each(function(idx, element){
        total += parseFloat(jQuery(element).val());
    });
    
    jQuery("[name*=totalTarea_" + idTarea + "]").val(roundNumber(total, 2));
}


function roundNumber(num, dec) {
    return num;
    //return Math.round(num*Math.pow(10,dec))/Math.pow(10,dec);
}
