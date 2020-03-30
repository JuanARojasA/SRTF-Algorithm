package logica;

//La clase Nucleo, es donde se ejecutan los procesos, a su vez que es la que contiene las colas de procesos
public class Nucleo extends Thread {

    //Se definen las variables que utilizará la clase Nucleo
    private Cola listo, bloqueado, suspendido, terminado;
    private Proceso enEjecucion;
    private Recurso recursoUsado;
    private Thread delay;
    private float velocidadEjecucion;
    public int contador;
    public String colaAntes;        //Esta variable sirve para tener una copia de como se encontraba la cola de listos antes de ser ordenada
    public int tiempoS = 5;         //Definimos el tiempo que pasará un proceso en la cola de suspendidos como 5 rafagas

    //Constructor de la clase Nucleo, se inicializan las colas y el proceso en ejecucion, a su vez que la velocidad a la que se ejecutará el programa
    public Nucleo() {
        this.listo = new Cola();
        this.enEjecucion = new Proceso();
        this.bloqueado = new Cola();
        this.suspendido = new Cola();
        this.terminado = new Cola();
        this.delay = new Thread();
        this.velocidadEjecucion = 1;
        contador = 0;
        colaAntes = "";
    }

    public Cola getListo() {
        return listo;
    }

    public Cola getBloqueado() {
        return bloqueado;
    }

    public Cola getTerminado() {
        return terminado;
    }

    public Cola getSuspendido() {
        return suspendido;
    }

    public Proceso getEnEjecucion() {
        return enEjecucion;
    }

    public void setListo(Cola listo) {
        this.listo = listo;
    }

    public void setBloqueado(Cola bloqueado) {
        this.bloqueado = bloqueado;
    }

    public void setTerminado(Cola terminado) {
        this.terminado = terminado;
    }

    public void setEnEjecucion(Proceso enEjecucion) {
        this.enEjecucion = enEjecucion;
    }

    //Cambia la velocidad a la que se ejecutarán los procesos en zona critica
    public void setVelocidadEjecucion(float velocidadEjecucion) {
        this.velocidadEjecucion = this.velocidadEjecucion;
        enEjecucion.setVelocidadEjecucion(velocidadEjecucion);
    }

    //Detiene el núcleo
    public void Deneter() {
        enEjecucion.suspend();
    }

    //AÑade nuevos procesos a la cola de listos
    public void agregarProceso(Proceso proceso) {
        listo.put(proceso);                             //Añadimos el proceso a la cola de listos
        colaAntes = listo.toString();                   //Guardamos la información de la cola antes de ser ordenada
        contador = 1;                                   //Reiniciamos el delay en pantalla
        listo.ordenar();                                //Ordenamos la cola de listos
    }

    //Inicia la ejecución del proceso que se encuentra en zona critica
    public void iniciarEjecucion() {
        try {                                                                   
            enEjecucion.setVelocidadEjecucion(velocidadEjecucion);              //Cambiamos la velocidad de ejecucion del proceso en zona critica
            enEjecucion.start();                                                //Iniciamos el proceso en zona critica
        } catch (IllegalStateException ex) {
            enEjecucion.setVelocidadEjecucion(velocidadEjecucion);              //Cambiamos la velocidad de ejecución del proceso en zona critica
            enEjecucion.resume();                                               //En caso de que el proceso anteriormente haya sido suspendido, entonces resumimos su ejecución
        }
    }

    //Añade un proceso a la cola de bloqueados
    public void bloquearProceso(Proceso proceso) {
        bloqueado.put(proceso);
    }

