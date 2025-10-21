package com.fitbuddy.fitbudd.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SQSListenerService {

    private final AmazonSQS amazonSQS;
    private final ObjectMapper objectMapper;
    private final MatrixCalculation matrixCalculation;

    @Value("${aws.sqs.queue.url}")
    private String queueUrl;

    @Scheduled(fixedDelay = 1000) // Poll every second
    public void receiveMessage() {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl)
                .withMaxNumberOfMessages(10)
                .withWaitTimeSeconds(20);

        List<Message> messages = amazonSQS.receiveMessage(receiveMessageRequest).getMessages();
        System.out.print(messages);
        for (Message message : messages) {
            try {
                matrixCalculation.calculateReports();
                amazonSQS.deleteMessage(queueUrl, message.getReceiptHandle());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
