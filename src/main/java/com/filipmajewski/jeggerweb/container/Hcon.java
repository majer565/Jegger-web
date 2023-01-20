package com.filipmajewski.jeggerweb.container;

public class Hcon {

    private int dealerID;

    private String nazwa;

    public Hcon(int dealerID, String nazwa) {
        this.dealerID = dealerID;
        this.nazwa = nazwa;
    }

    public int getDealerID() {
        return dealerID;
    }

    public void setDealerID(int dealerID) {
        this.dealerID = dealerID;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }
}
