package com.fitbuddy.fitbudd.repository;

import com.fitbuddy.fitbudd.entity.Report;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableScan
public interface ReportRepository extends DynamoDBCrudRepository<Report, String> {
    Optional<Report> findByCoachEmailAndWeekId(String coachEmail, String weekId);
    List<Report> findByWeekId(String weekId);
}