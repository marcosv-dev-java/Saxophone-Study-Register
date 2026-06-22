package edu.marcos.saxtracker.repository;

import edu.marcos.saxtracker.model.Skill;
import edu.marcos.saxtracker.model.SkillAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillAssessmentRepository extends JpaRepository<SkillAssessment, Long>{
    boolean existsBySkillAndSession_Id(Skill skill, Long sessionId);
    // verifica se já avaliou essa habilidade na sessão
}
