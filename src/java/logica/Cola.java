package logica;

import java.util.ArrayList;

public class Cola {

    Nodo z;
    Nodo cabeza;
    private int tam;

    //Constructor de la clase cola, el siguiente de cabeza es si mismo, y el siguiente de z es cabeza
    public Cola() {
        z = new Nodo();
        cabeza = new Nodo();
        z.setSiguiente(cabeza);
        cabeza.setAnterior(z);
        cabeza.setSiguiente(cabeza);
        tam = 0;
    }

    //Metodo para añadir procesos a la cola
    public void put(Proceso dato) {
        Nodo t = new Nodo();
        t.setClave(dato);
        t.setSiguiente(z.getSiguiente());
        z.setSiguiente(t);
        t.getSiguiente().setAnterior(t);
        t.setAnterior(z);
        tam++;
    }

    //Retornar y elimina el primer proceso de la cola
    public Proceso get() {
        if(tam > 0){
            Proceso x;
            Nodo t = cabeza.getAnterior();
            t.getAnterior().setSiguiente(cabeza);
            cabeza.setAnterior(t.getAnterior());
            x = t.getClave();
            tam--;
            return x;
        }else{
            return null;
        }
    }
    
    //Unicamente retorna el primer elemento de la cola, sin eliminarlo
    public Proceso primero(){
        return cabeza.getAnterior().getClave();
    }
    
    //Retorna un booleano que dice si la cola se encuentra vacia
    public boolean vacio(){
        return z.getSiguiente() == cabeza;
    }
    
    //retorna la cantidad de procesos que están actualmente almacenados en la cola
    public int getTam(){
        return tam;
    }
    
    //Ordena la cola, utilizando el metodo de ordenamiento por inserción
    public void ordenar(){
        Proceso temp = new Proceso();
        Nodo aux = z.getSiguiente();
        Nodo aux1;
        while(aux != cabeza){
            aux1 = aux;
            while(aux1 != z.getSiguiente()){
                if(aux1.getClave().getTiempoVida() > aux1.getAnterior().getClave().getTiempoVida()){
                    temp = aux1.getClave();
                    aux1.setClave(aux1.getAnterior().getClave());
                    aux1.getAnterior().setClave(temp);
                }
                aux1 = aux1.getAnterior();
            }
            aux = aux.getSiguiente();
        }
    }
    
    //Retorna la información de los procesos en cola como un String
    public String toString(){
        String s = "";
        if(tam == 0){
            s += ".";
        }else{
            Proceso aux;
            Nodo t = cabeza.getAnterior();
            while (t != z) {
                aux = t.getClave();
                s += aux.getPid() + "," + aux.getNombre() + "," + aux.getTiempoVida() + "," + aux.getRecurso() + "-";
                t = t.getAnterior();
            }
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}
