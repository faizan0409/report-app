package com.fitbuddy.fitbudd.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SQSMessage {
    private String type;
    private String status;

}
