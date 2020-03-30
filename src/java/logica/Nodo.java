package logica;

//Clase nodo que será utilizada en la clase Cola
public class Nodo {

    private Proceso clave;
    private Nodo siguiente;
    private Nodo anterior;

    public Nodo() {
        clave = null;
        siguiente = null;
        anterior = null;
    }

    public Proceso getClave() {
        return clave;
    }

    public Nodo getSiguiente() {
        return siguiente;
    }

    public Nodo getAnterior() {
        return anterior;
    }

    public void setClave(Proceso dato) {
        this.clave = dato;
    }

    public void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }

    public void setAnterior(Nodo anterior) {
        this.anterior = anterior;
    }
}
