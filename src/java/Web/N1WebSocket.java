package Web;

import java.io.IOException;
import java.util.ArrayList;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import logica.Nucleo;
import logica.Procesador;
import logica.Proceso;

//Se define el WebSocket que se va a comunicar con la pagina web
@ServerEndpoint("/websocket1")
public class N1WebSocket {

    //Se definen los datos que utilizará la clase
    private Procesador procesador;
    private Nucleo aux;
    private Proceso pAux;
    private String s;
    private ArrayList<Proceso> alAux;

    //Se define que hacer cuando se reciba un mensaje desde el cliente JavaScript de la pagina HTML
    @OnMessage
    public void onMessage(String message, Session session) throws EncodeException, IOException {
        String aux[] = message.split(";");
        if (aux[0].equals("p")) {
            String proceso[] = aux[1].split(",");
            procesador.agregarProceso(Integer.parseInt(proceso[0]), proceso[1], Integer.parseInt(proceso[2]), proceso[3], Integer.parseInt(proceso[4]));
        } else if (aux[0].equals("r")) {
            procesador.agregarRecurso(aux[1]);
        } else if (aux[0].equals("i")) {
            procesador.Iniciar();
        } else if (aux[0].equals("d")) {
            procesador.Detener();
        } else {
            procesador.setTiempo(Float.parseFloat(aux[1]));
            correr(session);
        }
    }

    //Acción a realizar cuando se abra la conexión desde el cliente
    @OnOpen
    public void onOpen(Session session) throws EncodeException {
        procesador = new Procesador();
        System.out.println("Servidor Iniciado");
    }

    //Acción a tomar cuando el cliente cierre la conexión
    @OnClose
    public void onClose(Session session) {
        System.out.println("Session " + session.getId() + " has ended");
    }

    //Se genera el String con los datos necesarios para el cliente, definiendo el núcleo que será enviado como mensaje
    public String nucleoToString(int numNodo) {
        s = numNodo + ";";
        aux = procesador.getNucleo(numNodo);
        pAux = aux.getEnEjecucion();
        if (aux.contador == 0) {
            s += pAux.toString() + ";" + aux.getListo().toString() + ";" + aux.getBloqueado().toString() + ";" + aux.getTerminado().toString() + ";" + aux.getSuspendido().toString() + ";" + procesador.retornarInfoGannt();
            System.out.println(s);
        } else {
            s += pAux.toString() + ";" + aux.colaAntes + ";" + aux.getBloqueado().toString() + ";" + aux.getTerminado().toString() + ";" + aux.getSuspendido().toString() + ";" + procesador.retornarInfoGannt();
            System.out.println(s);
            aux.contador--;
        }
        return s;
    }

    //Enviar los Strings que contienen la información de los tres núcleos
    public void correr(Session session) throws IOException {
        String aux;
        for (int i = 1; i <= 3; i++) {
            aux = nucleoToString(i);
            session.getBasicRemote().sendText(aux);
        }
    }
}
