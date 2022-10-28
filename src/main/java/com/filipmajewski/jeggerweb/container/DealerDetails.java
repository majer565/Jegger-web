package com.filipmajewski.jeggerweb.container;

import com.filipmajewski.jeggerweb.entity.Dealer;
import com.filipmajewski.jeggerweb.entity.DealerHandlowcy;

import java.util.List;

public class DealerDetails {

    private final Dealer dealer;

    private final List<DealerHandlowcy> handlowcyDealera;

    public DealerDetails(Dealer dealer, List<DealerHandlowcy> handlowcyDealera) {
        this.dealer = dealer;
        this.handlowcyDealera = handlowcyDealera;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public List<DealerHandlowcy> getHandlowcyDealera() {
        return handlowcyDealera;
    }
}
