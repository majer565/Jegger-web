package com.filipmajewski.jeggerweb.repository;

import com.filipmajewski.jeggerweb.entity.Order;
import com.filipmajewski.jeggerweb.entity.PaymentHandlowiec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentHandlowiecRepository extends JpaRepository<PaymentHandlowiec, Integer> {

    PaymentHandlowiec findByOrderID(int orderID);

    @Query("FROM PaymentHandlowiec p WHERE p.orderID=?1 AND (p.payment=?2 OR p.payment=?3)")
    PaymentHandlowiec findByOrderIDAndPayment(int id, int payment1, int payment2);

}
