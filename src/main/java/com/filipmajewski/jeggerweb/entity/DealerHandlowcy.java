package com.filipmajewski.jeggerweb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dealers_handlowcy")
public class DealerHandlowcy {

    @Id
    @Column(name = "handlowiecID")
    private int id;

    private int dealerID;

    private String handlowiec;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDealerID() {
        return dealerID;
    }

    public void setDealerID(int dealerID) {
        this.dealerID = dealerID;
    }

    public String getHandlowiec() {
        return handlowiec;
    }

    public void setHandlowiec(String handlowiec) {
        this.handlowiec = handlowiec;
    }
}
