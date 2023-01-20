package com.filipmajewski.jeggerweb.container;

public class OpenOrderReport {

    private final int orderNumber;

    private final String username;

    private final String orderDate;

    private final String dealerName;

    public OpenOrderReport(int orderNumber, String username, String orderDate, String dealerName) {
        this.orderNumber = orderNumber;
        this.username = username;
        this.orderDate = orderDate;
        this.dealerName = dealerName;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getDealerName() {
        return dealerName;
    }
}
