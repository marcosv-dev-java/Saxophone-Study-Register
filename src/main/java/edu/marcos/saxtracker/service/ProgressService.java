package edu.marcos.saxtracker.service;
import edu.marcos.saxtracker.dto.progress.SkillProgressResponse;
import edu.marcos.saxtracker.dto.progress.SkillWeekComparisonResponse;
import edu.marcos.saxtracker.model.Skill;
import edu.marcos.saxtracker.repository.SkillAssessmentRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;


@Service
public class ProgressService {
    private final SkillAssessmentRepository assessmentRepository;

    public ProgressService(SkillAssessmentRepository assessmentRepositoryRepository) {
        this.assessmentRepository = assessmentRepositoryRepository;
    }

    private SkillProgressResponse averageInPeriod_filterBySkill(Skill skill, LocalDate start, LocalDate end) {
        return new SkillProgressResponse(skill, assessmentRepository.findAverageBySkillAndPeriod(
                skill, start, end
        ));
    }
    private LocalDate[] getWeekRange(int year, int weekNumber) {
        LocalDate start = LocalDate.of(year, 1, 1)
                .with(IsoFields.WEEK_BASED_YEAR, year)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR,weekNumber)
                .with(DayOfWeek.MONDAY);
        return new LocalDate[]{start, start.plusDays(6)};
        // retorna o dia da segunda feira e domingo da semana
    }
    public SkillWeekComparisonResponse compareWeekBySkill(Skill skill,String week){
        String[] parts =  week.split("-");
        int year = Integer.parseInt(parts[0]);
        int weekNumber = Integer.parseInt(parts[1].substring(1));
        LocalDate[] actual =  this.getWeekRange(year, weekNumber);
        SkillProgressResponse actualResponse = averageInPeriod_filterBySkill(skill, actual[0], actual[1]);
        LocalDate[] previous = {actual[0].minusDays(7), actual[0].minusDays(1)};
        SkillProgressResponse previousResponse = averageInPeriod_filterBySkill(skill, previous[0], previous[1]);
        return new SkillWeekComparisonResponse(actualResponse, previousResponse);
    }


}
