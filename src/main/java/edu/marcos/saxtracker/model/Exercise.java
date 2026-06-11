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


    public Exercise() {
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
