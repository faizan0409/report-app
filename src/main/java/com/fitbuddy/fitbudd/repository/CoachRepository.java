package com.fitbuddy.fitbudd.repository;

import com.fitbuddy.fitbudd.entity.Coach;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableScan
public interface CoachRepository extends DynamoDBCrudRepository<Coach, String> {
    Optional<List<Coach>> findByExpertise(String expertise);
    Optional<Coach> findByEmail(String email);
}