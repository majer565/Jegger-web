package com.filipmajewski.jeggerweb.controller;

import com.filipmajewski.jeggerweb.container.AcceptedOrderReport;
import com.filipmajewski.jeggerweb.container.CompleteOrder;
import com.filipmajewski.jeggerweb.container.OpenOrderReport;
import com.filipmajewski.jeggerweb.container.PaymentInfo;
import com.filipmajewski.jeggerweb.email.EmailService;
import com.filipmajewski.jeggerweb.email.EmailTaskType;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ReportsController {

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final OrderStatusRepository orderStatusRepository;

    private final OrderDealerRepository orderDealerRepository;

    private final OrderHandlowiecRepository orderHandlowiecRepository;

    private final DealerHandlowcyRepository dealerHandlowcyRepository;

    private final DealerRepository dealerRepository;

    private final HistoryRepository historyRepository;

    private final PaymentDealerRepository paymentDealerRepository;

    private final PaymentHandlowiecRepository paymentHandlowiecRepository;

    private final EmailService emailService;

    @Autowired
    public ReportsController(UserRepository userRepository,
                             OrderRepository orderRepository,
                             OrderStatusRepository orderStatusRepository,
                             OrderDealerRepository orderDealerRepository,
                             OrderHandlowiecRepository orderHandlowiecRepository,
                             DealerHandlowcyRepository dealerHandlowcyRepository,
                             DealerRepository dealerRepository,
                             HistoryRepository historyRepository,
                             PaymentDealerRepository paymentDealerRepository,
                             PaymentHandlowiecRepository paymentHandlowiecRepository,
                             EmailService emailService) {

        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.orderDealerRepository = orderDealerRepository;
        this.orderHandlowiecRepository = orderHandlowiecRepository;
        this.dealerHandlowcyRepository = dealerHandlowcyRepository;
        this.dealerRepository = dealerRepository;
        this.historyRepository = historyRepository;
        this.paymentDealerRepository = paymentDealerRepository;
        this.paymentHandlowiecRepository = paymentHandlowiecRepository;
        this.emailService = emailService;
    }

    @RequestMapping(value = "/raporty/admin", method = RequestMethod.GET)
    public ModelAndView reportsAdminPage(RedirectAttributes rdir) {

        ModelAndView mv = new ModelAndView("reports_admin.html");

        if(!getAuthenticatedUser().getRole().equals("ADMIN")) {
            rdir.addFlashAttribute("reportError", "Błąd. Nie masz wystarczających uprawnień.");
            mv.setViewName("redirect:/raporty");
            return mv;
        }

        List<Order> sentOrderList = new ArrayList<>();

        List<OrderStatus> statusList = orderStatusRepository.findAllByStatus(2);

        for(OrderStatus os : statusList) {
            sentOrderList.add(orderRepository.findById(os.getOrderID()).orElse(null));
        }

        List<OpenOrderReport> orderReportList = new ArrayList<>();

        for(Order o : sentOrderList) {

            User user = userRepository.findById(o.getUserID()).orElse(null);
            OrderDealer orderDealer = orderDealerRepository.findByOrderID(o.getId());

            orderReportList.add(new OpenOrderReport(o.getId(),
                                                    user.getHandlowiec(),
                                                    refactorTimestamp(o.getDate()),
                                                    orderDealer.getName())
            );

        }

        mv.addObject("reportList", orderReportList);

        return mv;
    }

    @RequestMapping(value = "/raporty/admin/details", method = RequestMethod.GET)
    public ModelAndView adminOrderDetails(@RequestParam int nr) {

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


                mv.addObject("completeOrder", new CompleteOrder(order, orderDealer, orderHandlowiec, orderUser));
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

                mv.setViewName("admin_orders_details.html");

            } else {
                mv.setViewName("redirect:/raporty/admin");
            }

        } catch(NullPointerException e) {
            mv.setViewName("redirect:/raporty/admin");
        }

        return mv;
    }

    @RequestMapping(value = "/raporty/admin/approve", method = RequestMethod.POST)
    public ModelAndView adminOrderApprove(@RequestParam("comment") String comment,
                                          @RequestParam("orderID") int orderID,
                                          RedirectAttributes rdir) {

        ModelAndView mv = new ModelAndView("redirect:/raporty/admin");

        try {
            History history = new History(orderID, comment);

            OrderStatus status = orderStatusRepository.findByOrderID(orderID);

            Order order = orderRepository.findById(orderID).orElse(null);
            if(order == null) throw new NullPointerException();

            sendEmail(orderID, EmailTaskType.ACCEPTED_ORDER.getTaskType());

            order.setDealerAcceptance(true);
            order.setHandlowiecAcceptance(true);
            Timestamp date = new Timestamp(System.currentTimeMillis());
            order.setDealerAcceptanceDate(date);
            order.setHandlowiecAcceptanceDate(date);

            status.setStatus(3);
            orderStatusRepository.save(status);
            historyRepository.save(history);
            orderRepository.save(order);

            rdir.addFlashAttribute("reportSuccess", "Pomyślnie zatwierdzono rozliczenie.");
        } catch (Exception e) {
            rdir.addFlashAttribute("reportError", "Niepowodzenie. Proszę skontaktować się z developerem");
        }

        return mv;
    }

    @RequestMapping(value = "/raporty/admin/reject", method = RequestMethod.POST)
    public ModelAndView adminOrderReject(@RequestParam("comment") String comment,
                                         @RequestParam("orderID") int orderID,
                                         RedirectAttributes rdir) {

        ModelAndView mv = new ModelAndView("redirect:/raporty/admin");

        try{
            History history = new History(orderID, comment);

            OrderStatus status = orderStatusRepository.findByOrderID(orderID);

            sendEmail(orderID, EmailTaskType.REJECTED_ORDER.getTaskType());

            status.setStatus(0);
            orderStatusRepository.save(status);
            historyRepository.save(history);

            rdir.addFlashAttribute("reportSuccess", "Pomyślnie odrzucono rozliczenie.");
        }catch (Exception e) {
            rdir.addFlashAttribute("reportError", "Niepowodzenie. Proszę skontaktować się z developerem");
        }

        return mv;
    }

    @RequestMapping("/raporty/rozlicz")
    public ModelAndView reportsPayment(RedirectAttributes rdir){

        ModelAndView mv = new ModelAndView("reports_payment.html");

        User user = getAuthenticatedUser();
        String userRole = user.getRole();

        if(userRole.equals("ADMIN") || userRole.equals("ACCOUNTANT")) {

            List<OrderStatus> orderStatusList = orderStatusRepository.findAllByStatus(4);
            List<Order> orderList = new ArrayList<>();
            List<PaymentDealer> paymentDealerList= new ArrayList<>();
            List<PaymentHandlowiec> paymentHandlowiecList = new ArrayList<>();
            List<PaymentInfo> reportList = new ArrayList<>();
            orderStatusList.forEach(o -> {
                PaymentDealer paymentDealer = paymentDealerRepository.findByOrderID(o.getOrderID());
                PaymentHandlowiec paymentHandlowiec = paymentHandlowiecRepository.findByOrderID(o.getOrderID());

                paymentDealerList.add(paymentDealer);
                paymentHandlowiecList.add(paymentHandlowiec);
            });

            if(userRole.equals("ADMIN")) {
                paymentHandlowiecList.forEach(e -> {
                    if ((e.getPayment() == 2 || e.getPayment() == 3) && e.getPaymentDate() == null) {
                        reportList.add(new PaymentInfo(
                                String.valueOf(e.getOrderID()),
                                dealerHandlowcyRepository.findById(e.getHandlowiecID()).orElse(new DealerHandlowcy()).getHandlowiec(),
                                e.getPaymentAmount() + " PLN",
                                1
                        ));
                    }
                });
            } else {
                paymentHandlowiecList.forEach(e -> {
                    if ((e.getPayment() == 1 || e.getPayment() == 4) && e.getPaymentDate() == null) {
                        reportList.add(new PaymentInfo(
                                String.valueOf(e.getOrderID()),
                                dealerHandlowcyRepository.findById(e.getHandlowiecID()).orElse(new DealerHandlowcy()).getHandlowiec(),
                                e.getPaymentAmount() + " PLN",
                                1
                        ));
                    }
                });

                paymentDealerList.forEach(e -> {
                    if(e.getPayment() == 1 && e.getPaymentDate() == null) {
                        reportList.add(new PaymentInfo(
                                String.valueOf(e.getOrderID()),
                                dealerRepository.findById(e.getDealerID()).orElse(new Dealer()).getCompany(),
                                e.getPaymentAmount() + " PLN",
                                0
                        ));
                    }
                });
            }

            mv.addObject("reportList", reportList);
            return mv;

        } else {
            rdir.addFlashAttribute("reportError", "Błąd. Nie masz wystarczających uprawnień.");
            mv.setViewName("redirect:/raporty");
            return mv;
        }

    }

    @RequestMapping("/raporty/rozliczenia")
    public ModelAndView reportsOrders(){

        ModelAndView mv = new ModelAndView("reports_orders.html");

        User user = getAuthenticatedUser();

        List<OrderStatus> acceptedOrdersStatus = orderStatusRepository.findAllByStatus(3);
        List<Order> acceptedOrders = new ArrayList<>();
        List<AcceptedOrderReport> orderList = new ArrayList<>();

        for(OrderStatus o : acceptedOrdersStatus) {
            Order order = orderRepository.findByIdAndUserID(o.getOrderID(), user.getId());
            if(order != null) acceptedOrders.add(order);
        }

        for(Order o : acceptedOrders) {
            orderList.add(new AcceptedOrderReport(o.getId(),
                                                user.getHandlowiec(),
                                                refactorTimestamp(o.getDealerAcceptanceDate())));
        }

        mv.addObject("reportList", orderList);

        return mv;

    }

    @RequestMapping("/raporty/platnosc/send")
    public ModelAndView sendAcceptedOrder(@RequestParam int nr, RedirectAttributes rdir) {

        ModelAndView mv = new ModelAndView("redirect:/raporty/rozliczenia");

        try {

            OrderStatus orderStatus = orderStatusRepository.findByOrderID(nr);
            PaymentHandlowiec paymentHandlowiec = paymentHandlowiecRepository.findByOrderID(nr);

            //EMAIL CHANGE EMAILS
            if(paymentHandlowiec.getPayment() == 2 || paymentHandlowiec.getPayment() == 3) {
                sendEmail("jegger@jegger.pl", EmailTaskType.ORDER_TO_PAYMENT.getTaskType());
            } else {
                sendEmail("ksiegowy@jegger.pl", EmailTaskType.ORDER_TO_PAYMENT.getTaskType());
            }

            orderStatus.setStatus(4);
            orderStatusRepository.save(orderStatus);

            rdir.addFlashAttribute("reportSuccess", "Pomyślnie wysłano rozliczenie do płatności.");
            return mv;

        } catch (Exception e) {
            rdir.addFlashAttribute("reportError", "Nie udało się wysłać rozliczenia do płatności.");
            return mv;
        }

    }

    @RequestMapping("/raporty/modify/payment")
    public ModelAndView modifyPayment(@RequestParam int nr) {

        ModelAndView mv = new ModelAndView("reports_modify.html");

        Order order = orderRepository.findById(nr).orElse(null);
        PaymentDealer paymentDealer = paymentDealerRepository.findByOrderID(nr);
        PaymentHandlowiec paymentHandlowiec = paymentHandlowiecRepository.findByOrderID(nr);

        mv.addObject("orderID", nr);
        mv.addObject("kw_rozl", order.getFinalPrice());
        mv.addObject("dealerPayment", paymentDealer.getPayment());
        mv.addObject("dealerDocument", paymentDealer.getDocument());
        mv.addObject("dealerPaymentAmount", paymentDealer.getPaymentAmount());
        mv.addObject("handlowiecPayment", paymentHandlowiec.getPayment());
        mv.addObject("handlowiecDocument", paymentHandlowiec.getDocument());
        mv.addObject("handlowiecPaymentAmount", paymentHandlowiec.getPaymentAmount());

        return mv;

    }

    @RequestMapping(value = "/raporty/modify/modify", method = RequestMethod.POST)
    public ModelAndView sendOrderToPayment(@RequestParam int nr,
                                           @RequestParam("dealerPayment") int dealerPayment,
                                           @RequestParam("dealerDocument") String dealerDocument,
                                           @RequestParam("dealerPaymentAmount") int dealerPaymentAmount,
                                           @RequestParam("handlowiecPayment") int handlowiecPayment,
                                           @RequestParam("handlowiecDocument") String handlowiecDocument,
                                           @RequestParam("handlowiecPaymentAmount") int handlowiecPaymentAmount,
                                           RedirectAttributes rdir
                                           ) {

        ModelAndView mv = new ModelAndView("redirect:/raporty/rozliczenia");

        try {
            PaymentDealer paymentDealer = paymentDealerRepository.findByOrderID(nr);
            PaymentHandlowiec paymentHandlowiec = paymentHandlowiecRepository.findByOrderID(nr);
            OrderDealer orderDealer = orderDealerRepository.findByOrderID(nr);
            OrderHandlowiec orderHandlowiec = orderHandlowiecRepository.findByOrderID(nr);

            paymentDealer.setPayment(dealerPayment);
            paymentDealer.setDocument(dealerDocument);
            paymentDealer.setPaymentAmount(dealerPaymentAmount);
            orderDealer.setPrice(dealerPaymentAmount);

            paymentHandlowiec.setPayment(handlowiecPayment);
            paymentHandlowiec.setDocument(handlowiecDocument);
            paymentHandlowiec.setPaymentAmount(handlowiecPaymentAmount);
            orderHandlowiec.setPrice(handlowiecPaymentAmount);

            paymentDealerRepository.save(paymentDealer);
            paymentHandlowiecRepository.save(paymentHandlowiec);

            rdir.addFlashAttribute("reportSuccess", "Pomyślnie zmodyfikowano płatność.");
        } catch (Exception e) {
            e.printStackTrace();
            rdir.addFlashAttribute("reportError", "Nie udało się zmodyfikować płatności.");
        }
        return mv;
    }

    @RequestMapping("/raporty/platnosc/admin")
    public ModelAndView paymentAdmin(@RequestParam int id, @RequestParam int person) {

        ModelAndView mv = new ModelAndView();
        User user = getAuthenticatedUser();
        PaymentHandlowiec paymentHandlowiec = paymentHandlowiecRepository.findByOrderID(id);
        DealerHandlowcy dealerHandlowcy = dealerHandlowcyRepository.findById(paymentHandlowiec.getHandlowiecID()).orElse(null);

        if(user.getRole().equals("ADMIN")) {

            mv.addObject("orderID", id);
            mv.addObject("handlowiec", dealerHandlowcy.getHandlowiec());
            mv.addObject("document", paymentHandlowiec.getDocument());
            mv.addObject("payment", convertPayment(paymentHandlowiec.getPayment()));
            mv.addObject("price", paymentHandlowiec.getPaymentAmount() + " PLN");
            mv.setViewName("reports_pay_admin.html");

        } else if(user.getRole().equals("ACCOUNTANT")) {

            if(person == 0) {

                PaymentDealer paymentDealer = paymentDealerRepository.findByOrderID(id);
                Dealer dealer = dealerRepository.findById(paymentDealer.getDealerID()).orElse(null);

                mv.addObject("orderID", id);
                mv.addObject("person", false);
                mv.addObject("name", dealer.getCompany());
                mv.addObject("document", paymentDealer.getDocument());
                mv.addObject("payment", convertPayment(paymentDealer.getPayment()));
                mv.addObject("price", paymentDealer.getPaymentAmount() + " PLN");

            } else if (person == 1) {

                mv.addObject("orderID", id);
                mv.addObject("person", true);
                mv.addObject("name", dealerHandlowcy.getHandlowiec());
                mv.addObject("document", paymentHandlowiec.getDocument());
                mv.addObject("payment", convertPayment(paymentHandlowiec.getPayment()));
                mv.addObject("price", paymentHandlowiec.getPaymentAmount() + " PLN");

            }

            mv.setViewName("reports_pay_accountant.html");

        }

        return mv;
    }

    @RequestMapping(value = "/raporty/admin/payment/approve", method = RequestMethod.POST)
    public ModelAndView adminPaymentApprove(@RequestParam("comment") String comment,
                                          @RequestParam("orderID") int orderID,
                                          RedirectAttributes rdir) {

        ModelAndView mv = new ModelAndView("redirect:/raporty/rozlicz");

        try {
            History history = new History(orderID, comment);
            PaymentHandlowiec paymentHandlowiec = paymentHandlowiecRepository.findByOrderID(orderID);
            Order order = orderRepository.findById(orderID).orElse(null);

            if(order == null) throw new NullPointerException();
            order.setHandlowiecAcceptance(true);
            Timestamp date = new Timestamp(System.currentTimeMillis());
            order.setHandlowiecAcceptanceDate(date);
            paymentHandlowiec.setPaymentDate(date);


            //Add email sender

            historyRepository.save(history);
            orderRepository.save(order);
            paymentHandlowiecRepository.save(paymentHandlowiec);

            checkOrderCompletion(orderID);

            rdir.addFlashAttribute("reportSuccess", "Pomyślnie przeprowadzono płatność.");
        } catch (Exception e) {
            e.printStackTrace();
            rdir.addFlashAttribute("reportError", "Niepowodzenie. Proszę skontaktować się z developerem");
        }

        return mv;
    }

    @RequestMapping(value = "/raporty/accountant/payment/approve", method = RequestMethod.POST)
    public ModelAndView accountantPaymentApprove(@RequestParam("comment") String comment,
                                            @RequestParam("orderID") int orderID,
                                            @RequestParam("person") boolean person,
                                            RedirectAttributes rdir) {

        ModelAndView mv = new ModelAndView("redirect:/raporty/rozlicz");

        try {
            History history = new History(orderID, comment);
            Order order = orderRepository.findById(orderID).orElse(null);
            PaymentHandlowiec paymentHandlowiec = paymentHandlowiecRepository.findByOrderID(orderID);
            PaymentDealer paymentDealer = paymentDealerRepository.findByOrderID(orderID);

            if(order == null) throw new NullPointerException();

            if(!person) {

                order.setDealerAcceptance(true);
                Timestamp date = new Timestamp(System.currentTimeMillis());
                order.setDealerAcceptanceDate(date);
                paymentDealer.setPaymentDate(date);
                paymentDealerRepository.save(paymentDealer);

            } else {

                order.setHandlowiecAcceptance(true);
                Timestamp date = new Timestamp(System.currentTimeMillis());
                order.setHandlowiecAcceptanceDate(date);
                paymentHandlowiec.setPaymentDate(date);
                paymentHandlowiecRepository.save(paymentHandlowiec);

            }

            //Add email sender

            historyRepository.save(history);
            orderRepository.save(order);

            checkOrderCompletion(orderID);

            rdir.addFlashAttribute("reportSuccess", "Pomyślnie przeprowadzono płatność.");
        } catch (Exception e) {
            e.printStackTrace();
            rdir.addFlashAttribute("reportError", "Niepowodzenie. Proszę skontaktować się z developerem");
        }

        return mv;

    }

    @RequestMapping(value = "/raporty/admin/payment/reject", method = RequestMethod.POST)
    public ModelAndView adminPaymentReject(@RequestParam("comment") String comment,
                                         @RequestParam("orderID") int orderID,
                                         RedirectAttributes rdir) {

        ModelAndView mv = new ModelAndView("redirect:/raporty/rozlicz");

        try{
            History history = new History(orderID, comment);

            OrderStatus status = orderStatusRepository.findByOrderID(orderID);

            sendEmail(orderID, EmailTaskType.REJECTED_PAYMENT.getTaskType());

            status.setStatus(3);
            orderStatusRepository.save(status);
            historyRepository.save(history);

            rdir.addFlashAttribute("reportSuccess", "Pomyślnie odrzucono płatność.");
        }catch (Exception e) {
            rdir.addFlashAttribute("reportError", "Niepowodzenie. Proszę skontaktować się z developerem");
        }

        return mv;
    }

    private void checkOrderCompletion(int id) {

        OrderStatus orderStatus = orderStatusRepository.findByOrderID(id);
        PaymentHandlowiec paymentHandlowiec = paymentHandlowiecRepository.findByOrderID(id);
        PaymentDealer paymentDealer = paymentDealerRepository.findByOrderID(id);

        if (orderStatus.getStatus() == 4) {
            if (paymentDealer.getPayment() == -1 && paymentHandlowiec.getPayment() != -1) {
                if (paymentHandlowiec.getPaymentDate() != null) {
                    orderStatus.setStatus(1);
                    orderStatusRepository.save(orderStatus);
                }
            } else if (paymentDealer.getPayment() != -1 && paymentHandlowiec.getPayment() == -1) {
                if (paymentDealer.getPaymentDate() != null) {
                    orderStatus.setStatus(1);
                    orderStatusRepository.save(orderStatus);
                }
            } else if (paymentDealer.getPayment() != -1 && paymentHandlowiec.getPayment() != -1) {
                if (paymentDealer.getPaymentDate() != null && paymentHandlowiec.getPaymentDate() != null) {
                    orderStatus.setStatus(1);
                    orderStatusRepository.save(orderStatus);
                }
            }
        }
    }

    private void sendEmail(int orderID, String type) {
        Order order = orderRepository.findById(orderID).orElse(null);
        if(order == null) throw new NullPointerException();

        User userToEmail = userRepository.findById(order.getUserID()).orElse(null);
        if(userToEmail == null) throw new NullPointerException();

        emailService.sendEmail(
                userToEmail.getUsername(),
                type,
                getAuthenticatedUser().getHandlowiec()
        );
    }

    private void sendEmail(String to, String type) {
        emailService.sendEmail(
                to,
                type,
                getAuthenticatedUser().getHandlowiec()
        );
    }

    private String convertPayment(int payment) {
        return switch (payment) {
            case 1 -> "Faktura";
            case 2 -> "Blik";
            case 3 -> "Gotówka";
            case 4 -> "Umowa";
            default -> "Brak";
        };
    }

    private User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {

            String currentUserName = authentication.getName();

            return userRepository.findByUsername(currentUserName);
        }

        return null;
    }

    private String refactorTimestamp(Timestamp timestamp) {

        if(timestamp == null) {
            return "---";
        }

        return new SimpleDateFormat("dd.MM.yyyy HH:mm").format(timestamp);
    }

    private String refactorAcceptance(boolean text) {
        if(text) return "TAK";
        else return "NIE";
    }

    private String refactorPayment(Timestamp date) {
        if(date != null) return "TAK";
        else return "NIE";
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

}
