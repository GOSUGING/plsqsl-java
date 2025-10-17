package model;
import java.util.Date;

public class LogEntry {
    private long id;
    private String subprograma;
    private String gravedad;
    private String mensaje;
    private Date fecha;

    public LogEntry(long id, String subprograma, String gravedad, String mensaje, Date fecha) {
        this.id = id; this.subprograma = subprograma; this.gravedad = gravedad; this.mensaje = mensaje; this.fecha = fecha;
    }
    // getters
    public long getId(){return id;}
    public String getSubprograma(){return subprograma;}
    public String getGravedad(){return gravedad;}
    public String getMensaje(){return mensaje;}
    public Date getFecha(){return fecha;}
}