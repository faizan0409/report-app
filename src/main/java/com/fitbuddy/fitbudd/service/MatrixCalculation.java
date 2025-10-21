package com.fitbuddy.fitbudd.service;

import com.fitbuddy.fitbudd.entity.Coach;
import com.fitbuddy.fitbudd.entity.Feedback;
import com.fitbuddy.fitbudd.entity.Report;
import com.fitbuddy.fitbudd.entity.Workout;
import com.fitbuddy.fitbudd.repository.CoachRepository;
import com.fitbuddy.fitbudd.repository.FeedbackRepository;
import com.fitbuddy.fitbudd.repository.ReportRepository;
import com.fitbuddy.fitbudd.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class MatrixCalculation {

    private final WorkoutRepository workoutRepository;

    private final FeedbackRepository feedbackRepository;

    private final CoachRepository coachRepository;

    private final ReportRepository reportRepository;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final LocalDate today = LocalDate.now();
    private final LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
    private final LocalDate endOfWeek = today.with(DayOfWeek.SATURDAY);
    private final String weekId = today.getYear() + "-" + today.get(WeekFields.ISO.weekOfWeekBasedYear());
    private final String previousWeekId = today.getYear() + "-" + today.minusWeeks(1).get(WeekFields.ISO.weekOfWeekBasedYear());

    private List<Workout> allWorkoutsList;
    private Map<String, List<Workout>> allWorkoutsListByCoach;
    private int totalHours = 0;
    private int totalWorkouts = 0;
    private long noOfUniqueCoach;
    private Map<String, Integer> coachTotalRatingMap = new HashMap<>();
    private int overallTotalRating = 0;

    public void calculateReports() {
        allWorkoutsList = getAllWorkoutsList();
        if (allWorkoutsList.isEmpty()) {
            return;
        }

        allWorkoutsListByCoach = allWorkoutsList.stream()
                .collect(Collectors.groupingBy(Workout::getCoachEmail));

        calculateAverageFeedback();

        for (String coachEmail : allWorkoutsListByCoach.keySet()) {
            List<Workout> allWorkoutsByCoach = allWorkoutsListByCoach.get(coachEmail);
            storeDataToReportDB(coachEmail, allWorkoutsByCoach);
        }
    }

    private List<Workout> getAllWorkoutsList() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime endOfWeek = startOfWeek.plusDays(7).minusNanos(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String startDate = startOfWeek.format(formatter);
        String endDate = endOfWeek.format(formatter);
        List<String> statuses = Arrays.asList("Finished", "Waiting for Feedback".toUpperCase());
        List<Workout> allWorkouts = StreamSupport.stream(workoutRepository
                        .findByDateBetweenAndStatusIn(startDate, endDate, statuses).spliterator(), false)
                .collect(Collectors.toList());
        totalHours = (int) allWorkouts.stream()
                .map(Workout::getDate)
                .distinct()
                .count();

        totalWorkouts = allWorkouts.size();

        noOfUniqueCoach = allWorkouts.stream()
                .map(Workout::getCoachEmail)
                .distinct()
                .count();

        return allWorkouts;
    }

    private String getCoachName(String email) {
        Optional<Coach> coachOptional = coachRepository.findByEmail(email);
        return coachOptional.map(coach -> coach.getFName() + " " + coach.getLName()).orElse("Unknown");
    }

    private int getAverageHoursLedPerCoach() {
        return (int) (totalHours / noOfUniqueCoach);
    }

    private int getCoachWorkOutsLead(String email) {
        return (int) allWorkoutsList.stream()
                .filter(workout -> workout.getCoachEmail().equals(email))
                .count();
    }


    private void calculateAverageFeedback() {
        Map<String, List<Workout>> listToConsiderForFeedBack = allWorkoutsList.stream()
                .filter(workout -> workout.getStatus().equalsIgnoreCase("Finished"))
                .collect(Collectors.groupingBy(Workout::getCoachEmail));

        for (String coachEmail : listToConsiderForFeedBack.keySet()) {
            List<Workout> workoutsList = listToConsiderForFeedBack.get(coachEmail);
            List<Long> workoutIds = workoutsList.stream()
                    .map(Workout::getWorkout_id)
                    .collect(Collectors.toList());

            List<Feedback> coachFeedbackItems = StreamSupport.stream(feedbackRepository.findAllById(workoutIds).spliterator(), false)
                    .collect(Collectors.toList());

            int coachTotalRating = coachFeedbackItems.stream()
                    .mapToInt(feedback -> feedback.getRating().intValue())
                    .sum();

            coachTotalRatingMap.put(coachEmail, coachTotalRating);
            overallTotalRating += coachTotalRating;
        }
    }

    private List<Feedback> getFeedbackItemsForCoach(String coachEmail) {
        return feedbackRepository.findByCoachEmail(coachEmail);
    }

    private int calculateMinimumFeedback(String coachEmail) {
        List<Feedback> coachFeedbackItems = getFeedbackItemsForCoach(coachEmail);
        return coachFeedbackItems.stream()
                .mapToInt(feedback -> feedback.getRating().intValue())
                .min()
                .orElse(0);
    }

    private int deltaOfCoachWorkoutsPercent(String email, int workoutsLedByEachCoach) {
        Optional<Report> previousReport = reportRepository.findByCoachEmailAndWeekId(email, previousWeekId);
        if (previousReport.isPresent()) {
            int previousWorkoutsLead = previousReport.get().getWorkoutsLedByEachCoach();
            if (previousWorkoutsLead > 0) {
                return (workoutsLedByEachCoach - previousWorkoutsLead) * 100 / previousWorkoutsLead;
            }
        }
        return 0;
    }

    private int calculateDeltaOfMinimumFeedbackPercent(String coachEmail, int currentMinimumFeedback) {
        Optional<Report> previousReport = reportRepository.findByCoachEmailAndWeekId(coachEmail, previousWeekId);
        if (previousReport.isPresent()) {
            int previousMinimumFeedback = previousReport.get().getMinimumFeedback();
            if (previousMinimumFeedback > 0) {
                return (currentMinimumFeedback - previousMinimumFeedback) * 100 / previousMinimumFeedback;
            }
        }
        return 0;
    }

    private boolean storeDataToReportDB(String coachEmail, List<Workout> allWorkoutsByCoach) {
        try {
            int totalHoursByCoach = (int) allWorkoutsByCoach.stream()
                    .map(Workout::getDate)
                    .distinct()
                    .count();

            int averageHoursLedPerCoach = getAverageHoursLedPerCoach();

            int coachWorkOutLead = getCoachWorkOutsLead(coachEmail);
            int deltaOfCoachWorkoutsPercent = deltaOfCoachWorkoutsPercent(coachEmail, coachWorkOutLead);

            int coachTotalRating = coachTotalRatingMap.getOrDefault(coachEmail, 0);
            double averageFeedback = coachWorkOutLead > 0 ? (double) coachTotalRating / coachWorkOutLead : 0;

            int minimumFeedback = calculateMinimumFeedback(coachEmail);
            int deltaOfMinimumFeedbackPercent = calculateDeltaOfMinimumFeedbackPercent(coachEmail, minimumFeedback);

            Report report = new Report();
            report.setCoachEmail(coachEmail);
            report.setWeekId(weekId);
            report.setGymLocation("Gym Location"); // You might want to make this dynamic
            report.setCoachName(getCoachName(coachEmail));
            report.setTimePeriodStart(startOfWeek.format(dateTimeFormatter));
            report.setTimePeriodEnd(endOfWeek.format(dateTimeFormatter));
            report.setTotalHoursByCoach(totalHoursByCoach);
            report.setTotalHours(totalHours);
            report.setAverageHoursLeadByCoach(averageHoursLedPerCoach);
            report.setTotalWorkouts(totalWorkouts);
            report.setWorkoutsLedByEachCoach(coachWorkOutLead);
            report.setDeltaOfCoachWorkoutsPercent(deltaOfCoachWorkoutsPercent);
            report.setDeltaOfMinimumFeedbackPercent(deltaOfMinimumFeedbackPercent);
            report.setAverageFeedback(averageFeedback);
            report.setMinimumFeedback(minimumFeedback);
            report.setTotalRating(coachTotalRating);
            report.setNoOfUniqueCoaches((int) noOfUniqueCoach);

            reportRepository.save(report);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}