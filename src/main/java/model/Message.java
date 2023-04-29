package model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "mensajes")
public class Message {
    @Id
    @Column(name = "id_mensaje")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private Integer id_sala;
    @Column(name = "code_user")
    private Integer id_user;
    @Column
    private String mensaje;
    @Column
    private long fecha;

    public Message() {
    }

    public Message(Integer id, Integer id_sala, Integer id_user, String mensaje, long fecha) {
        this.id = id;
        this.id_sala = id_sala;
        this.id_user = id_user;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public Integer getId() {
        return id;
    }

    public Integer getId_sala() {
        return id_sala;
    }

    public Integer getId_user() {
        return id_user;
    }

    public String getMensaje() {
        return mensaje;
    }

    public long getFecha() {
        return fecha;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setId_sala(Integer id_sala) {
        this.id_sala = id_sala;
    }

    public void setId_user(Integer id_user) {
        this.id_user = id_user;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }
}
