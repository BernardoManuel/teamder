package model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "mensajes")
public class Message {
    @Id
    @Column(name = "id_mensaje")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String mensaje;
    @Column
    private long fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sala")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_user")
    private User user;

    public Message() {
    }

    public Message(Integer id, String mensaje, long fecha, Room room, User user) {
        this.id = id;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.room = room;
        this.user = user;
    }

    public Integer getId() {
        return id;
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

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
