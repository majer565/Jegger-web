package com.filipmajewski.jeggerweb.entity;

import javax.persistence.*;

@Entity
@Table(name = "dealers_handlowcy")
public class DealerHandlowcy {

    @Id
    @Column(name = "handlowiecID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int dealerID;

    private String handlowiec;

    public DealerHandlowcy() {
    }

    public DealerHandlowcy(int dealerID, String handlowiec) {
        this.dealerID = dealerID;
        this.handlowiec = handlowiec;
    }

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
