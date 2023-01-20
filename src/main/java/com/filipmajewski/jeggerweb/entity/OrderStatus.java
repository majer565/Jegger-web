package com.filipmajewski.jeggerweb.entity;

import javax.persistence.*;

@Entity
@Table(name = "rozliczenie_status")
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int orderID;

    private int status;

    public OrderStatus() {}

    public OrderStatus(int orderID, int status) {
        this.orderID = orderID;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
