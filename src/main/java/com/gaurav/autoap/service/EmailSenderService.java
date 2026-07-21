package com.gaurav.autoap.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    private final JavaMailSender mailSender;

    @Value("${app.payroll.email}")
    private String payrollEmail;

    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendReport(String reportContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(payrollEmail);
        message.setSubject("AutoAP Daily Invoice Processing Report");
        message.setText(reportContent);
        mailSender.send(message);
    }
}