function showPassword(){
    var tipo = document.getElementById("password");
    if(tipo.type == ("password")){
        tipo.type = "text";
    }else{
        tipo.type = "password";
    }
}

function showNewPassword(){
    var tipo = document.getElementById("password1");
    if(tipo.type == ("password")){
        tipo.type = "text";
    }else{
        tipo.type = "password";
    }
}