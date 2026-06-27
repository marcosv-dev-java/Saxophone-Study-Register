package edu.marcos.saxtracker.service;
import edu.marcos.saxtracker.dto.progress.SkillProgressResponse;
import edu.marcos.saxtracker.model.Skill;
import edu.marcos.saxtracker.repository.SkillAssessmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
public class ProgressService {
    private SkillAssessmentRepository assessmentRepositoryRepository;

    public ProgressService(SkillAssessmentRepository assessmentRepositoryRepository) {
        this.assessmentRepositoryRepository = assessmentRepositoryRepository;
    }

    private SkillProgressResponse averageInPeriod_filterBySkill(Skill skill, LocalDate start, LocalDate end) {
        return new SkillProgressResponse(skill, assessmentRepositoryRepository.findAverageBySkillAndPeriod(
                skill, start, end
        ));
    }
}
