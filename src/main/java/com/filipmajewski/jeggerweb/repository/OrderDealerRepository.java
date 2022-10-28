package com.filipmajewski.jeggerweb.repository;

import com.filipmajewski.jeggerweb.entity.OrderDealer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDealerRepository extends JpaRepository<OrderDealer, Integer> {

    OrderDealer findByOrderID(int orderID);

}
