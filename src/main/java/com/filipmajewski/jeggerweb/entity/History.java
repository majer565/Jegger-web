package com.filipmajewski.jeggerweb.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "historia")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Timestamp date;

    private int orderID;

    private String description;

    public History() {}

    public History(int orderID, String description) {
        this.orderID = orderID;
        this.description = description;
        this.date = new Timestamp(System.currentTimeMillis());
    }

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

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
