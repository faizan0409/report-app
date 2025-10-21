package com.fitbuddy.fitbudd.scheduler;

import com.fitbuddy.fitbudd.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Component
public class ReportGenerationScheduler {

    @Autowired
    private ReportService reportService;

    @Scheduled(cron = "0 0 8 * * *")// Run every 2 minutes
    public void generateWeeklyReport() {
        String weekId = getCurrentWeekId();
        reportService.generateAndSendReport(weekId);
    }

    private String getCurrentWeekId() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int weekOfYear = now.get(WeekFields.of(Locale.getDefault()).weekOfYear());
        return String.format("%d-%02d", year, weekOfYear);
    }
}