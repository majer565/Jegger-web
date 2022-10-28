package com.filipmajewski.jeggerweb.controller;

import com.filipmajewski.jeggerweb.container.CompleteOrder;
import com.filipmajewski.jeggerweb.entity.*;
import com.filipmajewski.jeggerweb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDealerRepository orderDealerRepository;

    @Autowired
    private OrderHandlowiecRepository orderHandlowiecRepository;

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private EventRepository eventRepository;

    @RequestMapping({"/", "/home"})
    public String homePage() {
        return "home.html";
    }

    @RequestMapping("/login")
    public String loginPage() {
        return "index.html";
    }

    @RequestMapping(value = "/rozliczenia", method = RequestMethod.GET)
    public ModelAndView clearancePage() {
        ModelAndView mav = new ModelAndView("orders_all.html");

        List<Order> orderList = orderRepository.findAll();

        List<OrderDealer> orderDealerList = orderDealerRepository.findAll();

        List<OrderHandlowiec> orderHandlowiecList = orderHandlowiecRepository.findAll();

        List<CompleteOrder> completeOrderList = new ArrayList<>();

        for(int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            OrderDealer orderDealer = orderDealerList.get(i);
            OrderHandlowiec orderHandlowiec = orderHandlowiecList.get(i);
            User user = userRepository.findById(order.getUserID()).orElse(new User());

            completeOrderList.add(new CompleteOrder(order, orderDealer, orderHandlowiec, user));
        }

        mav.addObject("completeOrderList", completeOrderList);

        return mav;
    }

    @RequestMapping(value = "/rozliczenia/open", method = RequestMethod.GET)
    public ModelAndView clearanceOpenPage() {

        ModelAndView mv = new ModelAndView("orders_open.html");

        List<Order> orderList = orderRepository.findAllOpenOrder();

        List<CompleteOrder> completeOrderList = new ArrayList<>();

        for(Order o : orderList) {

            int orderID = o.getId();

            User user = userRepository.findById(o.getUserID()).orElse(new User());
            completeOrderList.add(new CompleteOrder(o,
                    orderDealerRepository.findByOrderID(orderID),
                    orderHandlowiecRepository.findByOrderID(orderID),
                    user));
        }

        mv.addObject("completeOrderList", completeOrderList);

        return mv;
    }

    @RequestMapping(value = "/rozliczenia/new", method = RequestMethod.GET)
    public String clearanceNewPage() {
        return "orders_new.html";
    }

    @RequestMapping(value = "/dealerzy", method = RequestMethod.GET)
    public ModelAndView dealersPage() {

        ModelAndView mv = new ModelAndView("dealers.html");

        List<Dealer> dealerList = dealerRepository.findAll();

        mv.addObject("dealerList", dealerList);

        return mv;
    }

    @RequestMapping(value = "/dealerzy/new", method = RequestMethod.GET)
    public String dealersNewPage() {
        return "dealers_new.html";
    }

    @RequestMapping(value = "/ustawienia", method = RequestMethod.GET)
    public String settingsPage() {
        return "settings.html";
    }

}
