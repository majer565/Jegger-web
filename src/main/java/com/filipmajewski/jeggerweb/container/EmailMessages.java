package com.filipmajewski.jeggerweb.container;

import org.springframework.mail.SimpleMailMessage;

import java.util.Map;

public class EmailMessages {

    public static final Map<String, String> MAP_TAKS_TYPE = Map.of(
            "ORDER_TO_ACCEPTANCE", "Rozliczenie do akceptacji",
            "ACCEPTED_ORDER", "Zaakceptowane rozliczenie",
            "REJECTED_ORDER", "Odrzucenie rozliczenia",
            "ORDER_TO_PAYMENT", "Rozliczenie do płatności",
            "REJECTED_PAYMENT", "Odrzucenie płatności"
    );

    private final String TASK_TYPE;

    private final String DATE;

    private final String HANDLOWIEC;

    public EmailMessages(String TASK_TYPE, String DATE, String HANDLOWIEC) {
        this.TASK_TYPE = MAP_TAKS_TYPE.get(TASK_TYPE);
        this.DATE = DATE;
        this.HANDLOWIEC = HANDLOWIEC;
    }

    public SimpleMailMessage createMailMessage(String to, String subject) {

        SimpleMailMessage message = new SimpleMailMessage();

        String HEADER = "Pojawiło się nowe zadanie:";
        String FOOTER = "Ta wiadomość została wysłana automatycznie. Prosimy nie odpowiadać na tę wiadomość.";

        message.setFrom("noreplyfilip@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(
                HEADER + "\n\n" +
                "\bTyp: \b" + TASK_TYPE + "\n" +
                "\bOd: \b" + HANDLOWIEC + "\n" +
                "\bData: \b" + DATE + "\n\n" +
                 FOOTER
        );

        return message;
    }
}
