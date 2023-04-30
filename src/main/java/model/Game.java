package model;

import jakarta.persistence.*;


@Entity
@Table(name = "juegos")
public class Game {
    @Id
    @Column(name = "cod_juego")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "nom_juego")
    private String name;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "genero")
    private String genero;

    public Game() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }
}
