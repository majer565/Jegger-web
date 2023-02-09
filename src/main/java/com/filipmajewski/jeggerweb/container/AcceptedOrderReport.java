package com.filipmajewski.jeggerweb.container;

public class AcceptedOrderReport {

    private final int orderNumber;

    private final String username;

    private final String orderAcceptDate;

    public AcceptedOrderReport(int orderNumber, String username, String orderAcceptDate) {
        this.orderNumber = orderNumber;
        this.username = username;
        this.orderAcceptDate = orderAcceptDate;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getOrderAcceptDate() {
        return orderAcceptDate;
    }
}
