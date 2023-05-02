package model;

import jakarta.persistence.*;

@Entity
@Table(name = "amistades")
public class Friendship {
    @Id
    @Column(name = "id_amistad")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_amistad;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "id_amigo1")
    private User amigo1;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "id_amigo2")
    private User amigo2;

    @Column(name = "solicitud")
    private String solicitud;
    @Column(name = "motivo_bloqueo")
    private String motivo_bloqueo;

    public Friendship() {
    }

    public Friendship(Integer id_amistad, User amigo1, User amigo2, String solicitud, String motivo_bloqueo) {
        this.id_amistad = id_amistad;
        this.amigo1 = amigo1;
        this.amigo2 = amigo2;
        this.solicitud = solicitud;
        this.motivo_bloqueo = motivo_bloqueo;
    }

    @Transient
    public String getAmigo1NombreUsuario() {
        if (amigo1 != null) {
            return amigo1.getNombreUsuario();
        }
        return null;
    }

    public Integer getId_amistad() {
        return id_amistad;
    }

    public void setId_amistad(Integer id_amistad) {
        this.id_amistad = id_amistad;
    }

    public User getAmigo1() {
        return amigo1;
    }

    public void setAmigo1(User amigo1) {
        this.amigo1 = amigo1;
    }

    public User getAmigo2() {
        return amigo2;
    }

    public void setAmigo2(User amigo2) {
        this.amigo2 = amigo2;
    }

    public String getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(String solicitud) {
        this.solicitud = solicitud;
    }

    public String getMotivo_bloqueo() {
        return motivo_bloqueo;
    }

    public void setMotivo_bloqueo(String motivo_bloqueo) {
        this.motivo_bloqueo = motivo_bloqueo;
    }
}
