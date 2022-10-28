package com.filipmajewski.jeggerweb.controller;

import com.filipmajewski.jeggerweb.container.CompleteOrder;
import com.filipmajewski.jeggerweb.container.NewOrderDetails;
import com.filipmajewski.jeggerweb.entity.Order;
import com.filipmajewski.jeggerweb.entity.OrderDealer;
import com.filipmajewski.jeggerweb.entity.OrderHandlowiec;
import com.filipmajewski.jeggerweb.entity.User;
import com.filipmajewski.jeggerweb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Controller
public class OrdersController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDealerRepository orderDealerRepository;

    @Autowired
    private OrderHandlowiecRepository orderHandlowiecRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private EventRepository eventRepository;

    private Map<String, NewOrderDetails> orderContainer = new HashMap<>();

    @RequestMapping(value = "/rozliczenia/details", method = RequestMethod.GET)
    public ModelAndView orderDetails(@RequestParam String nr) {

        ModelAndView mv = new ModelAndView();

        try {
            Order order = orderRepository.findByOrderNumber(nr);

            OrderDealer orderDealer = orderDealerRepository.findByOrderID(order.getId());

            OrderHandlowiec orderHandlowiec = orderHandlowiecRepository.findByOrderID(order.getId());

            User user = userRepository.findById(order.getUserID()).orElse(new User());


            mv.addObject("completeOrder", new CompleteOrder(order, orderDealer, orderHandlowiec, user));
            mv.addObject("orderSubmitDate", refactorTimestamp(order.getDate()));
            mv.addObject("orderDealerDate", refactorTimestamp(orderDealer.getDate()));
            mv.addObject("orderHandlowiecDate", refactorTimestamp(orderHandlowiec.getDate()));
            mv.addObject("orderDealerAcceptanceDate", refactorTimestamp(order.getDealerAcceptanceDate()));
            mv.addObject("orderHandlowiecAcceptanceDate", refactorTimestamp(order.getHandlowiecAcceptanceDate()));
            mv.addObject("orderDealerAcceptance", refactorAcceptance(order.getDealerAcceptance()));
            mv.addObject("orderHandlowiecAcceptance", refactorAcceptance(order.getHandlowiecAcceptance()));

            mv.setViewName("orders_details.html");
        } catch(NullPointerException e) {
            mv.setViewName("redirect:/rozliczenia");
        }

        return mv;
    }

    @RequestMapping(value = "/rozliczenia/new/submit", method = RequestMethod.POST)
    public ModelAndView newOrderPageFirst(@RequestParam("nr_zlec") String nr_zlec,
                                          @RequestParam("nr_fakt") String nr_fakt,
                                          @RequestParam("kw_fakt") Double kw_fakt,
                                          @RequestParam("kw_pocz") Double kw_pocz,
                                          @RequestParam("rabat") Integer rabat,
                                          @RequestParam("kw_rabat") Double kw_rabat,
                                          @RequestParam("kw_rozl") Double kw_rozl,
                                          @RequestParam("dealer") String dealer) {

        ModelAndView mav = new ModelAndView("orders_new_next.html");

        //nr_zlec do ustalenia czy ma dalej istniec

        //https://www.codejava.net/frameworks/spring-boot/spring-boot-thymeleaf-form-handling-tutorial
        //https://stackoverflow.com/questions/51560702/how-to-set-up-spring-form-and-thymeleaf-to-not-changing-the-fields-of-object-add

        NewOrderDetails nod = new NewOrderDetails(nr_fakt, kw_fakt, kw_pocz, rabat, kw_rabat, kw_rozl, dealer);

        orderContainer.put(nr_fakt, nod);

        mav.addObject("newOrder", nod);

        System.out.println(orderContainer.values());
        System.out.println(nr_fakt);

        return mav;
    }

    @RequestMapping(value = "/rozliczenie/new/add", method = RequestMethod.POST)
    public String newOrderPageSecondAdd(@RequestParam("hn_dealer") String hn_dealer,
                                        @RequestParam("kw_dealer") Double kw_dealer,
                                        @RequestParam("kw_handl") Double kw_handl,
                                        @RequestParam("nr_fakt") String nr_fakt) {

        NewOrderDetails nod = orderContainer.get(nr_fakt);

        System.out.println(nr_fakt);
        System.out.println(nod.getKwfakt());
        System.out.println(nod.getKwpocz());
        System.out.println(nod.getKwrabat());
        System.out.println(nod.getKwrabat());
        System.out.println(nod.getKwrozl());
        System.out.println(nod.getDealer());
        System.out.println(hn_dealer);
        System.out.println(kw_dealer);
        System.out.println(kw_handl);

        System.out.println(orderContainer.values());
        orderContainer.remove(nr_fakt);

        return "redirect:/rozliczenia/new";
    }

    @RequestMapping(value = "/rozliczenie/new/send", method = RequestMethod.POST)
    public String newOrderPageSecondSend(@RequestParam("hn_dealer") String hn_dealer,
                                         @RequestParam("kw_dealer") Double kw_dealer,
                                         @RequestParam("kw_handl") Double kw_handl) {

        System.out.println(orderContainer.get("nr_zlec"));
        System.out.println(orderContainer.get("nr_fakt"));
        System.out.println(orderContainer.get("kw_fakt"));
        System.out.println(orderContainer.get("kw_pocz"));
        System.out.println(orderContainer.get("rabat"));
        System.out.println(orderContainer.get("kw_rabat"));
        System.out.println(orderContainer.get("kw_rozl"));
        System.out.println(orderContainer.get("dealer"));
        System.out.println(hn_dealer);
        System.out.println(kw_dealer);
        System.out.println(kw_handl);

        return "redirect:/rozliczenia/new";
    }

    @RequestMapping(value = "/rozliczenia/modify", method = RequestMethod.GET)
    public ModelAndView modifyOrder(@RequestParam String nr) {

        ModelAndView mv = new ModelAndView();

        try {
            Order order = orderRepository.findByOrderNumber(nr);

            OrderDealer orderDealer = orderDealerRepository.findByOrderID(order.getId());

            OrderHandlowiec orderHandlowiec = orderHandlowiecRepository.findByOrderID(order.getId());

            User user = userRepository.findById(order.getUserID()).orElse(new User());


            mv.addObject("completeOrder", new CompleteOrder(order, orderDealer, orderHandlowiec, user));

            mv.setViewName("orders_modify.html");
            //add successful message
        } catch(NullPointerException e) {
            mv.setViewName("redirect:/rozliczenia/open");
            //add error message
        }

        return mv;
    }

    @RequestMapping(value = "/rozliczenia/modify/submit", method = RequestMethod.POST)
    public ModelAndView submitModifyOrder(@RequestParam("nr_zlec") String nr_zlec,
                                          @RequestParam("nr_fakt") String nr_fakt,
                                          @RequestParam("kw_fakt") Double kw_fakt,
                                          @RequestParam("dealer") String dealer,
                                          @RequestParam("handl_dealer") String handl_dealer,
                                          @RequestParam("kw_pocz") Double kw_pocz,
                                          @RequestParam("rabat") Integer rabat,
                                          @RequestParam("kw_rabat") Double kw_rabat,
                                          @RequestParam("kw_rozl") Double kw_rozl) {

        ModelAndView mv = new ModelAndView("redirect:/rozliczenia/open");

        try{
            System.out.println("OK");
        } catch(Exception e) {
            System.out.println("NIE OK");
        }
        return mv;
    }

    private String refactorTimestamp(Timestamp timestamp) {

        if(timestamp == null) {
            return "---";
        }

        return new SimpleDateFormat("MM/dd/yyyy HH:mm").format(timestamp);
    }

    private String refactorAcceptance(boolean text) {
        if(text) return "TAK";
        else return "NIE";
    }
}
