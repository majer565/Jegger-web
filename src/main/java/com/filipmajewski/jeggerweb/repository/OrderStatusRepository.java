package com.filipmajewski.jeggerweb.repository;

import com.filipmajewski.jeggerweb.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {

    OrderStatus findByOrderID(int orderID);

    @Query("FROM OrderStatus o WHERE o.status=?1")
    List<OrderStatus> findAllByStatus(int status);

    @Query("FROM OrderStatus o WHERE o.status=?1 OR o.status=?2")
    List<OrderStatus> findAllByStatus(int status1, int status2);

}
