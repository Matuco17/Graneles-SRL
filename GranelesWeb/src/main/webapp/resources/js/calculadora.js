function actualizarCalculadora(idTipoJornal, idTarea){
    var cantidad = jQuery("[name*=cant_" + idTipoJornal + "_" + idTarea + "]").val().replace(",", ".");
    var bruto = jQuery("[name*=bruto_" + idTipoJornal + "_" + idTarea + "]").val();
    var total = parseFloat(cantidad) * parseFloat(bruto);
    jQuery("[name*=total_" + idTipoJornal + "_" + idTarea + "]").val(roundNumber(total, 2));
    
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
}

function sumarTipoJornal(idTipoJornal){
    var total = 0.0;
    
    jQuery("[name*=total_" + idTipoJornal + "_]").each(function(idx, element){
        total += parseFloat(jQuery(element).val());
    });
    
    jQuery("[name*=totalTJ_" + idTipoJornal + "]").val(roundNumber(total, 2));
}


function roundNumber(num, dec) {
    return num;
    //return Math.round(num*Math.pow(10,dec))/Math.pow(10,dec);
}
