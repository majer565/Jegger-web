package com.filipmajewski.jeggerweb.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "rozliczenie_dealer")
public class OrderDealer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Timestamp date;

    private int orderID;

    private String name;

    private long nip;

    private double price;

    private String document;

    public OrderDealer() {}

    public OrderDealer(Timestamp date, int orderID, String name, long nip, double price, String document) {
        this.date = date;
        this.orderID = orderID;
        this.name = name;
        this.nip = nip;
        this.price = price;
        this.document = document;
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

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNip() {
        return nip;
    }

    public void setNip(long nip) {
        this.nip = nip;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

}
