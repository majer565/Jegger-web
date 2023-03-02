package com.filipmajewski.jeggerweb.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "payment_dealer")
public class PaymentDealer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Timestamp date;

    private int orderID;
    private int dealerID;

    private int payment;

    private String document;

    @Column(name = "payment_amount")
    private int paymentAmount;

    @Column(name = "payment_date")
    private Timestamp paymentDate;

    public PaymentDealer() {
    }

    public PaymentDealer(int orderID, int dealerID, int payment, String document, int paymentAmount, Timestamp paymentDate) {
        this.orderID = orderID;
        this.dealerID = dealerID;
        this.payment = payment;
        this.document = document;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.date = new Timestamp(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public Timestamp getDate() {
        return date;
    }

    public int getDealerID() {
        return dealerID;
    }

    public String getDocument() {
        return document;
    }

    public int getPaymentAmount() {
        return paymentAmount;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getPayment() {
        return payment;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public void setPaymentAmount(int paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }
}
