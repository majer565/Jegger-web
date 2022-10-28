package com.filipmajewski.jeggerweb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "rozliczenie")
public class Order {

    @Id
    private int id;

    private Timestamp date;

    @Column(name = "order_number")
    private String orderNumber;

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

    @Column(name = "handlowiec_acceptance")
    private boolean handlowiecAcceptance;

    @Column(name = "handlowiec_acceptance_date")
    private Timestamp handlowiecAcceptanceDate;

    private int historyID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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

    public int getHistoryID() {
        return historyID;
    }

    public void setHistoryID(int historyID) {
        this.historyID = historyID;
    }
}
