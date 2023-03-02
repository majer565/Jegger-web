package com.filipmajewski.jeggerweb.container;

public class PaymentInfo {

    private final String orderID;

    private final String receiver;

    private final String paymentAmount;

    /**
     * 0 - Dealer <br>
     * 1- Handlowiec Dealera
     * */
    private final int person;

    public PaymentInfo(String orderID, String receiver, String paymentAmount, int person) {
        this.orderID = orderID;
        this.receiver = receiver;
        this.paymentAmount = paymentAmount;
        this.person = person;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public int getPerson() {
        return person;
    }
}
