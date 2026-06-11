package edu.marcos.saxtracker.model;


import jakarta.persistence.*;

@Entity
@Table(name = "execucao_exercicio")
public class ExerciseExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "valor", nullable = false)
    private Double value;
    @ManyToOne
    @JoinColumn(name = "exercicio_id",nullable = false)
    private Exercise exercise;
    @ManyToOne
    @JoinColumn(name = "sessao_id",nullable = false)
    private Session session;
    @Column(name = "observacao")
    private String notes;


    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public ExerciseExecution() {
    }

    public Long getId() {
        return id;
    }

    public Double getValue() {
        return value;
    }

    public Session getSession() {
        return session;
    }

    public String getNotes() {
        return notes;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
