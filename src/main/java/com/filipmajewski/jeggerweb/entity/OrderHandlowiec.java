package com.filipmajewski.jeggerweb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "rozliczenie_handlowiec")
public class OrderHandlowiec {

    @Id
    private int id;

    private Timestamp date;

    private int orderID;

    private String name;

    private double price;

    private String document;

    public OrderHandlowiec() {}

    public OrderHandlowiec(Timestamp date, int orderID, String name, double price, String document) {
        this.date = date;
        this.orderID = orderID;
        this.name = name;
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
