# Biweekly Coach Performance Reporting Microservice – Fitbuddy App

This Spring Boot-based microservice automates the biweekly generation and delivery of performance reports for gym coaches within the Fitbuddy platform. Designed with a serverless architecture, it leverages AWS EventBridge, SQS, SNS, and RabbitMQ to orchestrate scheduled tasks, ensure reliable message delivery, and enable scalable event-driven processing.

### Email Delivery:
Sends reports directly to the app owner via email, ensuring timely insights for administrative review along with excel sheet.

### Multi-format Reporting: 
- Generates detailed Excel reports and HTML email summaries with metrics including:
- Number of sessions conducted
- Client ratings and feedback
- Coach engagement and positivity indicators

### Automated Scheduling: 
Uses AWS EventBridge to trigger biweekly report generation workflows.

### Asynchronous Processing: 
Integrates RabbitMQ and AWS SQS for decoupled, fault-tolerant task execution.

### Scalable & Serverless:
Built for high availability and low operational overhead using cloud-native components.

