package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "calificaciones")
public class Calificacion {
    @Id
    @Column(name = "id_calif")
    private Integer id;
    @Column(name = "id_user")
    private Integer idUser;
    @Column(name = "id_juego")
    private Integer idJuego;
    @Column(name = "id_evaluador")
    private Integer idEvaluador;
    @Column(name = "fecha_calif")
    private Date fechaCalif;
    private String comentario;
    private Double valoracion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Integer getIdJuego() {
        return idJuego;
    }

    public void setIdJuego(Integer idJuego) {
        this.idJuego = idJuego;
    }

    public Integer getIdEvaluador() {
        return idEvaluador;
    }

    public void setIdEvaluador(Integer idEvaluador) {
        this.idEvaluador = idEvaluador;
    }

    public Date getFechaCalif() {
        return fechaCalif;
    }

    public void setFechaCalif(Date fechaCalif) {
        this.fechaCalif = fechaCalif;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Double getValoracion() {
        return valoracion;
    }

    public void setValoracion(Double valoracion) {
        this.valoracion = valoracion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Calificacion that)) return false;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getIdUser(), that.getIdUser()) && Objects.equals(getIdJuego(), that.getIdJuego()) && Objects.equals(getIdEvaluador(), that.getIdEvaluador()) && Objects.equals(getFechaCalif(), that.getFechaCalif()) && Objects.equals(getComentario(), that.getComentario()) && Objects.equals(getValoracion(), that.getValoracion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getIdUser(), getIdJuego(), getIdEvaluador(), getFechaCalif(), getComentario(), getValoracion());
    }

    @Override
    public String toString() {
        return "Calificacion{" +
                "id=" + id +
                ", idUser=" + idUser +
                ", idJuego=" + idJuego +
                ", idEvaluador=" + idEvaluador +
                ", fechaCalif=" + fechaCalif +
                ", comentario='" + comentario + '\'' +
                ", valoracion=" + valoracion +
                '}';
    }
}
