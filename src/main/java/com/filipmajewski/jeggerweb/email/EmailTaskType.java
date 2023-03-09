package com.filipmajewski.jeggerweb.email;

public enum EmailTaskType {
    ORDER_TO_ACCEPTANCE("Rozliczenie do akceptacji"),
    ACCEPTED_ORDER("Zaakceptowane rozliczenie"),
    REJECTED_ORDER("Odrzucenie rozliczenia"),
    ORDER_TO_PAYMENT("Rozliczenie do płatności"),
    REJECTED_PAYMENT("Odrzucenie płatności");

    private final String taskType;

    EmailTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskType() {
        return taskType;
    }
}
