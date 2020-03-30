//Variables globales del JS
var webSocket;
var messages = document.getElementById("messages");
var timer;
var timer2;

//Esta función define que se hará cuando se conecte el javascript al websocket en java
function openSocket() {
    if (webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) {
        writeResponse("WebSocket is already opened.");
        return;
    }

    webSocket = new WebSocket("ws://" + document.location.host + document.location.pathname + "websocket1");       //Se define a que websocket se realizará la conexión

    webSocket.onopen = function (event) {
        if (event.data === undefined)
            return;
        writeResponse(event.data);
    };

    webSocket.onmessage = function (event) {
        writeResponse(event.data);
    };

    webSocket.onclose = function (event) {
        ;
    };
}

//Define la manera en como se solicitan los datos al servidor desde el cliente, por medio de mensajes
function pedirDatos() {
    if (document.getElementById("slider").value < 1) {
        timer = setTimeout(function () {
            webSocket.send("n;" + document.getElementById("slider").value);
            pedirDatos();
        }, 500);
    } else {
        timer = setTimeout(function () {
            webSocket.send("n;" + document.getElementById("slider").value);
            pedirDatos();
        }, 1000 / document.getElementById("slider").value);
    }
}

//Muestra la velocidad a la que se está realizando la simulación
function showValue() {
    document.getElementById("range").innerHTML = document.getElementById("slider").value + "x";
}

//Pinta el gant del nucleo solicitado
function pintarGantt(nucleo) {
    var gantt = document.getElementById("Gantt" + nucleo);
    var tabla;
    if (gantt.rows.length > 0) {
        tabla = document.getElementById("enEjecucion" + nucleo)
        if (tabla.rows.length > 1) {
            for (var i = 0; i < gantt.rows.length; i++) {
                if (tabla.rows[1].cells[0].innerHTML === gantt.rows[i].cells[0].innerHTML) {
                    var x = gantt.rows[i].insertCell(-1);
                    x.style.backgroundColor = "#70BD17";
                    break;
                }
            }
        }
        tabla = document.getElementById("listos" + nucleo);
        if (tabla.rows.length > 1) {
            for (var i = 0; i < gantt.rows.length; i++) {
                for (var j = 1; j < tabla.rows.length; j++) {
                    if (tabla.rows[j].cells[0].innerHTML === gantt.rows[i].cells[0].innerHTML) {
                        var x = gantt.rows[i].insertCell(-1);
                        x.style.backgroundColor = "#33B5E5";
                        break;
                    }
                }
            }
        }
        tabla = document.getElementById("bloqueados" + nucleo);
        if (tabla.rows.length > 1) {
            for (var i = 0; i < gantt.rows.length; i++) {
                for (var j = 1; j < tabla.rows.length; j++) {
                    if (tabla.rows[j].cells[0].innerHTML === gantt.rows[i].cells[0].innerHTML) {
                        var x = gantt.rows[i].insertCell(-1);
                        x.style.backgroundColor = "#1B1919";
                        break;
                    }
                }
            }
        }
        tabla = document.getElementById("suspendidos" + nucleo);
        if (tabla.rows.length > 1) {
            for (var i = 0; i < gantt.rows.length; i++) {
                for (var j = 1; j < tabla.rows.length; j++) {
                    if (tabla.rows[j].cells[0].innerHTML === gantt.rows[i].cells[0].innerHTML) {
                        var x = gantt.rows[i].insertCell(-1);
                        x.style.backgroundColor = "#D62A2A";
                        break;
                    }
                }
            }
        }
    }
}

