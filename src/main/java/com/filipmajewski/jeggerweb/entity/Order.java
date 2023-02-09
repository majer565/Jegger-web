package com.filipmajewski.jeggerweb.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "rozliczenie")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Timestamp date;

    @Column(name = "old_order_number")
    private String oldOrderNumber;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "invoice_price")
    private double invoicePrice;

    @Column(name = "original_price")
    private double originalPrice;

    @Column(name = "discount")
    private int discount;

    @Column(name = "discount_price")
    private double discountPrice;

    @Column(name = "final_price")
    private double finalPrice;

    @Column(name = "userID")
    private int userID;

    @Column(name = "dealer_acceptance")
    private boolean dealerAcceptance;

    @Column(name = "dealer_acceptance_date")
    private Timestamp dealerAcceptanceDate;

    @Column(name = "dealer_payment")
    private boolean dealerPayment;

    @Column(name = "dealer_payment_date")
    private Timestamp dealerPaymentDate;

    @Column(name = "handlowiec_acceptance")
    private boolean handlowiecAcceptance;

    @Column(name = "handlowiec_acceptance_date")
    private Timestamp handlowiecAcceptanceDate;

    @Column(name = "handlowiec_payment")
    private boolean handlowiecPayment;

    @Column(name = "handlowiec_payment_date")
    private Timestamp handlowiecPaymentDate;

    public Order() {}

    public Order(Timestamp date, String oldOrderNumber, String invoiceNumber, double invoicePrice, double originalPrice, int discount, double discountPrice, double finalPrice, int userID, boolean accept, Date acceptDate) {
        this.date = date;
        this.oldOrderNumber = oldOrderNumber;
        this.invoiceNumber = invoiceNumber;
        this.invoicePrice = invoicePrice;
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.discountPrice = discountPrice;
        this.finalPrice = finalPrice;
        this.userID = userID;

        this.dealerAcceptance = accept;
        this.handlowiecAcceptance = accept;
        if(acceptDate != null) {
            this.dealerAcceptanceDate = new Timestamp(acceptDate.getTime());
            this.handlowiecAcceptanceDate = new Timestamp(acceptDate.getTime());
        }

        this.dealerPayment = false;
        this.dealerPaymentDate = null;
        this.handlowiecPayment = false;
        this.handlowiecPaymentDate = null;
    }

    public Order(String oldOrderNumber,
                 String invoiceNumber,
                 double invoicePrice,
                 double originalPrice,
                 int discount,
                 double discountPrice,
                 double finalPrice,
                 int userID) {

        this.date = new Timestamp(System.currentTimeMillis());
        this.oldOrderNumber = oldOrderNumber;
        this.invoiceNumber = invoiceNumber;
        this.invoicePrice = invoicePrice;
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.discountPrice = discountPrice;
        this.finalPrice = finalPrice;
        this.userID = userID;
        this.dealerAcceptance = false;
        this.handlowiecAcceptance = false;
        this.dealerPayment = false;
        this.dealerPaymentDate = null;
        this.handlowiecPayment = false;
        this.handlowiecPaymentDate = null;
    }

    public int getId() {
        return id;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public double getInvoicePrice() {
        return invoicePrice;
    }

    public void setInvoicePrice(double invoicePrice) {
        this.invoicePrice = invoicePrice;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public boolean getDealerAcceptance() {
        return dealerAcceptance;
    }

    public void setDealerAcceptance(boolean dealerAcceptance) {
        this.dealerAcceptance = dealerAcceptance;
    }

    public Timestamp getDealerAcceptanceDate() {
        return dealerAcceptanceDate;
    }

    public void setDealerAcceptanceDate(Timestamp dealerAcceptanceDate) {
        this.dealerAcceptanceDate = dealerAcceptanceDate;
    }

    public boolean getHandlowiecAcceptance() {
        return handlowiecAcceptance;
    }

    public void setHandlowiecAcceptance(boolean handlowiecAcceptance) {
        this.handlowiecAcceptance = handlowiecAcceptance;
    }

    public Timestamp getHandlowiecAcceptanceDate() {
        return handlowiecAcceptanceDate;
    }

    public void setHandlowiecAcceptanceDate(Timestamp handlowiecAcceptanceDate) {
        this.handlowiecAcceptanceDate = handlowiecAcceptanceDate;
    }

    public String getOldOrderNumber() {
        return oldOrderNumber;
    }

    public void setOldOrderNumber(String oldOrderNumber) {
        this.oldOrderNumber = oldOrderNumber;
    }
}
