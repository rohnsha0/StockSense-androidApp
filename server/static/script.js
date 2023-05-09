// Functionality for input-linked subdomain
/*
document.getElementById("searchInp").addEventListener("submit", function(event)){
    event.preventDefault();

    var subDomain= document.getElementById("inpSymbol").value;
    var form= document.getElementById("searchInp");

    //form.action= "http://"+subDomain+".localhost:8080";
    form.action= "http://127.0.0.1:5000/"+subDomain;
    form.submit();
}*/