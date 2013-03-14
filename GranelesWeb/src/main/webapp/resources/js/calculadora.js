function actualizarCalculadora(idTipoJornal, idTarea){
    var cantidad = jQuery("[name*=cant_tj" + idTipoJornal + "_tr" + idTarea + "]").val().replace(",", ".");
    var bruto = jQuery("[name*=bruto_tj" + idTipoJornal + "_tr" + idTarea + "]").val();
    var total = parseNumber(cantidad) * parseNumber(bruto);
    jQuery("[name*=total_tj" + idTipoJornal + "_tr" + idTarea + "]").val(roundNumber(total));
    
    sumarTipoJornal(idTipoJornal);
    sumarTotales();
    return total;
}

function sumarTotales(){
    var total = 0.00;
    
    jQuery("[name*=totalTJ_]").each(function(idx, element){
        total += parseNumber(jQuery(element).val());
    });
    
    jQuery("[name*=totalGeneral]").val(roundNumber(total));
    
    var porcAdministracion = parseNumber(jQuery("[name*=porcentajeAdministracion]").val()); 
    var totalLeyesSociales = total * porcAdministracion / 100.0;
    
    jQuery("[name*=totalLeyesSociales]").val(roundNumber(totalLeyesSociales));
    
    jQuery("[name*=totalFinal]").val(roundNumber(total + totalLeyesSociales));
    
}

function sumarTipoJornal(idTipoJornal){
    var total = 0.00;
    
    jQuery("[name*=total_tj" + idTipoJornal + "_tr]").each(function(idx, element){
        total += parseNumber(jQuery(element).val());
    });
    
    jQuery("[name*=totalTJ_" + idTipoJornal + "]").val(roundNumber(total));
}

function sumarTarea(idTarea){
    var total = 0.00;
    
    jQuery("[name*=_tr" + idTarea + "]").each(function(idx, element){
        total += parseNumber(jQuery(element).val());
    });
    
    jQuery("[name*=totalTarea_" + idTarea + "]").val(roundNumber(total));
}


function roundNumber(num) {
    return num.toFixed(2);
}

function parseNumber(text){
    return parseFloat(text.replace(",", "."));
}