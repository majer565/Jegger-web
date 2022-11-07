package com.filipmajewski.jeggerweb.controller;

import com.filipmajewski.jeggerweb.container.CompleteOrder;
import com.filipmajewski.jeggerweb.container.NewOrderDetails;
import com.filipmajewski.jeggerweb.entity.*;
import com.filipmajewski.jeggerweb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

@Controller
public class OrdersController {

    private final OrderRepository orderRepository;

    private final DealerRepository dealerRepository;

    private final OrderDealerRepository orderDealerRepository;

    private final OrderHandlowiecRepository orderHandlowiecRepository;

    private final DealerHandlowcyRepository dealerHandlowcyRepository;

    private final UserRepository userRepository;

    private final HistoryRepository historyRepository;

    private final EventRepository eventRepository;

    @Autowired
    public OrdersController(OrderRepository orderRepository,
                            DealerRepository dealerRepository,
                            OrderDealerRepository orderDealerRepository,
                            OrderHandlowiecRepository orderHandlowiecRepository,
                            DealerHandlowcyRepository dealerHandlowcyRepository,
                            UserRepository userRepository,
                            HistoryRepository historyRepository,
                            EventRepository eventRepository) {

        this.orderRepository = orderRepository;
        this.dealerRepository = dealerRepository;
        this.orderDealerRepository = orderDealerRepository;
        this.orderHandlowiecRepository = orderHandlowiecRepository;
        this.dealerHandlowcyRepository = dealerHandlowcyRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.eventRepository = eventRepository;
    }

    @RequestMapping(value = "/rozliczenia/details", method = RequestMethod.GET)
    public ModelAndView orderDetails(@RequestParam int nr) {

        ModelAndView mv = new ModelAndView();

        try {
            Order order = orderRepository.findById(nr).orElse(null);

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
                                          @RequestParam("dealer") int dealer,
                                          HttpSession session) {

        ModelAndView mav = new ModelAndView("orders_new_next.html");

        //nr_zlec do ustalenia czy ma dalej istniec

        //https://www.codejava.net/frameworks/spring-boot/spring-boot-thymeleaf-form-handling-tutorial
        //https://stackoverflow.com/questions/51560702/how-to-set-up-spring-form-and-thymeleaf-to-not-changing-the-fields-of-object-add

        NewOrderDetails nod = new NewOrderDetails(nr_zlec, nr_fakt, kw_fakt, kw_pocz, rabat, kw_rabat, kw_rozl, dealer);

        session.setAttribute("newOrderDetails", nod);

        List<DealerHandlowcy> dealerHandlowcyList = dealerHandlowcyRepository.findAllByDealerID(dealer);
        dealerHandlowcyList.sort(Comparator.comparing(DealerHandlowcy::getHandlowiec));

        mav.addObject("newOrder", nod);
        mav.addObject("handlowcy", dealerHandlowcyList);

        return mav;
    }

    @RequestMapping(value = "/rozliczenie/new/add", method = RequestMethod.POST)
    public ModelAndView newOrderPageSecondAdd(@RequestParam("hn_dealer") int hn_dealer,
                                        @RequestParam("kw_dealer") Double kw_dealer,
                                        @RequestParam("kw_handl") Double kw_handl,
                                        @RequestParam("nr_fakt") String nr_fakt,
                                        HttpSession session) {

        ModelAndView mv = new ModelAndView("redirect:/rozliczenia");

        NewOrderDetails nod = (NewOrderDetails) session.getAttribute("newOrderDetails");

        User user = getAuthenticatedUser();

        Dealer dealer = dealerRepository.findById(nod.getDealer()).orElse(null);

        DealerHandlowcy handlowiec = dealerHandlowcyRepository.findById(hn_dealer).orElse(null);

        if(dealer == null || user == null || handlowiec == null) {

            mv.addObject("newOrderError", "Błąd przy dodawaniu rozliczenia.");
            return mv;
        }


        Order newOrder = new Order(
                nod.getNrzlec(),
                nr_fakt,
                nod.getKwfakt(),
                nod.getKwpocz(),
                nod.getRabat(),
                nod.getKwrabat(),
                nod.getKwrozl(),
                user.getId());

        orderRepository.save(newOrder);

        OrderDealer newOrderDealer = new OrderDealer(
                newOrder.getDate(),
                newOrder.getId(),
                dealer.getCompany(),
                dealer.getNip(),
                kw_dealer,
                "asd");

        OrderHandlowiec newOrderHandlowiec = new OrderHandlowiec(
                newOrder.getDate(),
                newOrder.getId(),
                handlowiec.getHandlowiec(),
                kw_handl,
                "asd");

        orderDealerRepository.save(newOrderDealer);
        orderHandlowiecRepository.save(newOrderHandlowiec);
        mv.addObject("newOrderSuccess", "Pomyślnie dodano nowe rozliczenie");

        return mv;
    }

    @RequestMapping(value = "/rozliczenie/new/send", method = RequestMethod.POST)
    public String newOrderPageSecondSend(@RequestParam("hn_dealer") String hn_dealer,
                                         @RequestParam("kw_dealer") Double kw_dealer,
                                         @RequestParam("kw_handl") Double kw_handl) {

        System.out.println(hn_dealer);
        System.out.println(kw_dealer);
        System.out.println(kw_handl);

        return "redirect:/rozliczenia/new";
    }

    @RequestMapping(value = "/rozliczenia/modify", method = RequestMethod.GET)
    public ModelAndView modifyOrder(@RequestParam int nr) {

        ModelAndView mv = new ModelAndView();

        try {
            Order order = orderRepository.findById(nr).orElse(null);

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

    private User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {

            String currentUserName = authentication.getName();

            return userRepository.findByUsername(currentUserName);
        }

        return null;
    }
}
