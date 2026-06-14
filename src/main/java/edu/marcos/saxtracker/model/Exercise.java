package edu.marcos.saxtracker.model;
import jakarta.persistence.*;

@Entity
@Table(name = "exercicios")
public class Exercise {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, name = "nome")
    private String name;
    @Column(name = "descricao")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,name = "tipo")
    private ExerciseType type;
    private Boolean active;

    public Exercise(String name, String description, ExerciseType type) {
        this.name = name;
        this.description = description;
        this.type = type;
        active = true;
    }

    public Exercise() {
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ExerciseType getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(ExerciseType type) {
        this.type = type;
    }
}
