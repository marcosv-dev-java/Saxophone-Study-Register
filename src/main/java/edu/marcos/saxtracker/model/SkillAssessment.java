package edu.marcos.saxtracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "avaliacao_habilidade")
public class SkillAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,name = "habilidade")
    private Skill skill;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessao_id",nullable = false)
    private Session session;
    @Column(nullable = false, name = "valor")
    private Integer value;

    public SkillAssessment() {
    }

    public SkillAssessment(Skill skill, Session session, Integer value) {
        this.skill = skill;
        this.session = session;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public Skill getSkill() {
        return skill;
    }

    public Session getSession() {
        return session;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}

