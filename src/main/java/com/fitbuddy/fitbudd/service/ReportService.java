package com.fitbuddy.fitbudd.service;

import com.fitbuddy.fitbudd.entity.Report;
import com.fitbuddy.fitbudd.repository.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private EmailService emailService;

    public void generateAndSendReport(String weekId) {
        List<Report> reportData = reportRepository.findByWeekId(weekId);

        logger.info("Retrieved {} reports for week {}", reportData.size(), weekId);
        for (Report report : reportData) {
            logger.debug("Report: {}", report);
        }

        if (reportData.isEmpty()) {
            logger.warn("No data found for week {}", weekId);
            return;
        }

        try {
            byte[] excelFile = excelService.generateExcelReport(reportData, weekId);
            emailService.sendReportEmail(excelFile, weekId, reportData);
            logger.info("Report generated and sent successfully for week {}", weekId);
        } catch (IOException e) {
            logger.error("Error generating Excel report for week {}", weekId, e);
            // Handle the error (e.g., send an error notification email)
        } catch (RuntimeException e) {
            logger.error("Error sending email for week {}", weekId, e);
            // Handle the error (e.g., retry sending the email or log the failure)
        }
    }
}