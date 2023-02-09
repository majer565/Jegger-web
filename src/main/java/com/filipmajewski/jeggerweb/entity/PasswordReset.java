package com.filipmajewski.jeggerweb.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "password_reset")
public class PasswordReset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Timestamp date;

    @Column(name = "userID")
    private int userID;

    @Column(name = "code")
    private int verifyCode;
    @Column(name = "expiration_date")
    private Long expirationDate;

    public PasswordReset(){}
    public PasswordReset(int userID, int verifyCode, Long expirationDate) {
        this.userID = userID;
        this.verifyCode = verifyCode;
        this.expirationDate = expirationDate;
    }

    public int getId() {
        return id;
    }

    public Timestamp getDate() {
        return date;
    }

    public int getUserID() {
        return userID;
    }

    public int getVerifyCode() {
        return verifyCode;
    }

    public Long getExpirationDate() {
        return expirationDate;
    }
}
