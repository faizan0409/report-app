package com.fitbuddy.fitbudd.repository;

import com.fitbuddy.fitbudd.entity.Feedback;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.EnableScanCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableScan
@EnableScanCount
@Repository
public interface FeedbackRepository extends PagingAndSortingRepository<Feedback, Long>, DynamoDBCrudRepository<Feedback, Long> {
    Page<Feedback> findByCoachEmail(String coachEmail, Pageable pageable);
    List<Feedback> findByCoachEmail(String coachEmail);
    boolean existsByWorkoutId(Long workoutId);
}