package logica;

public class Proceso extends Thread{
    private int pid;
    private String nombre;
    public int tiempoVida;
    private String recurso;
    private float velocidadEjecucion;
    private int tiempoSuspendido;

    Proceso(){
        this.pid = 0;
        this.nombre = "";
        this.tiempoVida = 0;
        this.recurso = "";
        this.velocidadEjecucion = 1;
        this.tiempoSuspendido = 0;
    }
    
    public Proceso(int pid, String nombre, int tiempoVida, String recurso){
        this.pid = pid;
        this.nombre = nombre;
        this.tiempoVida = tiempoVida;
        this.recurso = recurso;
    }
    
    public int getPid(){
        return pid;
    }

    public String getNombre(){
        return nombre;
    }

    public int getTiempoVida(){
        return tiempoVida;
    }

    public String getRecurso(){
        return recurso;
    }

    public int getTiempoSuspendido() {
        return tiempoSuspendido;
    }

    public void setPid(int pid){
        this.pid = pid;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public void setTiempoVida(int tiempoVida){
        this.tiempoVida = tiempoVida;
    }

    public void setRecurso(String recurso){
        this.recurso = recurso;
    }

    public void setVelocidadEjecucion(float velocidadEjecucion) {
        this.velocidadEjecucion = velocidadEjecucion;
    }

    public void setTiempoSuspendido(int tiempoSuspendido) {
        this.tiempoSuspendido = tiempoSuspendido;
    }
    
    public void cambiarInfo(Proceso p){
        this.pid = p.pid;
        this.nombre = p.nombre;
        this.tiempoVida = p.tiempoVida;
        this.recurso = p.recurso;
    }
    
    //Este metodo genera un delay para poder disminuir el tiempo de vida de un proceso
    public void contador(){
        try{
            Thread.currentThread().sleep((long) (1000/this.velocidadEjecucion));
        }catch(InterruptedException ex){
            Thread.currentThread().interrupt();
        }
    }
    
    //Este metodo define que hará un hilo durante su tiempo de vida
    public void IniciarHilo(){
        System.out.println("Comienza el proceso: " + this.pid + "-" + this.nombre + " usando el recurso: " + this.recurso); 
        while(tiempoVida > 0){
            this.contador();
            //System.out.println(this.nombre + ", " + this.quantum + ", " + this.tiempoVida);
            this.setTiempoVida(this.tiempoVida-1);
            if(this.getTiempoVida() == 0){
                //System.out.println("El proceso: " + this.nombre + " ha terminado");
            }
        }
    }
    
    //Este metodo define lo que hará un proceso desde que inició su ejecución
    @Override
    public void run(){
        this.IniciarHilo();
    }
    
    //Convierte un proceso a String para así facilitar el envio de mensajes desde el servidor al cliente
    public String toString(){
        String s = "";
        if(pid == 0){
            s += ".";
        }else{
            s += pid + "," + nombre + "," + tiempoVida + "," + recurso;
        }
        return s;
    }
}
