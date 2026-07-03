package edu.marcos.saxtracker.service;
import edu.marcos.saxtracker.dto.exercise.ExerciseSummary;
import edu.marcos.saxtracker.dto.progress.*;
import edu.marcos.saxtracker.exceptions.ResourceNotFoundException;
import edu.marcos.saxtracker.model.Exercise;
import edu.marcos.saxtracker.model.Skill;
import edu.marcos.saxtracker.repository.ExerciseExecutionRepository;
import edu.marcos.saxtracker.repository.ExerciseRepository;
import edu.marcos.saxtracker.repository.SkillAssessmentRepository;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class ProgressService {
    private final SkillAssessmentRepository assessmentRepository;
    private final ExerciseExecutionRepository executionRepository;
    private final ExerciseRepository exerciseRepository;
    private static final int MAX_WEEK_PERIOD = 53;

    public ProgressService(SkillAssessmentRepository assessmentRepository, ExerciseExecutionRepository executionRepository, ExerciseRepository exerciseRepository) {
        this.assessmentRepository = assessmentRepository;
        this.executionRepository = executionRepository;
        this.exerciseRepository = exerciseRepository;
    }
    private ProgressDate isoParseInt(String iso){
        int year;
        int weekNumber;
        if (iso == null || !iso.matches("^\\d{4}-[Ww]\\d{2}$")) {
            throw new IllegalArgumentException("Malformed pattern. Use the format (yyyy-Www), e.g., 2026-W15");
        }
        String[] parts =  iso.split("-");
        try {
             year = Integer.parseInt(parts[0]);
             weekNumber = Integer.parseInt(parts[1].substring(1));
        }catch (ArrayIndexOutOfBoundsException | NumberFormatException | StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Malformed pattern. use (yyyy-Www)");
        }
        if (weekNumber < 1 || weekNumber > MAX_WEEK_PERIOD)
            throw new IllegalArgumentException("Invalid week number. It must be between 01 and 53.");
        return new ProgressDate(year,weekNumber);
    }
    private WeeklyProgressResponse buildWeeklyResponse(LocalDate monday, LocalDate sunday, Exercise exercise){
        Double weekAverage = executionRepository.findAverageByExerciseAndPeriod(exercise,monday,sunday);
        int weekBasedYear = monday.get(IsoFields.WEEK_BASED_YEAR);
        int weekNumber = monday.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        return new WeeklyProgressResponse(weekBasedYear + "-W" + String.format("%02d", weekNumber), weekAverage);
    }

    private SkillProgressResponse calculateSkillAverageForPeriod(Skill skill, LocalDate start, LocalDate end) {
        return new SkillProgressResponse(skill, assessmentRepository.findAverageBySkillAndPeriod(
                skill, start, end
        ));
    }
    private WeekRange getWeekRange(int year, int weekNumber) {
        try {
            LocalDate start = LocalDate.of(year, 1, 1)
                    .with(IsoFields.WEEK_BASED_YEAR, year)
                    .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekNumber)
                    .with(DayOfWeek.MONDAY);
            return new WeekRange(start,start.plusDays(6));
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("The week " + weekNumber + " does not exist in the year " + year);
        }
    }
    public SkillWeekComparisonResponse compareWeekBySkill(Skill skill,String week){
        ProgressDate dates =  this.isoParseInt(week);
        WeekRange actual =  this.getWeekRange(dates.year(), dates.weekNumber());
        SkillProgressResponse actualResponse = calculateSkillAverageForPeriod(skill, actual.start(), actual.end());
        WeekRange previous = new WeekRange(actual.start().minusDays(7), actual.start().minusDays(1));
        SkillProgressResponse previousResponse = calculateSkillAverageForPeriod(skill, previous.start(), previous.end());
        return new SkillWeekComparisonResponse(actualResponse, previousResponse);
    }
    public List<WeeklyProgressResponse> getExerciseEvolutionInPeriod(Long exerciseId, Integer weekPeriod){
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(
                () -> new ResourceNotFoundException("Exercise not found with id: " + exerciseId)
        );
        if (weekPeriod <= 0) throw new IllegalArgumentException
                ("Weeks solicited cannot be less or equal than zero");
        if (weekPeriod > MAX_WEEK_PERIOD) throw new IllegalArgumentException
                ("Cannot request a period greater than " + MAX_WEEK_PERIOD + " weeks.");
        List<WeeklyProgressResponse> responses = new ArrayList<>();
        LocalDate sundayActual = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        for (int i = 0; i < weekPeriod; i++) {
            LocalDate monday = sundayActual.minusDays(6);
            responses.add(this.buildWeeklyResponse(monday,sundayActual,exercise));
            sundayActual = monday.minusDays(1);
        }
        Collections.reverse(responses);
        return responses;
    }
    public WeeklyProgressResponse getExerciseEvolutionInActualWeek(Long exerciseId){
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(
                () -> new ResourceNotFoundException("Exercise not found with id: " + exerciseId)
        );
        LocalDate sundayActual = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDate monday = sundayActual.minusDays(6);
        return this.buildWeeklyResponse(monday,sundayActual,exercise);
    }
    public WeekSummaryProgress getWeekSummary(String week){
        ProgressDate dates = this.isoParseInt(week);
        WeekRange weekRange = this.getWeekRange(dates.year(), dates.weekNumber());
        Optional<Exercise> mostPracticed = executionRepository.findMostPracticedExercise(weekRange.start(),weekRange.end());

        ExerciseSummary exerciseSummary = mostPracticed
                .map(e -> new ExerciseSummary(e.getId(), e.getName()))
                .orElse(null);
        return new WeekSummaryProgress(exerciseSummary,
                executionRepository.averageWeeklyByBpm(weekRange.start(), weekRange.end()),
                executionRepository.averageWeeklyByValue(weekRange.start(), weekRange.end()));

    }
    public WeekSummaryProgress getWeekSummaryInActualWeek(){
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDate monday = sunday.minusDays(6);
        Optional<Exercise> mostPracticed = executionRepository.findMostPracticedExercise(monday,sunday);
        ExerciseSummary exerciseSummary = mostPracticed
                .map(e -> new ExerciseSummary(e.getId(), e.getName()))
                .orElse(null);
        return new WeekSummaryProgress(exerciseSummary,
                executionRepository.averageWeeklyByBpm(monday, sunday),
                executionRepository.averageWeeklyByValue(monday, sunday));
    }
    public List<SkillProgressResponse> skillSummaryInWeek(String week){
        ProgressDate dates = this.isoParseInt(week);
        WeekRange weekRange = this.getWeekRange(dates.year(), dates.weekNumber());
        List<SkillProgressResponse> responses = new ArrayList<>();
        for (Skill skill : Skill.values()) {
            SkillProgressResponse response = this.calculateSkillAverageForPeriod(skill, weekRange.start(),weekRange.end());
            responses.add(response);
        }
        return responses;
    }
}
