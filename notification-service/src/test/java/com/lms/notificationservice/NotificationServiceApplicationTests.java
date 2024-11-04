package com.lms.notificationservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.lms.notificationservice.model.Notification;
import com.lms.notificationservice.service.NotificationService;

class NotificationServiceApplicationTests {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    public NotificationServiceApplicationTests() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendNotification_shouldSendEmail() {
        Notification notification = new Notification("test@example.com", "This is a test message", "Test Subject");

        notificationService.sendNotification(notification);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        SimpleMailMessage message = captureSentEmail();
        assertEquals("test@example.com", message.getTo()[0]);
        assertEquals("Test Subject", message.getSubject());
        assertEquals("This is a test message", message.getText());
    }

    private SimpleMailMessage captureSentEmail() {
        var messageCaptor = forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        return messageCaptor.getValue();
    }
}
