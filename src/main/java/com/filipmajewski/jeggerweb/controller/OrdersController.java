package com.filipmajewski.jeggerweb.controller;

import com.filipmajewski.jeggerweb.container.CompleteOrder;
import com.filipmajewski.jeggerweb.container.NewOrderDetails;
import com.filipmajewski.jeggerweb.entity.*;
import com.filipmajewski.jeggerweb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        User user = getAuthenticatedUser();

        try {

            Order order = orderRepository.findById(nr).orElse(null);

            if(user != null && order.getUserID() == user.getId() || user.getRole().equals("ADMIN")) {

                OrderDealer orderDealer = orderDealerRepository.findByOrderID(order.getId());

                OrderHandlowiec orderHandlowiec = orderHandlowiecRepository.findByOrderID(order.getId());

                User orderUser = userRepository.findById(order.getUserID()).orElse(new User());


                mv.addObject("completeOrder", new CompleteOrder(order, orderDealer, orderHandlowiec, orderUser));
                mv.addObject("orderSubmitDate", refactorTimestamp(order.getDate()));
                mv.addObject("orderDealerDate", refactorTimestamp(orderDealer.getDate()));
                mv.addObject("orderHandlowiecDate", refactorTimestamp(orderHandlowiec.getDate()));
                mv.addObject("orderDealerAcceptanceDate", refactorTimestamp(order.getDealerAcceptanceDate()));
                mv.addObject("orderHandlowiecAcceptanceDate", refactorTimestamp(order.getHandlowiecAcceptanceDate()));
                mv.addObject("orderDealerAcceptance", refactorAcceptance(order.getDealerAcceptance()));
                mv.addObject("orderHandlowiecAcceptance", refactorAcceptance(order.getHandlowiecAcceptance()));

                mv.setViewName("orders_details.html");

            } else {
                mv.setViewName("redirect:/rozliczenia");
            }

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
                                          RedirectAttributes rdir,
                                          HttpSession session) {

        ModelAndView mav = new ModelAndView();

        if(orderRepository.findByOldOrderNumber(nr_zlec) != null) {
            rdir.addFlashAttribute("newOrderError", "Błąd przy dodawaniu rozliczenia. Rozliczenie o takim starym numerze zlecenia już istnieje");
            mav.setViewName("redirect:/rozliczenia");
            return mav;
        } else if(orderRepository.findByInvoiceNumber(nr_fakt) != null) {
            rdir.addFlashAttribute("newOrderError", "Błąd przy dodawaniu rozliczenia. Rozliczenie o takim numerze faktury już istnieje");
            mav.setViewName("redirect:/rozliczenia");
            return mav;
        }
        else mav.setViewName("orders_new_next.html");

        NewOrderDetails nod = new NewOrderDetails(nr_zlec, nr_fakt, kw_fakt, kw_pocz, rabat, kw_rabat, kw_rozl, dealer);

        session.setAttribute("newOrderDetails", nod);

        List<DealerHandlowcy> dealerHandlowcyList = dealerHandlowcyRepository.findAllByDealerID(dealer);
        dealerHandlowcyList.sort(Comparator.comparing(DealerHandlowcy::getHandlowiec, String.CASE_INSENSITIVE_ORDER));

        mav.addObject("newOrder", nod);
        mav.addObject("handlowcy", dealerHandlowcyList);

        return mav;
    }

    @RequestMapping(value = "/rozliczenie/new/add", method = RequestMethod.POST)
    public ModelAndView newOrderPageSecondAdd(@RequestParam("hn_dealer") int hn_dealer,
                                        @RequestParam("kw_dealer") Double kw_dealer,
                                        @RequestParam("kw_handl") Double kw_handl,
                                        @RequestParam("nr_fakt") String nr_fakt,
                                        @RequestParam("doc_dealer") String doc_dealer,
                                        @RequestParam("doc_handl") String doc_handl,
                                        RedirectAttributes rdir,
                                        HttpSession session) {

        ModelAndView mv = new ModelAndView("redirect:/rozliczenia");

        NewOrderDetails nod = (NewOrderDetails) session.getAttribute("newOrderDetails");

        User user = getAuthenticatedUser();

        Dealer dealer = dealerRepository.findById(nod.getDealer()).orElse(null);

        if(dealer == null || user == null) {
            rdir.addFlashAttribute("newOrderError", "Błąd przy dodawaniu rozliczenia.");
            return mv;
        } else if(hn_dealer != -1 && kw_dealer+kw_handl != nod.getKwrozl()) {
            rdir.addFlashAttribute("newOrderError", "Błąd przy dodawaniu rozliczenia. Suma kwoty dealera i kwoty handlowca powinna być równa kwocie rozliczenia");
        }

        if(hn_dealer == -1) {

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
                    newOrder.getFinalPrice(),
                    doc_dealer);

            OrderHandlowiec newOrderHandlowiec = new OrderHandlowiec(
                    newOrder.getDate(),
                    newOrder.getId(),
                    "---",
                    0,
                    "---");


            orderDealerRepository.save(newOrderDealer);
            orderHandlowiecRepository.save(newOrderHandlowiec);
            rdir.addFlashAttribute("newOrderSuccess", "Pomyślnie dodano nowe rozliczenie");
        } else {

            DealerHandlowcy handlowiec = dealerHandlowcyRepository.findById(hn_dealer).orElse(null);
            if(handlowiec == null) {
                rdir.addFlashAttribute("newOrderError", "Błąd przy dodawaniu rozliczenia.");
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
                    doc_dealer);

            OrderHandlowiec newOrderHandlowiec = new OrderHandlowiec(
                    newOrder.getDate(),
                    newOrder.getId(),
                    handlowiec.getHandlowiec(),
                    kw_handl,
                    doc_handl);

            orderDealerRepository.save(newOrderDealer);
            orderHandlowiecRepository.save(newOrderHandlowiec);
            rdir.addFlashAttribute("newOrderSuccess", "Pomyślnie dodano nowe rozliczenie");
        }

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
    public ModelAndView modifyOrder(@RequestParam int nr, RedirectAttributes rdir) {

        ModelAndView mv = new ModelAndView();

        User user = getAuthenticatedUser();

        try {
            Order order = orderRepository.findById(nr).orElse(null);

            if(user != null && order.getUserID() == user.getId() || user.getRole().equals("ADMIN")) {

                OrderDealer orderDealer = orderDealerRepository.findByOrderID(order.getId());

                OrderHandlowiec orderHandlowiec = orderHandlowiecRepository.findByOrderID(order.getId());

                User orderUser = userRepository.findById(order.getUserID()).orElse(new User());

                mv.addObject("completeOrder", new CompleteOrder(order, orderDealer, orderHandlowiec, orderUser));

                Dealer dealer = dealerRepository.findByCompany(orderDealer.getName());
                List<DealerHandlowcy> dealerHandlowcyList = dealerHandlowcyRepository.findAllByDealerID(dealer.getId());
                dealerHandlowcyList.sort(Comparator.comparing(DealerHandlowcy::getHandlowiec, String.CASE_INSENSITIVE_ORDER));
                mv.addObject("dealerHList", dealerHandlowcyList);


                mv.setViewName("orders_modify.html");

            } else {
                rdir.addFlashAttribute("newOrderError", "Błąd. Nie można zmodyfikować rozliczenia.");
            }
        } catch(NullPointerException e) {
            mv.setViewName("redirect:/rozliczenia/open");
            rdir.addFlashAttribute("newOrderError", "Błąd. Nie można zmodyfikować rozliczenia.");
        }

        return mv;
    }

    @RequestMapping(value = "/rozliczenia/modify/submit", method = RequestMethod.POST)
    public ModelAndView submitModifyOrder(@RequestParam("nr_zlec") Integer nr_zlec,
                                          @RequestParam("nr_fakt") String nr_fakt,
                                          @RequestParam("kw_fakt") Double kw_fakt,
                                          @RequestParam("handl_dealer") Integer handl_dealer,
                                          @RequestParam("kw_pocz") Double kw_pocz,
                                          @RequestParam("rabat") Integer rabat,
                                          @RequestParam(value = "kw_rabat", required = false) Double kw_rabat,
                                          @RequestParam(value = "kw_rozl", required = false) Double kw_rozl,
                                          RedirectAttributes rdir,
                                          @RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer) {

        ModelAndView mv = new ModelAndView();

        if(kw_rabat == null) {
            rdir.addFlashAttribute("modifyError", "Błąd. Wprowadź rabat.");
            mv.setViewName("redirect:" + referrer);
        } else {

            Order order = orderRepository.findById(nr_zlec).orElse(null);
            OrderHandlowiec orderHandlowiec = orderHandlowiecRepository.findByOrderID(order.getId());
            DealerHandlowcy handlowiec = dealerHandlowcyRepository.findById(handl_dealer).orElse(null);

            if(!order.getInvoiceNumber().equals(nr_fakt)) order.setInvoiceNumber(nr_fakt);
            if(!(order.getInvoicePrice() == kw_fakt)) order.setInvoicePrice(kw_fakt);
            if(!(order.getOriginalPrice() == kw_pocz)) order.setOriginalPrice(kw_pocz);
            if(!(order.getDiscount() == rabat)) order.setDiscount(rabat);
            if(!(order.getDiscountPrice() == kw_rabat)) order.setDiscountPrice(kw_rabat);
            if(!(order.getFinalPrice() == kw_rozl)) order.setFinalPrice(kw_rozl);

            if(!orderHandlowiec.getName().equals(handlowiec.getHandlowiec())) {
                orderHandlowiec.setName(handlowiec.getHandlowiec());
                orderHandlowiecRepository.save(orderHandlowiec);
            }

            orderRepository.save(order);
            mv.setViewName("redirect:/rozliczenia/open");
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
