package com.filipmajewski.jeggerweb.container;

import com.filipmajewski.jeggerweb.entity.Order;
import com.filipmajewski.jeggerweb.entity.OrderDealer;
import com.filipmajewski.jeggerweb.entity.OrderHandlowiec;
import com.filipmajewski.jeggerweb.entity.User;

public class CompleteOrder {

    private final Order order;

    private final OrderDealer orderDealer;

    private final OrderHandlowiec orderHandlowiec;

    private final User user;

    public CompleteOrder(Order order, OrderDealer orderDealer, OrderHandlowiec orderHandlowiec, User user) {
        this.order = order;
        this.orderDealer = orderDealer;
        this.orderHandlowiec = orderHandlowiec;
        this.user = user;
    }

    public Order getOrder() {
        return order;
    }

    public OrderDealer getOrderDealer() {
        return orderDealer;
    }

    public OrderHandlowiec getOrderHandlowiec() {
        return orderHandlowiec;
    }

    public User getUser() {
        return user;
    }
}
