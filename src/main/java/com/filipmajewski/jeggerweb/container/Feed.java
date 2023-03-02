package com.filipmajewski.jeggerweb.container;

public class Feed {

    private final int orderID;

    private final String type;

    public Feed(int orderID, String type) {
        this.orderID = orderID;
        this.type = type;
    }

    public int getOrderID() {
        return orderID;
    }

    public String getType() {
        return type;
    }
}
