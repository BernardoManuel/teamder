package model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "mensajes")
public class Message {
    @Id
    @Column(name = "id_mensaje")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) && Objects.equals(id_sala, message.id_sala) && Objects.equals(id_user, message.id_user) && Objects.equals(mensaje, message.mensaje) && Objects.equals(fecha, message.fecha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, id_sala, id_user, mensaje, fecha);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", id_sala=" + id_sala +
                ", id_user=" + id_user +
                ", mensaje='" + mensaje + '\'' +
                ", fecha=" + fecha +
                '}';
    }
}