//Llena la metrica del nucleo solicitado
function llenarMetrica(nucleo) {
    var metrica = document.getElementById("metrica" + nucleo);
    var tabla;
    if (metrica.rows.length > 1) {
        tabla = document.getElementById("enEjecucion" + nucleo);
        if (tabla.rows.length > 1) {
            for (var i = 0; i < metrica.rows.length; i++) {
                if (tabla.rows[1].cells[1].innerHTML === metrica.rows[i].cells[0].innerHTML) {
                    var x = parseInt(metrica.rows[i].cells[2].innerHTML);
                    metrica.rows[i].cells[2].innerHTML = x + 1;
                    break;
                }
            }
        }
        tabla = document.getElementById("listos" + nucleo);
        if (tabla.rows.length > 1) {
            for (var i = 0; i < metrica.rows.length; i++) {
                for (var j = 1; j < tabla.rows.length; j++) {
                    if (tabla.rows[j].cells[1].innerHTML === metrica.rows[i].cells[0].innerHTML) {
                        var x = parseInt(metrica.rows[i].cells[2].innerHTML);
                        metrica.rows[i].cells[2].innerHTML = x + 1;
                        break;
                    }
                }
            }
        }
        tabla = document.getElementById("bloqueados" + nucleo);
        if (tabla.rows.length > 1) {
            for (var i = 0; i < metrica.rows.length; i++) {
                for (var j = 1; j < tabla.rows.length; j++) {
                    if (tabla.rows[j].cells[1].innerHTML === metrica.rows[i].cells[0].innerHTML) {
                        var x = parseInt(metrica.rows[i].cells[2].innerHTML);
                        metrica.rows[i].cells[2].innerHTML = x + 1;
                        break;
                    }
                }
            }
        }
        tabla = document.getElementById("suspendidos" + nucleo);
        if (tabla.rows.length > 1) {
            for (var i = 0; i < metrica.rows.length; i++) {
                for (var j = 1; j < tabla.rows.length; j++) {
                    if (tabla.rows[j].cells[1].innerHTML === metrica.rows[i].cells[0].innerHTML) {
                        var x = parseInt(metrica.rows[i].cells[2].innerHTML);
                        metrica.rows[i].cells[2].innerHTML = x + 1;
                        break;
                    }
                }
            }
        }
        tabla = document.getElementById("terminados" + nucleo);
        var eficiencia = 0;
        var total = 0;
        var aux = 0;
        if (tabla.rows.length > 1) {
            for (var i = 0; i < metrica.rows.length; i++) {
                for (var j = 1; j < tabla.rows.length; j++) {
                    if (tabla.rows[j].cells[1].innerHTML === metrica.rows[i].cells[0].innerHTML) {
                        var ttl = parseInt(metrica.rows[i].cells[1].innerHTML);
                        var tr = parseInt(metrica.rows[i].cells[2].innerHTML);
                        aux = tr - ttl;
                        if (aux < 0) {
                            metrica.rows[i].cells[3].innerHTML = 0;
                            metrica.rows[i].cells[4].innerHTML = 1;
                            metrica.rows[i].cells[5].innerHTML = "100%";
                            eficiencia += 100;
                            total += 1;
                        } else {
                            metrica.rows[i].cells[3].innerHTML = aux;
                            aux = tr / ttl;
                            aux = aux.toFixed(2);
                            metrica.rows[i].cells[4].innerHTML = aux;
                            aux = 1 / aux;
                            aux *= 100;
                            eficiencia += aux;
                            aux = aux.toFixed(1);
                            total += 1;
                            metrica.rows[i].cells[5].innerHTML = aux + "%";
                        }
                        break;
                    }
                }
            }
            eficiencia /= total;
            eficiencia = eficiencia.toFixed(1);
            document.getElementById("eficiencia" + nucleo).innerHTML = eficiencia;
            if (eficiencia > 80) {
                document.getElementById("rendimiento" + nucleo).innerHTML = "El rendimiento en el núcleo "
                        + nucleo
                        + " fue del "
                        + eficiencia
                        + "%, por lo tanto, se puede concluir que el algoritmo es Eficiente";
            } else if (eficiencia > 50 && eficiencia < 80) {
                document.getElementById("rendimiento" + nucleo).innerHTML = "El rendimiento en el núcleo "
                        + nucleo
                        + " fue del "
                        + eficiencia
                        + "%, por lo tanto, se puede concluir que el algoritmo es Aceptable";
            } else {
                document.getElementById("rendimiento" + nucleo).innerHTML = "El rendimiento en el núcleo "
                        + nucleo
                        + " fue del "
                        + eficiencia
                        + "% , por lo tanto, se puede concluir que el algoritmo es Deficiente";
            }
        }
        eficiencia = 0;
        aux = 0;
        total = 0;
        aux = parseFloat(document.getElementById("eficiencia1").innerHTML);
        if (aux > 0) {
            total += 1;
            eficiencia += aux;
        }
        aux = parseFloat(document.getElementById("eficiencia2").innerHTML);
        if (aux > 0) {
            total += 1;
            eficiencia += aux;
        }
        aux = parseFloat(document.getElementById("eficiencia3").innerHTML);
        if (aux > 0) {
            total += 1;
            eficiencia += aux;
        }
        if (total !== 0) {
            eficiencia /= total;
            eficiencia = eficiencia.toFixed(1);
        }
        if (eficiencia > 80) {
            document.getElementById("rendimientoGeneral").innerHTML = "El rendimiento General fue del "
                    + eficiencia
                    + "%, por lo tanto, se puede concluir que el algoritmo es Eficiente";
        } else if (eficiencia > 50 && eficiencia < 80) {
            document.getElementById("rendimientoGeneral").innerHTML = "El rendimiento General fue del "
                    + eficiencia
                    + "%, por lo tanto, se puede concluir que el algoritmo es Aceptable";
        } else {
            document.getElementById("rendimientoGeneral").innerHTML = "El rendimiento General fue del "
                    + eficiencia
                    + "%, por lo tanto, se puede concluir que el algoritmo es Deficiente";
        }
    }
}

