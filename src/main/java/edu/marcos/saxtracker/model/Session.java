package edu.marcos.saxtracker.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "sessao")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, name = "data")
    private LocalDate date;
    @Column(name = "observacao")
    private String notes;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SessionStatus status;
    @ManyToOne
    @JoinColumn(name =  "rotina_id",nullable = false)
    private Routine routine;

    public Session() {
        this.status = SessionStatus.OPEN;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public Routine getRoutine() {
        return routine;
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }
}
