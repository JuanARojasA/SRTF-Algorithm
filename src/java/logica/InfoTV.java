package logica;

//Clase que define la información del tiempo de vida de un proceso
public class InfoTV {
    private int pid;
    private int tv;

    public InfoTV() {
    }

    public int getPid() {
        return pid;
    }

    public int getTv() {
        return tv;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public void setTv(int tv) {
        this.tv = tv;
    }
}
