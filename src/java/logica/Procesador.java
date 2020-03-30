package logica;

import java.util.ArrayList;

//Clase procesador que posee los nucleos en donde se ejecutan los procesos, ademas de poseer la lista de recursos que pueden ser usados en ejecución
public class Procesador {

    //Variables globales de la clase Procesador
    public static ArrayList<Recurso> recursosDisponibles;                       //Lista de recursos a la cual se puede acceder de los nucleos para verificar su disponibilidad
    private Nucleo n1, n2, n3;                                                  //Los 3 nucleos del procesador
    private boolean bloqueado;                                                  //Booleano que define si el procesador se encuentra suspendido o bloqueado
    public static int atendidos, suspendidos, bloqueados, terminados;                  //Variables para el Gannt

    //Contructor de la clase procesador, iniciamos los nucleos del procesador y creamos la lista de recursos disponibles
    public Procesador() {
        recursosDisponibles = new ArrayList<>();
        n1 = new Nucleo();
        n2 = new Nucleo();
        n3 = new Nucleo();
        atendidos = 0;
        suspendidos = 0;
        bloqueados = 0;
        terminados = 0;
    }

    //Iniciamos cada nucleo para comenzar a ejecutar procesos
    public void Iniciar() {
        n1.start();
        n2.start();
        n3.start();
    }

    //Metodo para detener o suspender los nucleos del procesador
    public void Detener() {
        n1.Deneter();
        n2.Deneter();
        n3.Deneter();
        n1.suspend();
        n2.suspend();
        n3.suspend();
        bloqueado = true;
    }

    //Metodo para reanudar la ejecución de los nucleos del procesador
    public void Reanudar() {
        n1.start();
        n2.start();
        n3.start();
        n1.iniciarEjecucion();
        n2.iniciarEjecucion();
        n3.iniciarEjecucion();
        bloqueado = false;
    }

    //Metodo para añadir un recurso a la lista de los recursos disponibles, cualquier recurso recien añadido se encuentra disponible desde que fue agregado
    public void agregarRecurso(String nombre) {
        Recurso recurso = new Recurso(recursosDisponibles.size() + 1, nombre);
        recursosDisponibles.add(recurso);
    }

    //Metodo para agregar un proceso a un nucleo
    public void agregarProceso(int id, String nombre, int tiempoVida, String recurso, int numProcesador) {
        Proceso proceso = new Proceso(id, nombre, tiempoVida, recurso);     //Creamos un proceso con la información suministrada
        atendidos += 1;
        if (numProcesador == 1) {                                           //preguntamos a que procesador se agregará el nuevo proceso
            n1.agregarProceso(proceso);
        } else if (numProcesador == 2) {
            n2.agregarProceso(proceso);
        } else {
            n3.agregarProceso(proceso);
        }
    }

    //Este metodo estatico permite a cualquier clase externa acceder a la lista de recursos disponibles, de esta manera se permite buscar si un recurso se encuentra disponible al momento de la consulta
    public static synchronized boolean consultarRecurso(String nombre) {
        Recurso r;
        for (int i = 0; i < recursosDisponibles.size(); i++) {                  //Recorremos la lista de recursos en busca del recurso solicitado
            r = recursosDisponibles.get(i);
            if (r.getNombre().equals(nombre)) {
                if (r.getEstado() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    //Este metodo estatico permite a cualquier clase externa tomar un recurso que se encuentre dentro de la lista de recursos disponibles
    public static synchronized Recurso tomarRecurso(String nombre) {
        Recurso r;
        for (int i = 0; i < recursosDisponibles.size(); i++) {
            r = recursosDisponibles.get(i);
            if (r.getNombre().equals(nombre)) {
                if (r.getEstado() == 0) {
                    r.setEstado(1);
                    Recurso aux = new Recurso(r.getRid(), r.getNombre());
                    //System.out.println("Se ha tomado el recurso: " + aux.getNombre() + ", Estado: " + aux.getEstado());
                    recursosDisponibles.set(i, r);
                    return aux;
                }
            }
        }
        return new Recurso();
    }

    //Este metodo permite liberar un recurso cuando este ya no sea necesita por el proceso, ya sea porque fue suspendido o porque termino su tiempo de vida
    public static synchronized void liberarRecurso(Recurso r) {
        r.setEstado(0);
        //System.out.println("Se ha liberado el recurso: " + r.getNombre());
        recursosDisponibles.set(r.getRid() - 1, r);
    }

    public static synchronized void aumentarAtendidos() {
        Procesador.atendidos += 1;
    }

    public static synchronized void aumentarSuspendidos() {
        Procesador.suspendidos += 1;
    }

    public static synchronized void aumentarBloqueados() {
        Procesador.bloqueados += 1;
    }

    public static synchronized void aumentarTerminados() {
        Procesador.terminados += 1;
    }

    //Preguntamos si un nucleo se encuentra ejecutandose 
    public boolean nucleoVivo(int i) {
        if (i == 1) {
            return n1.isAlive();
        } else if (i == 2) {
            return n2.isAlive();
        } else {
            return n3.isAlive();
        }
    }

    //Retorna la información del nucleo solicitado
    public Nucleo getNucleo(int i) {
        if (i == 1) {
            return n1;
        } else if (i == 2) {
            return n2;
        } else {
            return n3;
        }
    }

    public boolean n1Vivo() {
        return n1.isAlive();
    }

    public boolean n2Vivo() {
        return n2.isAlive();
    }

    public boolean n3Vivo() {
        return n3.isAlive();
    }

    public Nucleo getN1() {
        return n1;
    }

    public Nucleo getN2() {
        return n2;
    }

    public Nucleo getN3() {
        return n3;
    }

    public boolean getBloqueado() {
        return bloqueado;
    }

    public void imprimirRecursos() {
        for (int i = 0; i < recursosDisponibles.size(); i++) {
            System.out.println(recursosDisponibles.get(i).getNombre());
        }
    }

    //Este metodo permite cambiar la velocidad de ejecución de los nucleos
    public void setTiempo(float velocidadEjecucion) {
        n1.setVelocidadEjecucion(velocidadEjecucion);
        n2.setVelocidadEjecucion(velocidadEjecucion);
        n3.setVelocidadEjecucion(velocidadEjecucion);
    }
    
    public String retornarInfoGannt(){
        String aux = Procesador.atendidos + "," + Procesador.suspendidos + "," + Procesador.bloqueados + "," + Procesador.terminados;
        return aux;
    }
}