    //Atiende la cola de listos
    public void atenderListo() {
        Proceso p = new Proceso();
        if (enEjecucion.getTiempoVida() == 0 && enEjecucion.getPid() != 0) {    //Se pregunta si exise un proceso en zona critica, y si este ya terminó su tiempo de vida
            try {   //Se duerme al proceso en zona critica debido a que ya terminó su tiempo de vida y se añade a la cola de terminados
                enEjecucion.wait();
                p.cambiarInfo(enEjecucion);
                terminado.put(p);
                Procesador.aumentarTerminados();
            } catch (IllegalMonitorStateException ex) { //Si al momento de dormir el proceso en zona critica se genera algún error, entonces se suspende, se añade su información a terminados y se reinicia el proceso en zona critica
                enEjecucion.suspend();
                p.cambiarInfo(enEjecucion);
                enEjecucion = new Proceso();
                terminado.put(p);
                Procesador.aumentarTerminados();
                enEjecucion = new Proceso();
            } catch (InterruptedException ex) {
            }
            Procesador.liberarRecurso(recursoUsado);    //Se libera el proceso que se estaba utilizando para que se puea usar por otro proceso
            atenderBloqueado();                         //Se busca si algún proceso en la cola de bloqueados puede salir y entrar a la cola de listos
            recursoUsado = null;
            if (contador == 0) {                        //La variable contador permite generar el delay para ver el ordenamiento de la tabla en la pagina web
                p = listo.get();                        //Obtenemos el primer proceso de la cola de listos
                if (p != null) {                        //Preguntamos si el proceso no es nulo, ya que si lo es significa que no habían procesos en la cola de listos
                    Recurso r = Procesador.tomarRecurso(p.getRecurso());                //Buscamos el recurso dentro de la lista de recursos disponibles
                    if (r.getEstado() == 0 && r.getNombre().equals(p.getRecurso())) {   //Preguntamos si el recurso solicitado se encuentra disponible
                        recursoUsado = r;
                        enEjecucion.cambiarInfo(p);                                     //Añadimos el proceso que se ejecutará a zona critica
                        try {
                            enEjecucion.notify();                                       //Despertamos al proceso en zona crítica para que pueda comenzar su ejecución
                        } catch (IllegalMonitorStateException ex) {
                            iniciarEjecucion();                                         //En caso de que el proceso en zona critica no pueda despertar debido a que se reinició, entonces volvemos a iniciar su ejecución
                        }
                    } else {
                        bloqueado.put(p);                                               //En caso de que el recurso que el proceso necesitaba, estuviese siendo usado entonces este se añade a la cola de bloqueados
                        Procesador.aumentarBloqueados();
                        //System.out.println("Se ha bloqueado el proceso; " + p.getNombre());
                    }
                }
            }
        } else if (enEjecucion.getPid() == 0) {                                         //Preguntamos si la zona critia no tiene ningun proceso en ejecución
            if (listo.vacio() == false) {                                               //Preguntamos si la cola de listos no esta vacia
                if (contador == 0) {                                                    //Preguntamos si el delay del ordenamiento no está activo
                    p = listo.get();                                                    //Tomamos el primer proceso de la cola de lsitos
                    if (p != null) {                                                    //Preguntamos si su información es valida
                        Recurso r = Procesador.tomarRecurso(p.getRecurso());            //Preguntamos por el recurso requerido
                        if (r.getEstado() == 0 && r.getNombre().equals(p.getRecurso())) {   //Miramos si el recurso se encuentra disponible
                            recursoUsado = r;
                            enEjecucion.cambiarInfo(p);                                     //Añadimos el proceso que se ejecutará a zona critica
                            try {
                                enEjecucion.notify();                                       //Despertamos al proceso en zona crítica para que pueda comenzar su ejecución
                            } catch (IllegalMonitorStateException ex) {
                                iniciarEjecucion();                                         //En caso de que el proceso en zona critica no pueda despertar debido a que se reinició, entonces volvemos a iniciar su ejecución
                            }
                        } else {
                            bloqueado.put(p);                                               //En caso de que el recurso que el proceso necesitaba, estuviese siendo usado entonces este se añade a la cola de bloqueados
                            Procesador.aumentarBloqueados();
                            //System.out.println("Se ha bloqueado el proceso; " + p.getNombre());
                        }
                    }
                }

            }
        } else if (enEjecucion.getTiempoVida() > 0 && !listo.vacio()) {                     //Miramos si en zona critica existe algún proceso que aún no ha terminado su tiempo de vida y si la cola de listos no se encuentra vacia
            if (enEjecucion.getTiempoVida() > listo.primero().getTiempoVida()) {            //Preguntamos si el tiempo de vida del proceso en zona critica es menor al del primer proceso de la cola de listos
                p = listo.get();                                                            //Si el tiempo de vida es menor, tomamos el primer proceso de la cola de listos para añadirlo a zona critica
                try {                       
                    enEjecucion.wait();                                                     //Dormimos al proceso que se encuentra en zona critica para posteriormente añadirlo a la cola de suspendidos
                    enEjecucion.setTiempoSuspendido(tiempoS);                               //Al proceso en zona critica le ponemos un tiempo de suspension igual al definido desde las variables globales de la clase
                    suspendido.put(enEjecucion);                                            //Añadimos al proceso en zona critica a la cola de suspendidos
                    Procesador.aumentarSuspendidos();
                    enEjecucion.cambiarInfo(p);                                             //Añadimos al primer proceso de la cola de listos a la zona critica
                    enEjecucion.notify();                                                   //Despertamos al proceso en zona critica para que inicie su ejecución
                } catch (IllegalMonitorStateException ex) { //En caso de que se genere un problema debemos reiniciar la zona critica para volver a aceptar procesos
                    enEjecucion.suspend();                                                  //Si ocurre algún problema durante el wait de la zona critica, debemos reiniciarla, para ello primero suspendemos al proceso en zona critica
                    enEjecucion.setTiempoSuspendido(tiempoS);                               //Añadimos el tiempo de suspension al proceso de zona critica
                    suspendido.put(enEjecucion);                                            //Añadimos dicho proceso a la cola de suspendidos
                    Procesador.aumentarSuspendidos();
                    enEjecucion = new Proceso();                                            //Reiniciamos la zona critica
                    enEjecucion.cambiarInfo(p);                                             //Añadimos al primer proceso de la cola de listos a la zona critica
                    iniciarEjecucion();                                                     //Iniciamos la ejecución del proceso en zona critica
                } catch (InterruptedException ex) {
                }
            }
        }
    }
    
