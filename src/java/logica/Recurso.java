package logica;

public class Recurso {
    private int rid;
    private String nombre;
    private int estado;
    
    public Recurso(){
       this.rid = 0;
       this.nombre = "";
       this.estado = 0;
    }
    
    public Recurso(int rid, String nombre){
        this.rid = rid;
        this.nombre = nombre;
        this.estado = 0;
    }

    public int getRid() {
        return rid;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEstado() {
        return estado;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
