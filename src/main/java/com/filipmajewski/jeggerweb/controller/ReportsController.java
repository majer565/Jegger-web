package com.filipmajewski.jeggerweb.controller;

import com.filipmajewski.jeggerweb.container.AcceptedOrderReport;
import com.filipmajewski.jeggerweb.container.CompleteOrder;
import com.filipmajewski.jeggerweb.container.OpenOrderReport;
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

    private UserRepository userRepository;

    private OrderRepository orderRepository;

    private OrderStatusRepository orderStatusRepository;

    private OrderDealerRepository orderDealerRepository;

    private OrderHandlowiecRepository orderHandlowiecRepository;

    private HistoryRepository historyRepository;

    @Autowired
    public ReportsController(UserRepository userRepository,
                             OrderRepository orderRepository,
                             OrderStatusRepository orderStatusRepository,
                             OrderDealerRepository orderDealerRepository,
                             OrderHandlowiecRepository orderHandlowiecRepository,
                             HistoryRepository historyRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.orderDealerRepository = orderDealerRepository;
        this.orderHandlowiecRepository = orderHandlowiecRepository;
        this.historyRepository = historyRepository;
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

            if(userRole.equals("ADMIN")) {



            } else {

            }

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
            acceptedOrders.add(orderRepository.findByIdAndUserID(o.getOrderID(), user.getId()));
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

            orderStatus.setStatus(4);
            orderStatusRepository.save(orderStatus);

            //Add emiail sender

            rdir.addFlashAttribute("reportSuccess", "Pomyślnie wysłano rozliczenie do płatności.");
            return mv;

        } catch (Exception e) {
            rdir.addFlashAttribute("reportError", "Nie udało się wysłać rozliczenia do płatności.");
            return mv;
        }

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
}
