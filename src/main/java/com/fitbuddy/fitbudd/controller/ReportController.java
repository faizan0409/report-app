package com.fitbuddy.fitbudd.controller;

import com.fitbuddy.fitbudd.service.MatrixCalculation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {
    private final MatrixCalculation matrixCalculation;

    // Testing URL
    @GetMapping("/home")
    public String view() {
        return "Hello...";
    }

    @PostMapping("report")
    public void calculateMatrix() {
        matrixCalculation.calculateReports();
    }
}
