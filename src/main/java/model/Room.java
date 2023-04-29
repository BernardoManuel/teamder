package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "salas")
public class Room {
    @Id
    @Column(name = "id_salas")
    private Integer id;
    @Column
    private Integer id_juego;
    @Column(name = "nombre")
    private String nombre;
    @Column
    private Integer max_jugadores;
    @Column(name = "creador")
    private Integer id_creador;

    public Room() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId_juego() {
        return id_juego;
    }

    public void setId_juego(Integer id_juego) {
        this.id_juego = id_juego;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getMax_jugadores() {
        return max_jugadores;
    }

    public void setMax_jugadores(Integer max_jugadores) {
        this.max_jugadores = max_jugadores;
    }

    public Integer getId_creador() {
        return id_creador;
    }

    public void setId_creador(Integer id_creador) {
        this.id_creador = id_creador;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id) && Objects.equals(id_juego, room.id_juego) && Objects.equals(nombre, room.nombre) && Objects.equals(max_jugadores, room.max_jugadores) && Objects.equals(id_creador, room.id_creador);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, id_juego, nombre, max_jugadores, id_creador);
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", id_juego='" + id_juego + '\'' +
                ", nombre='" + nombre + '\'' +
                ", max_jugadores=" + max_jugadores +
                ", id_creador=" + id_creador +
                '}';
    }
}
