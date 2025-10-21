package com.fitbuddy.fitbudd.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EntityScan
@DynamoDBTable(tableName = "workout_db")
public class Workout {

    @DynamoDBHashKey(attributeName = "workout_id")
    private Long workout_id;

    @DynamoDBAttribute(attributeName = "clientEmail")
    private String clientEmail;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "coachEmail-date-index")
    @DynamoDBAttribute(attributeName = "coachEmail")
    private String coachEmail;

    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "coachEmail-date-index")
    @DynamoDBAttribute(attributeName = "date")
    private String date;

    @DynamoDBAttribute(attributeName = "duration")
    private Integer duration;

    @DynamoDBAttribute(attributeName = "status")
    private String status;

    @DynamoDBAttribute(attributeName = "workoutType")
    private String workoutType;
}
