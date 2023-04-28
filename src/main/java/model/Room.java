package model;

import jakarta.persistence.*;
import java.util.*;

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
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sala_usuario",
            joinColumns = @JoinColumn(name = "id_salas"),
            inverseJoinColumns = @JoinColumn(name = "cod_user"))
    private Set<User> users = new HashSet<>();

    public Room() {}

    public Room(Integer id, Integer id_juego, String nombre, Integer max_jugadores, Integer id_creador, HashSet<User> users) {
        this.id = id;
        this.id_juego = id_juego;
        this.nombre = nombre;
        this.max_jugadores = max_jugadores;
        this.id_creador = id_creador;
        this.users = users;
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

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
