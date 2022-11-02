package com.filipmajewski.jeggerweb.container;

public class NewOrderDetails {

    private final String nr_zlec;
    private final String nr_fakt;

    private final double kw_fakt;

    private final double kw_pocz;

    private final int rabat;

    private final double kw_rabat;

    private final double kw_rozl;

    private final int dealer;

    public NewOrderDetails(String nr_zlec, String nr_fakt, double kw_fakt, double kw_pocz, int rabat, double kw_rabat, double kw_rozl, int dealer) {
        this.nr_zlec = nr_zlec;
        this.nr_fakt = nr_fakt;
        this.kw_fakt = kw_fakt;
        this.kw_pocz = kw_pocz;
        this.rabat = rabat;
        this.kw_rabat = kw_rabat;
        this.kw_rozl = kw_rozl;
        this.dealer = dealer;
    }

    public String getNr_fakt() {
        return nr_fakt;
    }

    public double getKwfakt() {
        return kw_fakt;
    }

    public double getKwpocz() {
        return kw_pocz;
    }

    public int getRabat() {
        return rabat;
    }

    public double getKwrabat() {
        return kw_rabat;
    }

    public double getKwrozl() {
        return kw_rozl;
    }

    public int getDealer() {
        return dealer;
    }

    public String getNrzlec() {
        return nr_zlec;
    }
}
