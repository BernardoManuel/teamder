package model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class User{
    @Id
    @Column(name = "cod_user")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "nom_user")
    private String nombreUsuario;
    @Column(name = "password")
    private String password;
    @Column
    private String salt;
    @Column
    private String correo;
    @Column
    private String descripcion;

    @OneToMany(mappedBy = "solicitante", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Request> requests = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "users")
    public Set<Room> rooms = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> messages = new HashSet<>();

    // Relación bidireccional entre User y Amistad
    @OneToMany(mappedBy = "amigo1", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Friendship> amistades = new HashSet<>();

    public User() {
    }


    public User(Integer id, String nombreUsuario, String password, String salt, String correo, String descripcion, Set<Request> requests, Set<Room> rooms, Set<Message> messages, Set<Friendship> amistades) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.salt = salt;
        this.correo = correo;
        this.descripcion = descripcion;
        this.requests = requests;
        this.rooms = rooms;
        this.messages = messages;
        this.amistades = amistades;
    }

    @Transient
    public List<User> getAmigos() {
        List<User> amigos = new ArrayList<>();
        for (Friendship amistad : amistades) {
            if (amistad.getAmigo1().equals(this)) {
                amigos.add(amistad.getAmigo2());
            } else {
                amigos.add(amistad.getAmigo1());
            }
        }
        return amigos;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<Request> getRequests() {
        return requests;
    }

    public void setRequests(Set<Request> requests) {
        this.requests = requests;
    }

    public Set<Room> getRooms() {
        return rooms;
    }

    public void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public Set<Friendship> getAmistades() {
        return amistades;
    }

    public void setAmistades(Set<Friendship> amistades) {
        this.amistades = amistades;
    }
}
