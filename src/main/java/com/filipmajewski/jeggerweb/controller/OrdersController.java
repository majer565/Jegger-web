package com.filipmajewski.jeggerweb.controller;

import com.filipmajewski.jeggerweb.container.CompleteOrder;
import com.filipmajewski.jeggerweb.container.EmailMessages;
import com.filipmajewski.jeggerweb.container.NewOrderDetails;
import com.filipmajewski.jeggerweb.entity.*;
import com.filipmajewski.jeggerweb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

    /**
     * Order status: <br/>
     * - 0 - open <br/>
     * - 1 - accepted <br/>
     * - 2 - pending <br/>
     * - 3 - accepted, open payment <br/>
     * - 4 - accepted, pending payment <br/>
     * - 5 - accepted, accepted payment, waiting for completion
     */
    private final OrderStatusRepository orderStatusRepository;

    private final DealerRepository dealerRepository;

    private final OrderDealerRepository orderDealerRepository;

    private final OrderHandlowiecRepository orderHandlowiecRepository;

    private final DealerHandlowcyRepository dealerHandlowcyRepository;

    private final UserRepository userRepository;

    private final HistoryRepository historyRepository;

    private final PaymentDealerRepository paymentDealerRepository;

    private final PaymentHandlowiecRepository paymentHandlowiecRepository;

    private final EventRepository eventRepository;

    @Autowired
    public OrdersController(OrderRepository orderRepository,
                            OrderStatusRepository orderStatusRepository,
                            DealerRepository dealerRepository,
                            OrderDealerRepository orderDealerRepository,
                            OrderHandlowiecRepository orderHandlowiecRepository,
                            DealerHandlowcyRepository dealerHandlowcyRepository,
                            UserRepository userRepository,
                            HistoryRepository historyRepository,
                            PaymentDealerRepository paymentDealerRepository,
                            PaymentHandlowiecRepository paymentHandlowiecRepository,
                            EventRepository eventRepository) {

        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.dealerRepository = dealerRepository;
        this.orderDealerRepository = orderDealerRepository;
        this.orderHandlowiecRepository = orderHandlowiecRepository;
        this.dealerHandlowcyRepository = dealerHandlowcyRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.paymentDealerRepository = paymentDealerRepository;
        this.paymentHandlowiecRepository = paymentHandlowiecRepository;
        this.eventRepository = eventRepository;
    }

    @RequestMapping(value = "/rozliczenia/details", method = RequestMethod.GET)
    public ModelAndView orderDetails(@RequestParam int nr, RedirectAttributes rdir) {

        ModelAndView mv = new ModelAndView();

        User user = getAuthenticatedUser();

        try {

            Order order = orderRepository.findById(nr).orElse(null);
            int orderID = order.getId();

            if(user != null && order.getUserID() == user.getId() || user.getRole().equals("ADMIN")) {

                OrderDealer orderDealer = orderDealerRepository.findByOrderID(orderID);

                OrderHandlowiec orderHandlowiec = orderHandlowiecRepository.findByOrderID(orderID);

                User orderUser = userRepository.findById(order.getUserID()).orElse(new User());

                PaymentDealer paymentDealer = paymentDealerRepository.findByOrderID(orderID);

                PaymentHandlowiec paymentHandlowiec = paymentHandlowiecRepository.findByOrderID(orderID);

                List<History> historyList = historyRepository.findAllByOrderID(orderID);

                mv.addObject("completeOrder", new CompleteOrder(order, orderDealer, orderHandlowiec, orderUser));
                mv.addObject("historyList", historyList);
                mv.addObject("orderSubmitDate", refactorTimestamp(order.getDate()));
                mv.addObject("orderDealerDate", refactorTimestamp(orderDealer.getDate()));
                mv.addObject("orderHandlowiecDate", refactorTimestamp(orderHandlowiec.getDate()));
                mv.addObject("orderDealerAcceptanceDate", refactorTimestamp(order.getDealerAcceptanceDate()));
                mv.addObject("orderHandlowiecAcceptanceDate", refactorTimestamp(order.getHandlowiecAcceptanceDate()));
                mv.addObject("orderDealerAcceptance", refactorAcceptance(order.getDealerAcceptance()));
                mv.addObject("orderHandlowiecAcceptance", refactorAcceptance(order.getHandlowiecAcceptance()));
                mv.addObject("orderDealerPaymentDate", refactorTimestamp(paymentDealer.getPaymentDate()));
                mv.addObject("orderHandlowiecPaymentDate", refactorTimestamp(paymentHandlowiec.getPaymentDate()));
                mv.addObject("orderDealerPayment", refactorPayment(paymentDealer.getPaymentDate()));
                mv.addObject("orderHandlowiecPayment", refactorPayment(paymentHandlowiec.getPaymentDate()));
                mv.addObject("orderDealerPaymentDocument", paymentDealer.getDocument());
                mv.addObject("orderHandlowiecPaymentDocument", paymentHandlowiec.getDocument());
                mv.addObject("orderDealerPaymentType", refactorPaymentType(paymentDealer.getPayment()));
                mv.addObject("orderHandlowiecPaymentType", refactorPaymentType(paymentHandlowiec.getPayment()));

                mv.setViewName("orders_details.html");

            } else {
                rdir.addFlashAttribute("newOrderError", "Nie znaleziono takiego rozliczenia.");
                mv.setViewName("redirect:/rozliczenia");
            }

        } catch(NullPointerException e) {
            e.printStackTrace();
            rdir.addFlashAttribute("newOrderError", "Nie znaleziono takiego rozliczenia.");
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

        String rabatAtr = rabat + "%";
        String companyAtr = dealerRepository.findById(dealer).orElse(null).getCompany();

        System.out.println(rabatAtr);
        System.out.println(companyAtr);

        mav.addObject("newOrder", nod);
        mav.addObject("newOrder_rabat", rabatAtr);
        mav.addObject("newOrder_dealer", companyAtr);
        mav.addObject("handlowcy", dealerHandlowcyList);

        return mav;
    }

    @RequestMapping(value = "/rozliczenie/new/add", method = RequestMethod.POST)
    public ModelAndView newOrderPageSecondAdd(@RequestParam("hn_dealer") int hn_dealer,
                                        @RequestParam("kw_dealer") Integer kw_dealer,
                                        @RequestParam(value = "kw_handl", required = false) Integer kw_handl,
                                        @RequestParam("nr_fakt") String nr_fakt,
                                        @RequestParam("doc_dealer") int doc_dealer,
                                        @RequestParam("doc_handl") int doc_handl,
                                        @RequestParam(value = "pay_dealer_doc", required = false) String pay_doc_dealer,
                                        @RequestParam(value = "pay_handl_doc", required = false) String pay_doc_handl,
                                        RedirectAttributes rdir,
                                        HttpSession session) {

        ModelAndView mv = new ModelAndView("redirect:/rozliczenia");

        NewOrderDetails nod = (NewOrderDetails) session.getAttribute("newOrderDetails");

        User user = getAuthenticatedUser();

        Dealer dealer = dealerRepository.findById(nod.getDealer()).orElse(null);

        if(dealer == null || user == null) {
            rdir.addFlashAttribute("newOrderError", "Błąd przy dodawaniu rozliczenia.");
            return mv;
        } else if(hn_dealer != -1 && kw_dealer+kw_handl != nod.getKw_rozl()) {
            rdir.addFlashAttribute("newOrderError", "Błąd przy dodawaniu rozliczenia. Suma kwoty dealera i kwoty handlowca powinna być równa kwocie rozliczenia");
            return mv;
        }

        if(pay_doc_dealer == null) pay_doc_dealer = "Brak";
        else if(pay_doc_handl == null) pay_doc_handl = "Brak";
        if(kw_handl == null) kw_handl = 0;

        if(hn_dealer == -1) {

            Order newOrder = new Order(
                    nod.getNr_zlec(),
                    nr_fakt,
                    nod.getKw_fakt(),
                    nod.getKw_pocz(),
                    nod.getRabat(),
                    nod.getKw_rabat(),
                    nod.getKw_rozl(),
                    user.getId());

            orderRepository.save(newOrder);

            OrderDealer newOrderDealer = new OrderDealer(
                    newOrder.getDate(),
                    newOrder.getId(),
                    dealer.getCompany(),
                    dealer.getNip(),
                    newOrder.getFinalPrice(),
                    doc_dealer
            );

            OrderHandlowiec newOrderHandlowiec = new OrderHandlowiec(
                    newOrder.getDate(),
                    newOrder.getId(),
                    "---",
                    0,
                    -1
            );

            PaymentDealer paymentDealer = new PaymentDealer(
                    newOrder.getId(),
                    dealerRepository.findByCompany(newOrderDealer.getName()).getId(),
                    doc_dealer,
                    pay_doc_dealer,
                    (int)newOrder.getFinalPrice(),
                    null
            );

            PaymentHandlowiec paymentHandlowiec = new PaymentHandlowiec(
                    newOrder.getId(),
                    -1,
                    -1,
                    "---",
                    0,
                    null
            );

            OrderStatus newOrderStatus = new OrderStatus(newOrder.getId(), 0);

            orderDealerRepository.save(newOrderDealer);
            orderHandlowiecRepository.save(newOrderHandlowiec);
            orderStatusRepository.save(newOrderStatus);
            paymentDealerRepository.save(paymentDealer);
            paymentHandlowiecRepository.save(paymentHandlowiec);
            rdir.addFlashAttribute("newOrderSuccess", "Pomyślnie dodano nowe rozliczenie");
        } else {

            DealerHandlowcy handlowiec = dealerHandlowcyRepository.findById(hn_dealer).orElse(null);
            if(handlowiec == null) {
                rdir.addFlashAttribute("newOrderError", "Błąd przy dodawaniu rozliczenia.");
                return mv;
            }

            Order newOrder = new Order(
                    nod.getNr_zlec(),
                    nr_fakt,
                    nod.getKw_fakt(),
                    nod.getKw_pocz(),
                    nod.getRabat(),
                    nod.getKw_rabat(),
                    nod.getKw_rozl(),
                    user.getId());

            orderRepository.save(newOrder);

            OrderDealer newOrderDealer = new OrderDealer(
                    newOrder.getDate(),
                    newOrder.getId(),
                    dealer.getCompany(),
                    dealer.getNip(),
                    kw_dealer,
                    doc_dealer
            );

            OrderHandlowiec newOrderHandlowiec = new OrderHandlowiec(
                    newOrder.getDate(),
                    newOrder.getId(),
                    handlowiec.getHandlowiec(),
                    kw_handl,
                    doc_handl
            );

            PaymentDealer paymentDealer = new PaymentDealer(
                    newOrder.getId(),
                    dealerRepository.findByCompany(newOrderDealer.getName()).getId(),
                    doc_dealer,
                    pay_doc_dealer,
                    kw_dealer,
                    null
            );

            PaymentHandlowiec paymentHandlowiec = new PaymentHandlowiec(
                    newOrder.getId(),
                    handlowiec.getId(),
                    doc_handl,
                    pay_doc_handl,
                    kw_handl,
                    null
            );

            OrderStatus newOrderStatus = new OrderStatus(newOrder.getId(), 0);

            orderDealerRepository.save(newOrderDealer);
            orderHandlowiecRepository.save(newOrderHandlowiec);
            orderStatusRepository.save(newOrderStatus);
            paymentDealerRepository.save(paymentDealer);
            paymentHandlowiecRepository.save(paymentHandlowiec);
            rdir.addFlashAttribute("newOrderSuccess", "Pomyślnie dodano nowe rozliczenie");
        }

        return mv;
    }

    @RequestMapping(value = "/rozliczenie/new/send", method = RequestMethod.POST)
    public ModelAndView newOrderPageSecondSend(@RequestParam("hn_dealer") int hn_dealer,
                                         @RequestParam("kw_dealer") Integer kw_dealer,
                                         @RequestParam(value = "kw_handl", required = false) Integer kw_handl,
                                         @RequestParam("nr_fakt") String nr_fakt,
                                         @RequestParam("doc_dealer") int doc_dealer,
                                         @RequestParam("doc_handl") int doc_handl,
                                         @RequestParam(value = "pay_dealer_doc", required = false) String pay_doc_dealer,
                                         @RequestParam(value = "pay_handl_doc", required = false) String pay_doc_handl,
                                         RedirectAttributes rdir,
                                         HttpSession session) {

        ModelAndView mv = new ModelAndView("redirect:/rozliczenia");

        NewOrderDetails nod = (NewOrderDetails) session.getAttribute("newOrderDetails");

        User user = getAuthenticatedUser();

        Dealer dealer = dealerRepository.findById(nod.getDealer()).orElse(null);

        if(dealer == null || user == null) {
            rdir.addFlashAttribute("newOrderError", "Błąd przy dodawaniu rozliczenia.");
            return mv;
        } else if(hn_dealer != -1 && kw_dealer+kw_handl != nod.getKw_rozl()) {
            rdir.addFlashAttribute("newOrderError", "Błąd przy dodawaniu rozliczenia. Suma kwoty dealera i kwoty handlowca powinna być równa kwocie rozliczenia");
            return mv;
        }

        if(kw_handl == null) kw_handl = 0;

        if(hn_dealer == -1) {

            Order newOrder = new Order(
                    nod.getNr_zlec(),
                    nr_fakt,
                    nod.getKw_fakt(),
                    nod.getKw_pocz(),
                    nod.getRabat(),
                    nod.getKw_rabat(),
                    nod.getKw_rozl(),
                    user.getId());

            orderRepository.save(newOrder);

            OrderDealer newOrderDealer = new OrderDealer(
                    newOrder.getDate(),
                    newOrder.getId(),
                    dealer.getCompany(),
                    dealer.getNip(),
                    newOrder.getFinalPrice(),
                    doc_dealer
            );

            OrderHandlowiec newOrderHandlowiec = new OrderHandlowiec(
                    newOrder.getDate(),
                    newOrder.getId(),
                    "---",
                    0,
                    -1
            );

            PaymentDealer paymentDealer = new PaymentDealer(
                    newOrder.getId(),
                    dealerRepository.findByCompany(newOrderDealer.getName()).getId(),
                    doc_dealer,
                    pay_doc_dealer,
                    (int)newOrder.getFinalPrice(),
                    null
            );

            PaymentHandlowiec paymentHandlowiec = new PaymentHandlowiec(
                    newOrder.getId(),
                    -1,
                    -1,
                    "---",
                    0,
                    null
            );

            OrderStatus newOrderStatus = new OrderStatus(newOrder.getId(), 2);

            orderDealerRepository.save(newOrderDealer);
            orderHandlowiecRepository.save(newOrderHandlowiec);
            orderStatusRepository.save(newOrderStatus);
            paymentDealerRepository.save(paymentDealer);
            paymentHandlowiecRepository.save(paymentHandlowiec);
            rdir.addFlashAttribute("newOrderSuccess", "Pomyślnie wysłano nowe rozliczenie");
        } else {

            DealerHandlowcy handlowiec = dealerHandlowcyRepository.findById(hn_dealer).orElse(null);
            if(handlowiec == null) {
                rdir.addFlashAttribute("newOrderError", "Błąd przy dodawaniu rozliczenia.");
                return mv;
            }

            Order newOrder = new Order(
                    nod.getNr_zlec(),
                    nr_fakt,
                    nod.getKw_fakt(),
                    nod.getKw_pocz(),
                    nod.getRabat(),
                    nod.getKw_rabat(),
                    nod.getKw_rozl(),
                    user.getId());

            orderRepository.save(newOrder);

            OrderDealer newOrderDealer = new OrderDealer(
                    newOrder.getDate(),
                    newOrder.getId(),
                    dealer.getCompany(),
                    dealer.getNip(),
                    kw_dealer,
                    doc_dealer
            );

            OrderHandlowiec newOrderHandlowiec = new OrderHandlowiec(
                    newOrder.getDate(),
                    newOrder.getId(),
                    handlowiec.getHandlowiec(),
                    kw_handl,
                    doc_handl
            );

            PaymentDealer paymentDealer = new PaymentDealer(
                    newOrder.getId(),
                    dealerRepository.findByCompany(newOrderDealer.getName()).getId(),
                    doc_dealer,
                    pay_doc_dealer,
                    kw_dealer,
                    null
            );

            PaymentHandlowiec paymentHandlowiec = new PaymentHandlowiec(
                    newOrder.getId(),
                    handlowiec.getId(),
                    doc_handl,
                    pay_doc_handl,
                    kw_handl,
                    null
            );

            OrderStatus newOrderStatus = new OrderStatus(newOrder.getId(), 2);

            orderDealerRepository.save(newOrderDealer);
            orderHandlowiecRepository.save(newOrderHandlowiec);
            orderStatusRepository.save(newOrderStatus);
            paymentDealerRepository.save(paymentDealer);
            paymentHandlowiecRepository.save(paymentHandlowiec);
            rdir.addFlashAttribute("newOrderSuccess", "Pomyślnie wysłano nowe rozliczenie");
        }

        return mv;
    }

    @RequestMapping(value = "/rozliczenia/modify", method = RequestMethod.GET)
    public ModelAndView modifyOrder(@RequestParam int nr, RedirectAttributes rdir) {

        ModelAndView mv = new ModelAndView();

        User user = getAuthenticatedUser();

        try {
            Order order = orderRepository.findById(nr).orElse(null);

            OrderStatus orderStatus = orderStatusRepository.findByOrderID(order.getId());

            if(user != null && orderStatus.getStatus() == 0 && (order.getUserID() == user.getId() || user.getRole().equals("ADMIN"))) {

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
                rdir.addFlashAttribute("openOrderError", "Błąd. Nie można zmodyfikować rozliczenia.");
                mv.setViewName("redirect:/rozliczenia/open");
            }
        } catch(NullPointerException e) {
            rdir.addFlashAttribute("openOrderError", "Błąd. Nie można zmodyfikować rozliczenia.");
            mv.setViewName("redirect:/rozliczenia/open");
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

            try {
                Order order = orderRepository.findById(nr_zlec).orElse(null);
                Order invoiceOrder = orderRepository.findByInvoiceNumber(nr_fakt);
                OrderHandlowiec orderHandlowiec = orderHandlowiecRepository.findByOrderID(order.getId());
                DealerHandlowcy handlowiec = dealerHandlowcyRepository.findById(handl_dealer).orElse(null);
                PaymentHandlowiec paymentHandlowiec = paymentHandlowiecRepository.findByOrderID(nr_zlec);

                if(invoiceOrder == null || (order.getId() == invoiceOrder.getId())) {
                    if(!order.getInvoiceNumber().equals(nr_fakt)) order.setInvoiceNumber(nr_fakt);
                    if(!(order.getInvoicePrice() == kw_fakt)) order.setInvoicePrice(kw_fakt);
                    if(!(order.getOriginalPrice() == kw_pocz)) order.setOriginalPrice(kw_pocz);
                    if(!(order.getDiscount() == rabat)) order.setDiscount(rabat);
                    if(!(order.getDiscountPrice() == kw_rabat)) order.setDiscountPrice(kw_rabat);
                    if(!(order.getFinalPrice() == kw_rozl)) order.setFinalPrice(kw_rozl);

                    if(handl_dealer == -1) {
                        orderHandlowiec.setName("---");
                        orderHandlowiec.setPrice(0);
                        orderHandlowiec.setDocument(-1);
                        orderHandlowiecRepository.save(orderHandlowiec);

                        paymentHandlowiec.setHandlowiecID(-1);
                        paymentHandlowiec.setPayment(-1);
                        paymentHandlowiec.setPaymentAmount(0);
                        paymentHandlowiec.setDocument("---");
                        paymentHandlowiecRepository.save(paymentHandlowiec);
                    } else if(!orderHandlowiec.getName().equals(handlowiec.getHandlowiec())) {
                        orderHandlowiec.setName(handlowiec.getHandlowiec());
                        orderHandlowiecRepository.save(orderHandlowiec);
                    }

                    orderRepository.save(order);
                    rdir.addFlashAttribute("openOrderSuccess", "Pomyślnie zmodyfikowano rozliczenie.");
                } else {
                    rdir.addFlashAttribute("openOrderError", "Rozliczenie o takim numerze faktury już istnieje.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                rdir.addFlashAttribute("openOrderError", "Nie udało się zmodyfikować rozliczenia.");
            }

            mv.setViewName("redirect:/rozliczenia/open");
        }

        return mv;
    }

    @RequestMapping(value = "/rozliczenia/open/send", method = RequestMethod.POST)
    public ModelAndView sendOpenOrder(@RequestParam int nr, RedirectAttributes rdir) {

        ModelAndView mv = new ModelAndView("redirect:/rozliczenia/open");

        try {

            OrderStatus orderStatus = orderStatusRepository.findByOrderID(nr);

            orderStatus.setStatus(2);
            orderStatusRepository.save(orderStatus);

            EmailMessages emailMessages = new EmailMessages(
                    EmailMessages.MAP_TAKS_TYPE.get("ORDER_TO_ACCEPTANCE"),
                    refactorTimestamp(new Timestamp(System.currentTimeMillis())),
                    getAuthenticatedUser().getHandlowiec()
            );

            //Add emiail sender

            rdir.addFlashAttribute("openOrderSuccess", "Pomyślnie wysłano rozliczenie.");
            return mv;

        } catch (Exception e) {
            e.printStackTrace();
            rdir.addFlashAttribute("openOrderError", "Nie udało się wysłać rozliczenia.");
            return mv;
        }
    }

    private String refactorTimestamp(Timestamp timestamp) {

        if(timestamp == null) {
            return "---";
        }

        return new SimpleDateFormat("dd.MM.yyyy HH:mm").format(timestamp);
    }

    private String refactorPaymentType(int document) {
        return switch (document) {
            case -1 -> "---";
            case 1 -> "Faktura";
            case 2 -> "Blik";
            case 3 -> "Gotówka";
            case 4 -> "Umowa";
            default -> "error";
        };
    }

    private String refactorAcceptance(boolean text) {
        if(text) return "TAK";
        else return "NIE";
    }

    private String refactorPayment(Timestamp date) {
        if(date != null) return "TAK";
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
