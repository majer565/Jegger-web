package com.filipmajewski.jeggerweb.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userID;

    private Timestamp login;

    private Timestamp logout;

    public Session(){}

    public Session(int userID, Timestamp login, Timestamp logout) {
        this.userID = userID;
        this.login = login;
        this.logout = logout;
    }

    public int getId() {
        return id;
    }

    public int getUserID() {
        return userID;
    }

    public Timestamp getLogin() {
        return login;
    }

    public Timestamp getLogout() {
        return logout;
    }
}
