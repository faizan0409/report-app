package com.fitbuddy.fitbudd.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.fitbuddy.fitbudd.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service
public class EmailService {

    @Autowired
    private AmazonSimpleEmailService sesClient;

    @Autowired
    @Qualifier("emailTemplateEngine")
    private SpringTemplateEngine templateEngine;


    @Value("${report.sender.email}")
    private String senderEmail;

    @Value("${report.admin.email}")
    private String adminEmail;

    public void sendReportEmail(byte[] excelFile, String weekId, List<Report> reportData) {
        try {
            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.setSubject("Weekly Gym Performance Report - Week " + weekId, "UTF-8");
            mimeMessage.setFrom(new InternetAddress(senderEmail));
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(adminEmail));

            // Create a multipart/alternative child container
            MimeMultipart msg_body = new MimeMultipart("alternative");

            // Create the HTML part
            MimeBodyPart htmlPart = new MimeBodyPart();
            String htmlContent = generateEmailContent(weekId, reportData);
            htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");

            // Add the HTML part to the child container
            msg_body.addBodyPart(htmlPart);

            // Create a multipart/mixed parent container
            MimeMultipart msg = new MimeMultipart("mixed");

            // Add the child container to the parent container
            MimeBodyPart msg_body_part = new MimeBodyPart();
            msg_body_part.setContent(msg_body);
            msg.addBodyPart(msg_body_part);

            // Add the attachment
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.setFileName("GymPerformanceReport-Week" + weekId + ".xlsx");
            attachmentPart.setContent(excelFile, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            msg.addBodyPart(attachmentPart);

            // Set the content of the message
            mimeMessage.setContent(msg);

            // Send the email
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            mimeMessage.writeTo(outputStream);
            RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
            SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
            sesClient.sendRawEmail(rawEmailRequest);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String generateEmailContent(String weekId, List<Report> reportData) {
        Context context = new Context();
        context.setVariable("weekId", weekId);
        context.setVariable("reports", reportData);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (!reportData.isEmpty()) {
            Report firstReport = reportData.get(0);
            LocalDate startDate = LocalDate.parse(firstReport.getTimePeriodStart(), formatter);
            LocalDate endDate = LocalDate.parse(firstReport.getTimePeriodEnd(), formatter);

            context.setVariable("weekStartDate", Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            context.setVariable("weekEndDate", Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            // Set default dates or handle empty case
            LocalDate now = LocalDate.now();
            context.setVariable("weekStartDate", Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            context.setVariable("weekEndDate", Date.from(now.plusDays(6).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        return templateEngine.process("emailTemplate", context);
    }
}