package com.fitbuddy.fitbudd.repository;

import com.fitbuddy.fitbudd.entity.User;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;

import java.util.Optional;

public interface UserRepository extends DynamoDBCrudRepository<User, String> {
    Optional<User> findByEmail(String email);
}
