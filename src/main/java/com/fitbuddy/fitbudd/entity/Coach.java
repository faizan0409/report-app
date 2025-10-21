package com.fitbuddy.fitbudd.entity;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "coach_db")
public class Coach {
    @DynamoDBHashKey(attributeName = "email")  // email as the partition key
    private String email;

    @DynamoDBAttribute(attributeName = "certificates")
    private Map<String , String > certificates;  // Certificates as a map (e.g., certificate name -> URL)

    @DynamoDBAttribute(attributeName = "description")
    private String description;

    @DynamoDBAttribute(attributeName = "expertise")
    private String expertise;

    @DynamoDBAttribute(attributeName = "fName")
    private String fName;

    @DynamoDBAttribute(attributeName = "lName")
    private String lName;

    @DynamoDBAttribute(attributeName = "profile_picture")
    private String profile_picture;  // URL for profile picture

    @DynamoDBAttribute(attributeName = "rating")
    private Double rating;  // Stored as String, can be modified to a number if necessary

    @DynamoDBAttribute(attributeName = "summary")
    private String summary;
}
