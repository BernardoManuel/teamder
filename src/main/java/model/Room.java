package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Objects;

@Entity
@Table(name = "salas")
public class Room {
    @Id
    @Column(name = "id_salas")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "nombre")
    private String nombre;
    @Column
    private Integer max_jugadores;
    @Column(name = "creador")
    private Integer id_creador;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "sala_usuario",
            joinColumns = @JoinColumn(name = "id_salas"),
            inverseJoinColumns = @JoinColumn(name = "cod_user"))
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fecha ASC")
    @Fetch(FetchMode.JOIN)
    private Set<Message> messages = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_juego")
    private Game game;

    public Room() {}

    public Room(Integer id, String nombre, Integer max_jugadores, Integer id_creador, Set<User> users, Set<Message> messages, Game game) {
        this.id = id;
        this.nombre = nombre;
        this.max_jugadores = max_jugadores;
        this.id_creador = id_creador;
        this.users = users;
        this.messages = messages;
        this.game = game;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
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

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }
}
