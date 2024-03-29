package com.filipmajewski.jeggerweb.controller;

import com.filipmajewski.jeggerweb.container.*;
import com.filipmajewski.jeggerweb.entity.*;
import com.filipmajewski.jeggerweb.repository.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Controller
public class HomeController {

    private final OrderRepository orderRepository;

    private final OrderStatusRepository orderStatusRepository;

    private final OrderDealerRepository orderDealerRepository;

    private final OrderHandlowiecRepository orderHandlowiecRepository;

    private final DealerRepository dealerRepository;

    private final UserRepository userRepository;

    private final PaymentDealerRepository paymentDealerRepository;

    private final PaymentHandlowiecRepository paymentHandlowiecRepository;

    private final HistoryRepository historyRepository;

    private final EventRepository eventRepository;

    private final PasswordResetRepository passwordResetRepository;

    @Autowired
    private DealerHandlowcyRepository dealerHandlowcyRepository;

    @Autowired
    public HomeController(OrderRepository orderRepository,
                          OrderStatusRepository orderStatusRepository,
                          OrderDealerRepository orderDealerRepository,
                          OrderHandlowiecRepository orderHandlowiecRepository,
                          DealerRepository dealerRepository,
                          UserRepository userRepository,
                          PaymentDealerRepository paymentDealerRepository,
                          PaymentHandlowiecRepository paymentHandlowiecRepository,
                          HistoryRepository historyRepository,
                          EventRepository eventRepository,
                          PasswordResetRepository passwordResetRepository) {

        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.orderDealerRepository = orderDealerRepository;
        this.orderHandlowiecRepository = orderHandlowiecRepository;
        this.dealerRepository = dealerRepository;
        this.userRepository = userRepository;
        this.paymentDealerRepository = paymentDealerRepository;
        this.paymentHandlowiecRepository = paymentHandlowiecRepository;
        this.historyRepository = historyRepository;
        this.eventRepository = eventRepository;
        this.passwordResetRepository = passwordResetRepository;
    }

    @RequestMapping("/wgraj")
    public String wgraj() {
        return "wgraj.html";
    }

