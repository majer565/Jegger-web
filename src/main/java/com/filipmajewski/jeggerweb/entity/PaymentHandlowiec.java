package com.filipmajewski.jeggerweb.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "payment_handlowiec")
public class PaymentHandlowiec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Timestamp date;

    private int orderID;
    private int handlowiecID;

    private int payment;

    private String document;

    @Column(name = "payment_amount")
    private int paymentAmount;

    @Column(name = "payment_date")
    private Timestamp paymentDate;

    public PaymentHandlowiec(){}

    public PaymentHandlowiec(int orderID, int handlowiecID, int payment, String document, int paymentAmount, Timestamp paymentDate) {
        this.orderID = orderID;
        this.handlowiecID = handlowiecID;
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

    public int getHandlowiecID() {
        return handlowiecID;
    }

    public int getPayment() {
        return payment;
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

    public void setHandlowiecID(int handlowiecID) {
        this.handlowiecID = handlowiecID;
    }
}
