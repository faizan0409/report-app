package com.fitbuddy.fitbudd.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Component
@DynamoDBTable(tableName = "UserTable")
public class User {

    @DynamoDBHashKey(attributeName = "email")
    private String email;

    @DynamoDBAttribute(attributeName = "fName")
    private String fName;

    @DynamoDBAttribute(attributeName = "lName")
    private String lName;


    @DynamoDBAttribute(attributeName = "target")
    private String target;

    @DynamoDBAttribute(attributeName = "preferredActivity")
    private String preferredActivity;
}