    //Atendemos la cola de suspendidos en busqueda de algún proceso que haya terminado su tiempo de suspensión
    public void atenderSuspendido() {
        if (!suspendido.vacio()) {                                                          //Preguntamos si la cola de suspendidos se encuentra vacia
            Proceso p;
            int aux;
            for (int i = 0; i < suspendido.getTam(); i++) {                                 //Recorremos la cola de suspendidos
                p = suspendido.get();                                                       //Obtenemos el primer proceso de la cola de suspendidos
                aux = p.getTiempoSuspendido();                                              //Miramos cuanto es su tiempo de penalización restante
                if (aux > 0) {                                                              //Preguntamos si el tiempo de suspensión es mayor a 0
                    p.setTiempoSuspendido(aux - 1);                                         //Quitamos una rafaga al tiempo de suspensión                             
                    suspendido.put(p);                                                      //Volvemos a añadir el proceso a la cola de suspendidos
                } else {                                                    
                    listo.put(p);                                                           //Si el proceso ya terminó su tiempo de suspensión lo añadimos a la cola de listos
                    colaAntes = listo.toString();                                           //Guardamos la información de la cola antes de ordenarla
                    listo.ordenar();                                                        //Ordenamos la cola de lsitos
                    contador = 1;                                                           //Reiniciamos el delay en pantalla
                }
            }
        }
    }

    //Atendemos la cola de bloqueados en busqueda de algún proceso del cual su recurso ya puede volver a utilizarse
    public void atenderBloqueado() {
        if (!bloqueado.vacio()) {                                                           //Preguntamos si la cola de bloqueados se encuentra vacia
            Proceso p;              
            for (int i = 0; i < bloqueado.getTam(); i++) {                                  //Recorremos la cola de bloqueados
                p = bloqueado.get();                                                        //Obtenemos el primer proceso de la cola de bloqueados
                if (Procesador.consultarRecurso(p.getRecurso()) == true) {                  //Miramos si el recurso que dicho proceso necesita ya ha sido liberado
                    listo.put(p);                                                           //Si el recurso ya ha sido liberado, entonces, añadimos el proceso a la cola de listos
                    colaAntes = listo.toString();                                           //Guardamos la información de la cola antes de ser ordenada
                    listo.ordenar();                                                        //Ordenamos la cola de listos
                    contador = 1;                                                           //Reiniciamos el delay en pantalla
                    //System.out.println("Se ha desbloqueado el proceso; " + p.getPid() + "-" + p.getNombre());
                } else {
                    bloqueado.put(p);
                }
            }
        }
    }

    //Este metodo funciona para realizar un delay cada refaga
    public void generarDelay(int cant) {
        try {
            this.delay.sleep(cant);
        } catch (InterruptedException ex) {
            this.delay.interrupt();
        }
    }

    //Este metodo es aquel que se ejecut cuando se inicie el nucleo desde el proesador
    @Override
    public void run() {
        while (true) {
            atenderBloqueado();                                                 //Atendemos la cola de bloqueados
            atenderSuspendido();                                                //Atendemos la cola de suspendidos
            atenderListo();                                                     //Atendemos la cola de listos
            generarDelay((int) (1000 / velocidadEjecucion));                    //Generamos un delay teniendo en cuenta la velocidad de ejecución del procesador
        }
    }
}
