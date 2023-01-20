package com.filipmajewski.jeggerweb.container;

import com.filipmajewski.jeggerweb.entity.*;

public class CompleteOrder {

    private final Order order;

    private final OrderStatus orderStatus;

    private final OrderDealer orderDealer;

    private final OrderHandlowiec orderHandlowiec;

    private final User user;

    public CompleteOrder(Order order, OrderDealer orderDealer, OrderHandlowiec orderHandlowiec, User user) {
        this.order = order;
        this.orderStatus = null;
        this.orderDealer = orderDealer;
        this.orderHandlowiec = orderHandlowiec;
        this.user = user;
    }

    public CompleteOrder(Order order, OrderStatus orderStatus, OrderDealer orderDealer, OrderHandlowiec orderHandlowiec, User user) {
        this.order = order;
        this.orderStatus = orderStatus;
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

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
}