//Este metodo se ejcuta cada rafaga para así poder llenar la metrica y pintar el gannt
function MetricaGantt() {
    timer = setTimeout(function () {
        llenarMetrica(1);
        llenarMetrica(2);
        llenarMetrica(3);
        pintarGantt(1);
        pintarGantt(2);
        pintarGantt(3);
        var contador = parseInt(document.getElementById("contador").innerHTML);
        document.getElementById("contador").innerHTML = contador + 1;
        MetricaGantt();
    }, 1000 / document.getElementById("slider").value);
}

//Esta funcion se encarga de hacer el envio de los nuevos recursos que se añadiran a la simulación
function sendR() {
    var recurso = "r;";
    if (document.getElementById("nombreRecurso") !== "") {
        recurso += document.getElementById("nombreRecurso").value;
        var select = document.getElementById("combRecursos");
        var option = document.createElement("option");
        option.text = document.getElementById("nombreRecurso").value;
        select.add(option);
        webSocket.send(recurso);
    }
    document.getElementById("nombreRecurso").value = "";
}

//Esta funcion se encarga de enviar la información de los nuevos procesos de la simulación
function sendP() {
    var proceso = "p;";
    if (document.getElementById("combRecursos").value !== "") {
        if (document.getElementById("nombreProceso").value !== "") {
            if (document.getElementById("tiempoVida").value !== "") {
                var id = parseInt(document.getElementById("idProcesoActual").innerHTML);
                document.getElementById("idProcesoActual").innerHTML = id + 1;
                proceso += id + ",";
                proceso += document.getElementById("nombreProceso").value + ",";
                proceso += document.getElementById("tiempoVida").value + ",";
                proceso += document.getElementById("combRecursos").value + ",";
                proceso += document.getElementById("escogerProcesador").value + ",";
                webSocket.send(proceso);
            }
        }
    }
    //Añadir a Gantt
    row = document.getElementById("Gantt" + document.getElementById("escogerProcesador").value).insertRow(-1);
    ganttId = row.insertCell(0);
    ganttId.innerHTML = id;
    nombre = row.insertCell(1);
    nombre.innerHTML = document.getElementById("nombreProceso").value;
    var contador = parseInt(document.getElementById("contador").innerHTML);
    for (var i = 0; i < contador; i++) {
        row.insertCell(-1);
    }
    //Añadir a Metrica
    row = document.getElementById("metrica" + document.getElementById("escogerProcesador").value).insertRow(-1);
    nombre = row.insertCell(0);
    nombre.innerHTML = document.getElementById("nombreProceso").value;
    ttl = row.insertCell(1);
    ttl.innerHTML = document.getElementById("tiempoVida").value;
    var tr = row.insertCell(2);
    tr.innerHTML = 0;
    var te = row.insertCell(3);
    te.innerHTML = "-";
    var p = row.insertCell(4);
    p.innerHTML = "-";
    var propR = row.insertCell(5);
    propR.innerHTML = "-";
    //Limpiar Cajas de Texto
    document.getElementById("nombreProceso").value = "";
    document.getElementById("tiempoVida").value = "";
}

//Comienza el inicio de la simulación
function Iniciar() {
    webSocket.send("i");
}

//Termina la simulación y cierra la conexión con el servidor
function closeSocket() {
    terminarPedido();
    webSocket.send("d");
    webSocket.close();
}

