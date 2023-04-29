package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    private Integer id;
    @Column(name = "nom_user")
    private String nombreUsuario;
    @Column(name = "contraseña")
    private String contraseña;
    @Column
    private String salt;
    private String correo;
    private String descripcion;

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

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return Objects.equals(getId(), usuario.getId()) && Objects.equals(getNombreUsuario(), usuario.getNombreUsuario()) && Objects.equals(getContraseña(), usuario.getContraseña()) && Objects.equals(getSalt(), usuario.getSalt()) && Objects.equals(getCorreo(), usuario.getCorreo()) && Objects.equals(getDescripcion(), usuario.getDescripcion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNombreUsuario(), getContraseña(), getSalt(), getCorreo(), getDescripcion());
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", contraseña='" + contraseña + '\'' +
                ", salt='" + salt + '\'' +
                ", correo='" + correo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
