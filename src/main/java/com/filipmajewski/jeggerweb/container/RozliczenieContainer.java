package com.filipmajewski.jeggerweb.container;


import java.sql.Timestamp;
import java.util.Date;

public class RozliczenieContainer {

    private String oldOrderNumber;
    private Timestamp date;
    private String invoiceNumber;
    private double invoicePrice;
    private double originalPrice;
    private double discount;
    private double discountPrice;
    private double finalPrice;
    private int userID;
    private int accept;
    private Date acceptanceDate;

    private String dealerName;
    private double dealerPrice;
    private String dealerDocument;

    private String handlowiecName;
    private double handlowiecPrice;
    private String handlowiecDocument;

    public RozliczenieContainer() {}

    public RozliczenieContainer(String oldOrderNumber, Timestamp date, String invoiceNumber, double invoicePrice, double originalPrice, double discount, double discountPrice, double finalPrice, int userID, int accept, Date acceptanceDate, String dealerName, double dealerPrice, String dealerDocument, String handlowiecName, double handlowiecPrice, String handlowiecDocument) {
        this.oldOrderNumber = oldOrderNumber;
        this.date = date;
        this.invoiceNumber = invoiceNumber;
        this.invoicePrice = invoicePrice;
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.discountPrice = discountPrice;
        this.finalPrice = finalPrice;
        this.userID = userID;
        this.accept = accept;
        this.acceptanceDate = acceptanceDate;
        this.dealerName = dealerName;
        this.dealerPrice = dealerPrice;
        this.dealerDocument = dealerDocument;
        this.handlowiecName = handlowiecName;
        this.handlowiecPrice = handlowiecPrice;
        this.handlowiecDocument = handlowiecDocument;
    }

    public String getOldOrderNumber() {
        return oldOrderNumber;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public double getInvoicePrice() {
        return invoicePrice;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public int getUserID() {
        return userID;
    }

    public int getAccept() {
        return accept;
    }

    public Date getAcceptanceDate() {
        return acceptanceDate;
    }

    public String getDealerName() {
        return dealerName;
    }

    public double getDealerPrice() {
        return dealerPrice;
    }

    public String getDealerDocument() {
        return dealerDocument;
    }

    public String getHandlowiecName() {
        return handlowiecName;
    }

    public double getHandlowiecPrice() {
        return handlowiecPrice;
    }

    public String getHandlowiecDocument() {
        return handlowiecDocument;
    }

    public void setOldOrderNumber(String oldOrderNumber) {
        this.oldOrderNumber = oldOrderNumber;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setInvoicePrice(double invoicePrice) {
        this.invoicePrice = invoicePrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setAccept(int accept) {
        this.accept = accept;
    }

    public void setAcceptanceDate(Date acceptanceDate) {
        this.acceptanceDate = acceptanceDate;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public void setDealerPrice(double dealerPrice) {
        this.dealerPrice = dealerPrice;
    }

    public void setDealerDocument(String dealerDocument) {
        this.dealerDocument = dealerDocument;
    }

    public void setHandlowiecName(String handlowiecName) {
        this.handlowiecName = handlowiecName;
    }

    public void setHandlowiecPrice(double handlowiecPrice) {
        this.handlowiecPrice = handlowiecPrice;
    }

    public void setHandlowiecDocument(String handlowiecDocument) {
        this.handlowiecDocument = handlowiecDocument;
    }
}
