package com.filipmajewski.jeggerweb.repository;

import com.filipmajewski.jeggerweb.entity.PaymentDealer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentDealerRepository extends JpaRepository<PaymentDealer, Integer> {

    PaymentDealer findByOrderID(int orderID);

}
