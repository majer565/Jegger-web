package com.filipmajewski.jeggerweb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "dealers")
public class Dealer {

    @Id
    private int id;

    private Timestamp date;

    private String company;

    private String branch;

    private long nip;

    private long regon;

    private String street;

    private int postcode;

    private String city;

    public Dealer() {
    }

    public Dealer(String company, String branch, long nip, long regon, String street, int postcode, String city) {
        this.date = new Timestamp(System.currentTimeMillis());
        this.company = company;
        this.branch = branch;
        this.nip = nip;
        this.regon = regon;
        this.street = street;
        this.postcode = postcode;
        this.city = city;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public long getNip() {
        return nip;
    }

    public void setNip(long nip) {
        this.nip = nip;
    }

    public long getRegon() {
        return regon;
    }

    public void setRegon(long regon) {
        this.regon = regon;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getPostcode() {
        return postcode;
    }

    public void setPostcode(int postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
