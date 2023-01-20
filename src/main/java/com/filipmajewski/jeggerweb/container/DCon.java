package com.filipmajewski.jeggerweb.container;

public class DCon {

    private long data;

    private String nazwa;

    public DCon(long data, String nazwa) {
        this.data = data;
        this.nazwa = nazwa;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }
}
