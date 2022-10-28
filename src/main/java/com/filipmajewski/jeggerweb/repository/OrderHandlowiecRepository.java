package com.filipmajewski.jeggerweb.repository;

import com.filipmajewski.jeggerweb.entity.OrderHandlowiec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderHandlowiecRepository extends JpaRepository<OrderHandlowiec, Integer> {

    OrderHandlowiec findByOrderID(int orderID);

}
