package model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class User {
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
    private String correo;
    private String descripcion;
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "users")
    public Set<Room> rooms = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> messages = new HashSet<>();

    // Relación bidireccional entre User y Amistad
    @OneToMany(mappedBy = "amigo1", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Friendship> amistades = new HashSet<>();

    //@OneToMany(mappedBy = "amigo2", cascade = CascadeType.ALL, orphanRemoval = true)
    //private Set<Friendship> amistades2 = new HashSet<>();

    public User() {
    }

    public User(Integer id, String nombreUsuario, String password, String salt, String correo, String descripcion, Set<Room> rooms, Set<Message> messages) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.salt = salt;
        this.correo = correo;
        this.descripcion = descripcion;
        this.rooms = rooms;
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", contraseña='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", correo='" + correo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
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

    public void setPassword(String contraseña) {
        this.password = contraseña;
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
}
