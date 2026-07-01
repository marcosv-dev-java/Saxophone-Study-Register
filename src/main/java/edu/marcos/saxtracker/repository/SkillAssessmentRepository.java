package edu.marcos.saxtracker.repository;
import edu.marcos.saxtracker.model.Skill;
import edu.marcos.saxtracker.model.SkillAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface SkillAssessmentRepository extends JpaRepository<SkillAssessment, Long>{
    boolean existsBySkillAndSession_Id(Skill skill, Long sessionId);
    // verifica se já avaliou essa habilidade na sessão
    long countBySession_Id(Long sessionId);
    @Query("SELECT AVG(a.value) FROM SkillAssessment a WHERE a.skill = :skill AND a.session.date BETWEEN :start AND :end")
    // Essa Query retorna a média de valores no período de avaliação.skill = skillSolicitada E avaliação.sessão.data entre começo e fim solicitado
    Double findAverageBySkillAndPeriod(@Param("skill") Skill skill, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
