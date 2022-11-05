package com.filipmajewski.jeggerweb.controller;

import com.filipmajewski.jeggerweb.container.CompleteOrder;
import com.filipmajewski.jeggerweb.entity.*;
import com.filipmajewski.jeggerweb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class HomeController {

    private final OrderRepository orderRepository;

    private final OrderDealerRepository orderDealerRepository;

    private final OrderHandlowiecRepository orderHandlowiecRepository;

    private final DealerRepository dealerRepository;

    private final UserRepository userRepository;

    private final HistoryRepository historyRepository;

    private final EventRepository eventRepository;

    @Autowired
    public HomeController(OrderRepository orderRepository,
                          OrderDealerRepository orderDealerRepository,
                          OrderHandlowiecRepository orderHandlowiecRepository,
                          DealerRepository dealerRepository,
                          UserRepository userRepository,
                          HistoryRepository historyRepository,
                          EventRepository eventRepository) {

        this.orderRepository = orderRepository;
        this.orderDealerRepository = orderDealerRepository;
        this.orderHandlowiecRepository = orderHandlowiecRepository;
        this.dealerRepository = dealerRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.eventRepository = eventRepository;
    }

    @RequestMapping({"/", "/home"})
    public String homePage() {
        return "index.html";
    }

    @RequestMapping("/login")
    public String loginPage() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login.html";
        }

        return "redirect:/home";
    }

    @RequestMapping(value = "/rozliczenia", method = RequestMethod.GET)
    public ModelAndView clearancePage() {

        ModelAndView mav = new ModelAndView("orders_all.html");

        User user = getAuthenticatedUser();
        if(user == null) {
            System.out.println(new Timestamp(System.currentTimeMillis())
                                            + "  "
                                            + "\u001b[31;1m"
                                            + "ERROR "
                                            + "\u001B[0m"
                                            + "Authenticated user is null in request '/rozliczenia'");
            return mav;
        }

        if(user.getRole().equals("USER")) {

            List<Order> orderList = orderRepository.findAllByUserID(user.getId());
            List<CompleteOrder> completeOrderList = getUserCompleteOrderList(orderList, user);
            mav.addObject("completeOrderList", completeOrderList);

        } else if(user.getRole().equals("ADMIN")) {

            List<Order> orderList = orderRepository.findAll();
            List<CompleteOrder> completeOrderList = getAdminCompleteOrderList(orderList);
            mav.addObject("completeOrderList", completeOrderList);
        }

        return mav;
    }

    @RequestMapping(value = "/rozliczenia/open", method = RequestMethod.GET)
    public ModelAndView clearanceOpenPage() {

        ModelAndView mv = new ModelAndView("orders_open.html");

        User user = getAuthenticatedUser();
        if(user == null) {
            System.out.println(new Timestamp(System.currentTimeMillis())
                    + "  "
                    + "\u001b[31;1m"
                    + "ERROR "
                    + "\u001B[0m"
                    + "Authenticated user is null in request '/rozliczenia/open'");
            return mv;
        }

        if(user.getRole().equals("USER")) {

            List<Order> orderList = orderRepository.finaAllOpenOrderByUserID(user.getId());
            List<CompleteOrder> completeOrderList = getUserCompleteOrderList(orderList, user);
            mv.addObject("completeOrderList", completeOrderList);

        } else if(user.getRole().equals("ADMIN")) {

            List<Order> orderList = orderRepository.findAllOpenOrder();
            List<CompleteOrder> completeOrderList = getAdminCompleteOrderList(orderList);
            mv.addObject("completeOrderList", completeOrderList);
        }

        return mv;
    }

    @RequestMapping(value = "/rozliczenia/new", method = RequestMethod.GET)
    public ModelAndView clearanceNewPage() {

        ModelAndView mv = new ModelAndView("orders_new.html");

        List<Dealer> dealerList = dealerRepository.findAll();

        dealerList.sort(Comparator.comparing(Dealer::getCompany));

        mv.addObject("dealerList", dealerList);

        return mv;
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

    private List<CompleteOrder> getUserCompleteOrderList(List<Order> orderList, User user) {

        List<CompleteOrder> completeOrderList = new ArrayList<>();

        for(Order o : orderList) {

            int orderID = o.getId();

            completeOrderList.add(new CompleteOrder(
                    o,
                    orderDealerRepository.findByOrderID(orderID),
                    orderHandlowiecRepository.findByOrderID(orderID),
                    user
            ));
        }

        return completeOrderList;
    }

    private List<CompleteOrder> getAdminCompleteOrderList(List<Order> orderList) {

        List<CompleteOrder> completeOrderList = new ArrayList<>();

        for(Order o : orderList) {

            int orderID = o.getId();

            User orderUser = userRepository.findById(o.getUserID()).orElse(new User());
            completeOrderList.add(new CompleteOrder(
                    o,
                    orderDealerRepository.findByOrderID(orderID),
                    orderHandlowiecRepository.findByOrderID(orderID),
                    orderUser
            ));
        }

        return completeOrderList;
    }


    private User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {

            String currentUserName = authentication.getName();

            return userRepository.findByUsername(currentUserName);
        }

        return null;
    }

}
