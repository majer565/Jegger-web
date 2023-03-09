package com.filipmajewski.jeggerweb.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String TASK_TYPE, String HANDLOWIEC) {
        SimpleMailMessage message = createMailMessage(to, TASK_TYPE, HANDLOWIEC);
        javaMailSender.send(message);
    }

    private SimpleMailMessage createMailMessage(String to, String TASK_TYPE, String HANDLOWIEC) {

        SimpleMailMessage message = new SimpleMailMessage();
        String DATE = refactorTimestamp(new Timestamp(System.currentTimeMillis()));

        String HEADER = "Pojawiło się nowe zadanie:";
        String FOOTER = "Ta wiadomość została wysłana automatycznie. Prosimy nie odpowiadać na tę wiadomość.";

        message.setFrom("noreply@kdf-majewski.pl");
        message.setTo(to);
        message.setSubject("Pojawiło się nowe zadanie.");
        message.setText(
                HEADER + "\n\n" +
                        "\bTyp: \b" + TASK_TYPE + "\n" +
                        "\bOd: \b" + HANDLOWIEC + "\n" +
                        "\bData: \b" + DATE + "\n\n" +
                        FOOTER
        );

        return message;
    }

    private String refactorTimestamp(Timestamp timestamp) {

        if(timestamp == null) {
            return "---";
        }

        return new SimpleDateFormat("dd.MM.yyyy HH:mm").format(timestamp);
    }
}
