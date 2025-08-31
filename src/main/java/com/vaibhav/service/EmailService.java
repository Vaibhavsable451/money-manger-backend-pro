package com.vaibhav.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;
    
    public void sendEmail(String to, String subject, String body) {
        sendEmail(to, subject, body, true);
    }
    
    public void sendEmail(String to, String subject, String body, boolean isHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, isHtml);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
            
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while sending email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
    
    public void sendEmailWithTemplate(String to, String subject, String templateName, Context context) {
        try {
            String body = templateEngine.process(templateName, context);
            sendEmail(to, subject, body, true);
        } catch (Exception e) {
            log.error("Error processing email template: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing email template: " + e.getMessage(), e);
        }
    }
}
