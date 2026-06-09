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
    private SessionStatus status;
    @ManyToOne
    @JoinColumn(name =  "rotina_id",nullable = false)
    private Routine routine;
}
