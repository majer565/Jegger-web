package com.filipmajewski.jeggerweb.repository;

import com.filipmajewski.jeggerweb.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("FROM Order o WHERE o.dealerAcceptance=false OR o.handlowiecAcceptance=false")
    List<Order> findAllOpenOrder();

    List<Order> findAllByUserID(int id);

    @Query("FROM Order o WHERE (o.dealerAcceptance=false OR o.handlowiecAcceptance=false) AND o.userID=?1")
    List<Order> finaAllOpenOrderByUserID(int id);
}
