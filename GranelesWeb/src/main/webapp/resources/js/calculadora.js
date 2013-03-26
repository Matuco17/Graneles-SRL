function actualizarCalculadora(idTurnoEmbarque, idTarea){
    var cantidad = jQuery("[name*=cant_te" + idTurnoEmbarque + "_tr" + idTarea + "]").val().replace(",", ".");
    var bruto = jQuery("[name*=bruto_te" + idTurnoEmbarque + "_tr" + idTarea + "]").val();
    var total = parseNumber(cantidad) * parseNumber(bruto);
    jQuery("[name*=total_te" + idTurnoEmbarque + "_tr" + idTarea + "]").val(roundNumber(total));
    
    sumarTurnoEmbarque(idTurnoEmbarque);
    sumarTarea(idTarea);
    sumarTotales();
    return total;
}

function sumarTotales(){
    var total = 0.00;
    
    jQuery("[name*=totalTE_]").each(function(idx, element){
        total += parseNumber(jQuery(element).val());
    });
    
    jQuery("[name*=totalGeneral]").val(roundNumber(total));
    
    var porcAdministracion = parseNumber(jQuery("[name*=porcentajeAdministracion]").val()); 
    var totalLeyesSociales = total * porcAdministracion / 100.0;
    
    jQuery("[name*=totalLeyesSociales]").val(roundNumber(totalLeyesSociales));
    
    jQuery("[name*=totalFinal]").val(roundNumber(total + totalLeyesSociales));
    
}

function sumarTurnoEmbarque(idTurnoEmbarque){
    var total = 0.00;
    
    jQuery("[name*=total_te" + idTurnoEmbarque + "_tr]").each(function(idx, element){
        total += parseNumber(jQuery(element).val());
    });
    
    jQuery("[name*=totalTE_" + idTurnoEmbarque + "]").val(roundNumber(total));
}

function sumarTarea(idTarea){
    var total = 0.00;
    
    jQuery("[name*=_tr" + idTarea + "_total]").each(function(idx, element){
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