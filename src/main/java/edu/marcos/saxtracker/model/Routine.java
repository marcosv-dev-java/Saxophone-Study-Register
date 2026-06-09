package edu.marcos.saxtracker.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "rotina")
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "nome")
    private String name;
    @ManyToMany
    @JoinTable(
        name = "rotina_exercicio",
            joinColumns = @JoinColumn(name = "rotina_id"),
            inverseJoinColumns = @JoinColumn(name = "exercicio_id")

    )
    private List<Exercise> exercises;
    @Column(nullable = false, name = "ativo")
    private Boolean active;
    @Column(name = "descricao")
    private String description;

    public Routine() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public Boolean getActive() {
        return active;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }
}
