package edu.marcos.saxtracker.service;
import edu.marcos.saxtracker.dto.progress.SkillProgressResponse;
import edu.marcos.saxtracker.dto.progress.SkillWeekComparisonResponse;
import edu.marcos.saxtracker.dto.progress.WeeklyProgressResponse;
import edu.marcos.saxtracker.exceptions.ResourceNotFoundException;
import edu.marcos.saxtracker.model.Exercise;
import edu.marcos.saxtracker.model.Skill;
import edu.marcos.saxtracker.repository.ExerciseExecutionRepository;
import edu.marcos.saxtracker.repository.ExerciseRepository;
import edu.marcos.saxtracker.repository.SkillAssessmentRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


@Service
public class ProgressService {
    private final SkillAssessmentRepository assessmentRepository;
    private final ExerciseExecutionRepository executionRepository;
    private final ExerciseRepository exerciseRepository;

    public ProgressService(SkillAssessmentRepository assessmentRepository, ExerciseExecutionRepository executionRepository, ExerciseRepository exerciseRepository) {
        this.assessmentRepository = assessmentRepository;
        this.executionRepository = executionRepository;
        this.exerciseRepository = exerciseRepository;
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
    public List<WeeklyProgressResponse> getExerciseEvolutionInPeriod(Long exerciseId, Integer weekPeriod){
        /*TODO: criar o método para calcular o progresso da evolução do exericio do periodo atual até a semanas solicitadas
        ex: Semanas solicitadas = 10, calcular da semana atual até 10 semanas atras
         */
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(
                () -> new ResourceNotFoundException("Exercise not found with id: " + exerciseId)
        );
        if (weekPeriod < 0) throw new IllegalArgumentException("Weeks solicited cannot be less than zero");
        List<WeeklyProgressResponse> responses = new ArrayList<>();
        LocalDate sundayActual = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        for (int i = 0; i < weekPeriod; i++) {
            LocalDate monday = sundayActual.minusDays(6);
            Double weekAverage = executionRepository.findAverageByExerciseAndPeriod(exercise,monday,sundayActual);
            int weekBasedYear = monday.get(IsoFields.WEEK_BASED_YEAR);
            int weekNumber = monday.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            responses.add(new WeeklyProgressResponse(weekBasedYear + "-W"+ weekNumber,weekAverage));
            sundayActual = monday.minusDays(1);
        }
        Collections.reverse(responses);
        return responses;
    }


}
