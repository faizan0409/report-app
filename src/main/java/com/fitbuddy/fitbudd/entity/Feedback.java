package com.fitbuddy.fitbudd.entity;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "feedback_db")
public class Feedback {

    @DynamoDBHashKey(attributeName = "feedback_id")  // feedback_id as the partition key
    private Long feedbackId;

    @DynamoDBAttribute(attributeName = "clientEmail")
    private String clientEmail;

    @DynamoDBAttribute(attributeName = "coachEmail")
    private String coachEmail;
    @DynamoDBAttribute(attributeName = "clientName")
    private String clientName;
    @DynamoDBAttribute(attributeName = "feedback")
    private String feedback;

    @DynamoDBAttribute(attributeName = "feedbackDate")
    private String feedbackDate;

    @DynamoDBAttribute(attributeName = "rating")
    private Double rating;

    @DynamoDBAttribute(attributeName = "workout_id")
    private Long workoutId;

}
