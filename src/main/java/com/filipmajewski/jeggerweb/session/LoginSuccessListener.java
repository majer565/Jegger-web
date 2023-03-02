package com.filipmajewski.jeggerweb.session;

import com.filipmajewski.jeggerweb.entity.Order;
import com.filipmajewski.jeggerweb.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Component
public class LoginSuccessListener implements AuthenticationSuccessHandler {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Timestamp loginDate = new Timestamp(System.currentTimeMillis());

        List<Order> orderList = orderRepository.findAll();

        request.getSession().setAttribute("loginDate", loginDate);
        request.getSession().setAttribute("orderList", orderList);

        response.sendRedirect("/home");
    }
}