    @RequestMapping("/wgrajBaze")
    public String wgrajBaze() {

        try{

            List<RozliczenieContainer> rozlList = new ArrayList<>();
            List<DCon> dCons = new ArrayList<>();
            List<Hcon> hCons = new ArrayList<>();

            FileInputStream fis = new FileInputStream("data.xls");
            Workbook workbook = new HSSFWorkbook(fis);

            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();

            while(rowIterator.hasNext()) {

                Row row = rowIterator.next();

                Cell cell0 = row.getCell(0);
                long date = (long)cell0.getNumericCellValue();

                Cell cell1 = row.getCell(1);
                String oldOrderNumber = cell1.getStringCellValue();

                Cell cell2 = row.getCell(2);
                String invoiceNumber = cell2.getStringCellValue();

                Cell cell3 = row.getCell(3);
                double invoicePrice = cell3.getNumericCellValue();

                Cell cell4 = row.getCell(4);
                double originalPrice = cell4.getNumericCellValue();

                Cell cell5 = row.getCell(5);
                double discount = cell5.getNumericCellValue();

                Cell cell6 = row.getCell(6);
                double discountPrice = cell6.getNumericCellValue();

                Cell cell7 = row.getCell(7);
                double finalPrice = cell7.getNumericCellValue();

                Cell cell8 = row.getCell(8);
                int userID = (int)cell8.getNumericCellValue();

                Cell cell9 = row.getCell(9);
                int accpet = (int)cell9.getNumericCellValue();

                Cell cell10 = row.getCell(10);
                Date acceptDate = cell10.getDateCellValue();

                Cell cell11 = row.getCell(11);
                String dealerName = cell11.getStringCellValue();

                Cell cell12 = row.getCell(12);
                double dealerPrice = cell12.getNumericCellValue();

                Cell cell13 = row.getCell(13);
                int dealerDocument = (int)cell13.getNumericCellValue();

                Cell cell14 = row.getCell(14);
                String handlowiecName = cell14.getStringCellValue();

                Cell cell15 = row.getCell(15);
                double handlowiecPrice = cell15.getNumericCellValue();

                Cell cell16 = row.getCell(16);
                int handlowiecDocument = (int)cell16.getNumericCellValue();

                rozlList.add(new RozliczenieContainer(
                        oldOrderNumber,
                        new Timestamp(date),
                        invoiceNumber,
                        invoicePrice,
                        originalPrice,
                        discount,
                        discountPrice,
                        finalPrice,
                        userID,
                        accpet,
                        acceptDate,
                        dealerName,
                        dealerPrice,
                        dealerDocument,
                        handlowiecName,
                        handlowiecPrice,
                        handlowiecDocument
                ));
            }

            Sheet sheet2 = workbook.getSheetAt(1);
            Iterator<Row> rowIterator2 = sheet2.iterator();
            while(rowIterator2.hasNext()) {

                Row row = rowIterator2.next();

                Cell cell0 = row.getCell(0);
                long date = (long)cell0.getNumericCellValue();

                Cell cell1 = row.getCell(1);
                String name = cell1.getStringCellValue();

                dCons.add(new DCon(date, name));
            }

            Sheet sheet3 = workbook.getSheetAt(2);
            Iterator<Row> rowIterator3 = sheet3.iterator();
            while(rowIterator3.hasNext()) {

                Row row = rowIterator3.next();

                Cell cell0 = row.getCell(0);
                int dealerID = (int)cell0.getNumericCellValue();

                Cell cell1 = row.getCell(1);
                String name = cell1.getStringCellValue();

                hCons.add(new Hcon(dealerID, name));

            }

            System.out.println(rozlList.size());
            System.out.println(dCons.size());
            System.out.println(hCons.size());

            for(RozliczenieContainer r : rozlList) {

                Order order = new Order(
                        r.getDate(),
                        r.getOldOrderNumber(),
                        r.getInvoiceNumber(),
                        r.getInvoicePrice(),
                        r.getOriginalPrice(),
                        (int)r.getDiscount(),
                        r.getDiscountPrice(),
                        r.getFinalPrice(),
                        r.getUserID(),
                        r.getAccept() == 1,
                        r.getAcceptanceDate()
                );

                orderRepository.save(order);

                System.out.println(order.getId());

                OrderDealer orderDealer = new OrderDealer(
                        r.getDate(),
                        order.getId(),
                        r.getDealerName(),
                        0,
                        r.getDealerPrice(),
                        r.getDealerDocument()
                );

                OrderHandlowiec orderHandlowiec = new OrderHandlowiec(
                        r.getDate(),
                        order.getId(),
                        r.getHandlowiecName(),
                        r.getHandlowiecPrice(),
                        r.getHandlowiecDocument()
                );

                orderDealerRepository.save(orderDealer);
                orderHandlowiecRepository.save(orderHandlowiec);

                OrderStatus orderStatus = new OrderStatus(
                        order.getId(),
                        r.getAccept()
                );

                orderStatusRepository.save(orderStatus);

            }

            for(DCon d : dCons) {
                Dealer dealer = new Dealer(new Timestamp(d.getData()), d.getNazwa());
                dealerRepository.save(dealer);
            }

            for(Hcon h : hCons) {
                DealerHandlowcy handl = new DealerHandlowcy(h.getDealerID(), h.getNazwa());
                dealerHandlowcyRepository.save(handl);
            }

            System.out.println("finish");

        }catch (Exception e) {
            e.printStackTrace();
        }

        return "index.html";
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

            System.out.println("START: " + new Timestamp(System.currentTimeMillis()));
            List<Order> orderList = orderRepository.findAll();
            System.out.println("GOT LIST: " + new Timestamp(System.currentTimeMillis()));
            List<CompleteOrder> completeOrderList = getAdminCompleteOrderList(orderList);
            mav.addObject("completeOrderList", completeOrderList);
            System.out.println("ADDED ORDER: " + new Timestamp(System.currentTimeMillis()));
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
        dealerList.sort(Comparator.comparing(Dealer::getCompany, String.CASE_INSENSITIVE_ORDER));
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

    @RequestMapping(value = "/raporty", method = RequestMethod.GET)
    public ModelAndView reportsPage(HttpSession session) {

        ModelAndView mv = new ModelAndView("reports.html");

        User user = getAuthenticatedUser();
        List<OrderStatus> orderStatusList;
        List<Feed> feedList = new ArrayList<>();

        switch (user.getRole()) {
            case "USER" -> {
                orderStatusList = orderStatusRepository.findAllByStatus(3);
                orderStatusList.forEach(e -> {
                    Order order = orderRepository.findByIdAndUserID(e.getOrderID(), user.getId());
                    if(order != null) feedList.add(new Feed(order.getId(), "ZAAKCEPTOWANE"));
                });
            }
            case "ACCOUNTANT" -> {
                orderStatusList = orderStatusRepository.findAllByStatus(4);
                orderStatusList.forEach(e -> {
                    PaymentDealer paymentD = paymentDealerRepository.findByOrderID(e.getOrderID());
                    PaymentHandlowiec paymentH = paymentHandlowiecRepository.findByOrderIDAndPayment(e.getOrderID(), 1, 4);
                    if(paymentH != null) feedList.add(new Feed(paymentH.getOrderID(), "DO PŁATNOŚCI"));
                    if(paymentD != null) feedList.add(new Feed(paymentD.getOrderID(), "DO PŁATNOŚCI"));
                });
            }
            case "ADMIN" -> {
                orderStatusList = orderStatusRepository.findAllByStatus(2, 4);
                orderStatusList.forEach(e -> {
                    if (e.getStatus() == 2) feedList.add(new Feed(e.getOrderID(), "DO AKCEPTACJI"));
                    else {
                        PaymentHandlowiec payment = paymentHandlowiecRepository.findByOrderIDAndPayment(e.getOrderID(), 2, 3);
                        if(payment != null) feedList.add(new Feed(payment.getOrderID(), "DO PŁATNOŚCI"));
                    }
                });
            }
        }

        mv.addObject("feedList", feedList);

        return mv;
    }

    @RequestMapping(value = "/ustawienia", method = RequestMethod.GET)
    public String settingsPage() {
        return "settings.html";
    }

    @RequestMapping(value = "/ustawienia/resetPassword", method = RequestMethod.GET)
    public String resetPassword() {
        return "settings_pass.html";
    }

    @RequestMapping(value = "/ustawienia/resetPassword/sendCode")
    public ModelAndView resetPasswordSendCode(RedirectAttributes rdir) {

        ModelAndView mv = new ModelAndView("redirect:/ustawienia/resetPassword");

        User user = getAuthenticatedUser();
        int verifyCode = (int)(Math.random()*1000000);
        Long expirationDate = System.currentTimeMillis() + 300000;

        PasswordReset passwordReset = new PasswordReset(user.getId(), verifyCode, expirationDate);
        passwordResetRepository.save(passwordReset);

        //Email sender
        System.out.println(verifyCode + " | " + new Timestamp(expirationDate));
        rdir.addFlashAttribute("resetProcessID", passwordResetRepository.findByVerifyCode(verifyCode).getId());
        rdir.addFlashAttribute("passwordSuccess", "Kod został wysłany na adres email");

        return mv;
    }

    @RequestMapping(value = "/ustawienia/resetPassword/reset", method = RequestMethod.POST)
    public ModelAndView confirmReset(@RequestParam("newPassword") String newPassword,
                                     @RequestParam("newPasswordRepeat") String newPasswordRepeat,
                                     @RequestParam("verifyCode") int verifyCode,
                                     @RequestParam("processID") int id,
                                     RedirectAttributes rdir) {

        ModelAndView mv = new ModelAndView("redirect:/ustawienia/resetPassword");

        PasswordReset passwordReset = passwordResetRepository.findById(id).orElse(null);

        if(passwordReset == null) {

            rdir.addFlashAttribute("passwordError", "Wystąpił błąd. Spróbuj ponownie.");
            return mv;

        } else {
            User user = getAuthenticatedUser();

            if(newPassword.equals(newPasswordRepeat)) {
                if(verifyCode == passwordReset.getVerifyCode()) {

                    //DODAC EXPIRING DATE HANDLERA

                    BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(12);
                    user.setPassword(bcrypt.encode(newPassword));
                    userRepository.save(user);
                    rdir.addFlashAttribute("passwordSuccess", "Pomyślnie zmieniono hasło.");
                    return mv;
                } else {
                    rdir.addFlashAttribute("passwordError", "Błąd. Niepoprawny kod.");
                    return mv;
                }
            } else {
                rdir.addFlashAttribute("passwordError", "Błąd. Podane hasła różnią się.");
                return mv;
            }
        }
    }

    private List<CompleteOrder> getUserCompleteOrderList(List<Order> orderList, User user) {

        ConcurrentLinkedQueue<CompleteOrder> completeOrderList = new ConcurrentLinkedQueue<>();

        orderList.parallelStream().forEachOrdered(o -> {
            int orderID = o.getId();

            completeOrderList.add(new CompleteOrder(
                    o,
                    orderStatusRepository.findByOrderID(orderID),
                    orderDealerRepository.findByOrderID(orderID),
                    orderHandlowiecRepository.findByOrderID(orderID),
                    user
            ));
        });

        return completeOrderList.stream().toList();
    }

    private List<CompleteOrder> getAdminCompleteOrderList(List<Order> orderList) {

        ConcurrentLinkedQueue<CompleteOrder> completeOrderList = new ConcurrentLinkedQueue<>();

        orderList.parallelStream().forEachOrdered(o -> {
            int orderID = o.getId();

            User orderUser = userRepository.findById(o.getUserID()).orElse(new User());
            completeOrderList.add(new CompleteOrder(
                    o,
                    orderStatusRepository.findByOrderID(orderID),
                    orderDealerRepository.findByOrderID(orderID),
                    orderHandlowiecRepository.findByOrderID(orderID),
                    orderUser
            ));
        });

        return completeOrderList.stream().toList();
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
