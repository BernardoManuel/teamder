package model;

import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "peticion")
public class Request {
    @Id
    @Column(name = "id_peticion")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "id_sala")
    private Room sala;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "id_solicitante")
    private User solicitante;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "id_solicitado")
    private User solicitado;

    @Column(name = "estado")
    private String estado;

    @Column(name = "is_shown")
    private Boolean isShown;

    public Request() {
    }

    public Request(Integer id, Room sala, User solicitante, User solicitado, String estado, Boolean isShown) {
        this.id = id;
        this.sala = sala;
        this.solicitante = solicitante;
        this.solicitado = solicitado;
        this.estado = estado;
        this.isShown = isShown;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Room getSala() {
        return sala;
    }

    public void setSala(Room sala) {
        this.sala = sala;
    }

    public User getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(User solicitante) {
        this.solicitante = solicitante;
    }

    public User getSolicitado() {
        return solicitado;
    }

    public void setSolicitado(User solicitado) {
        this.solicitado = solicitado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getShown() {
        return isShown;
    }

    public void setShown(Boolean shown) {
        isShown = shown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        Request request = (Request) o;
        return Objects.equals(getId(), request.getId()) && Objects.equals(getSala(), request.getSala()) && Objects.equals(getSolicitante(), request.getSolicitante()) && Objects.equals(getSolicitado(), request.getSolicitado()) && Objects.equals(getEstado(), request.getEstado()) && Objects.equals(isShown, request.isShown);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSala(), getSolicitante(), getSolicitado(), getEstado(), isShown);
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", sala=" + sala +
                ", solicitante=" + solicitante +
                ", solicitado=" + solicitado +
                ", estado='" + estado + '\'' +
                ", isShown=" + isShown +
                '}';
    }
}