//Actualiza las tablas HTML de la pagina
function actualizarHTML(nucleo, s) {
    var tabla = document.getElementById("enEjecucion" + nucleo);
    if (s[1] !== ".") {
        var proceso = s[1].split(",");
        for (var i = 0; i < 4; i++) {
            tabla.rows[1].cells[i].innerHTML = proceso[i];
        }
    } else {
        for (var i = 0; i < 4; i++) {
            tabla.rows[1].cells[i].innerHTML = "";
        }
    }
    tabla = document.getElementById("listos" + nucleo);
    if (s[2] !== ".") {
        var procesos = s[2].split("-");
        for (var i = 0; i < procesos.length; i++) {
            var proceso = procesos[i].split(",");
            if ((i + 1) === tabla.rows.length) {
                var row = tabla.insertRow(i + 1);
                var id = row.insertCell(0);
                var nombre = row.insertCell(1);
                var ttl = row.insertCell(2);
                id.innerHTML = proceso[0];
                nombre.innerHTML = proceso[1];
                ttl.innerHTML = proceso[2];
            } else {
                for (var j = 0; j < 3; j++) {
                    tabla.rows[i + 1].cells[j].innerHTML = proceso[j];
                }
            }
        }
        for (var i = procesos.length + 1; i < tabla.rows.length; i++) {
            tabla.deleteRow(i);
        }
    } else {
        for (var i = 1; i < tabla.rows.length; i++) {
            tabla.deleteRow(i);
        }
    }
    tabla = document.getElementById("bloqueados" + nucleo);
    if (s[3] !== ".") {
        var procesos = s[3].split("-");
        for (var i = 0; i < procesos.length; i++) {
            var proceso = procesos[i].split(",");
            if ((i + 1) === tabla.rows.length) {
                var row = tabla.insertRow(i + 1);
                var id = row.insertCell(0);
                var nombre = row.insertCell(1);
                var ttl = row.insertCell(2);
                id.innerHTML = proceso[0];
                nombre.innerHTML = proceso[1];
                ttl.innerHTML = proceso[2];
            } else {
                for (var j = 0; j < 3; j++) {
                    tabla.rows[i + 1].cells[j].innerHTML = proceso[j];
                }
            }
        }
        for (var i = procesos.length + 1; i < tabla.rows.length; i++) {
            tabla.deleteRow(i);
        }
    } else {
        for (var i = 1; i < tabla.rows.length; i++) {
            tabla.deleteRow(i);
        }
    }
    tabla = document.getElementById("terminados" + nucleo);
    if (s[4] !== ".") {
        var procesos = s[4].split("-");
        for (var i = 0; i < procesos.length; i++) {
            var proceso = procesos[i].split(",");
            if ((i + 1) === tabla.rows.length) {
                var row = tabla.insertRow(i + 1);
                var id = row.insertCell(0);
                var nombre = row.insertCell(1);
                var ttl = row.insertCell(2);
                id.innerHTML = proceso[0];
                nombre.innerHTML = proceso[1];
                ttl.innerHTML = proceso[2];
            } else {
                for (var j = 0; j < 3; j++) {
                    tabla.rows[i + 1].cells[j].innerHTML = proceso[j];
                }
            }
        }
        for (var i = procesos.length + 1; i < tabla.rows.length; i++) {
            tabla.deleteRow(i);
        }
    } else {
        for (var i = 1; i < tabla.rows.length; i++) {
            tabla.deleteRow(i);
        }
    }
    tabla = document.getElementById("suspendidos" + nucleo);
    if (s[5] !== ".") {
        var procesos = s[5].split("-");
        for (var i = 0; i < procesos.length; i++) {
            var proceso = procesos[i].split(",");
            if ((i + 1) === tabla.rows.length) {
                var row = tabla.insertRow(i + 1);
                var id = row.insertCell(0);
                var nombre = row.insertCell(1);
                var ttl = row.insertCell(2);
                id.innerHTML = proceso[0];
                nombre.innerHTML = proceso[1];
                ttl.innerHTML = proceso[2];
            } else {
                for (var j = 0; j < 3; j++) {
                    tabla.rows[i + 1].cells[j].innerHTML = proceso[j];
                }
            }
        }
        for (var i = procesos.length + 1; i < tabla.rows.length; i++) {
            tabla.deleteRow(i);
        }
    } else {
        for (var i = 1; i < tabla.rows.length; i++) {
            tabla.deleteRow(i);
        }
    }
    var datos = s[6].split(",");
    document.getElementById("atendidos").innerHTML = datos[0];
    document.getElementById("suspendidos").innerHTML = datos[1];
    p = document.getElementById("bloqueados").innerHTML = datos[2];
    p = document.getElementById("terminados").innerHTML = datos[3];
}

function writeResponse(text) {
    var s = text.split(";");
    actualizarHTML(s[0], s);
}