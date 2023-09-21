package com.animal.user.model.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "email")
public class MailSenderDTO{

    String toEmail;

    @Value("${email.email-from}")
    String emailFrom;

    @Value("${email.subject}")
    String subject;

    String code;
}