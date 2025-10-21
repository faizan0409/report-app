package com.fitbuddy.fitbudd.repository;

import com.fitbuddy.fitbudd.entity.Workout;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableScan

public interface WorkoutRepository extends DynamoDBCrudRepository<Workout, Long> {

    @Query(filterExpression = "SELECT * FROM Workouts WHERE contains(#date, :date)")
    List<Workout> findAllByDateContaining(@Param("date") String date);

    @Query(filterExpression = "SELECT * FROM Workouts WHERE contains(#clientEmail, :clientEmail) AND contains(#date, :date)")
    Optional<Workout> findByDateAndClientEmail(String date, String clientEmail);

    @Query(filterExpression = "SELECT * FROM Workouts WHERE contains(#clientEmail, :clientEmail)")
    List<Workout> findByClientEmail(String clientEmail);

    Workout findByCoachEmailAndClientEmailAndStatus(String coachEmail, String clientEmail, String status);

    List<Workout> findByDateBetweenAndStatusIn(String startDate, String endDate, List<String> statuses);
}
