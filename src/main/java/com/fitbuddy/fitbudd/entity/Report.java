package com.fitbuddy.fitbudd.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Builder
@DynamoDBTable(tableName = "report_db")
public class Report {

    @DynamoDBHashKey
    private String coachEmail;

    @DynamoDBAttribute
    private String weekId;

    @DynamoDBAttribute
    private Double averageFeedback;

    @DynamoDBAttribute
    private Integer averageHoursLeadByCoach;

    @DynamoDBAttribute
    private String coachName;

    @DynamoDBAttribute
    private Integer deltaOfCoachWorkoutsPercent;

    @DynamoDBAttribute
    private Integer deltaOfMinimumFeedbackPercent;

    @DynamoDBAttribute
    private String gymLocation;

    @DynamoDBAttribute
    private Integer minimumFeedback;

    @DynamoDBAttribute
    private Integer noOfUniqueCoaches;

    @DynamoDBAttribute
    private String timePeriodEnd;

    @DynamoDBAttribute
    private String timePeriodStart;

    @DynamoDBAttribute
    private Integer totalHours;

    @DynamoDBAttribute
    private Integer totalHoursByCoach;

    @DynamoDBAttribute
    private Integer totalRating;

    @DynamoDBAttribute
    private Integer totalWorkouts;

    @DynamoDBAttribute
    private Integer workoutsLedByEachCoach;
}