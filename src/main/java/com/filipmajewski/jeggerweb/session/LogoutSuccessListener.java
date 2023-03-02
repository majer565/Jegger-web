package com.filipmajewski.jeggerweb.session;

import com.filipmajewski.jeggerweb.entity.Session;
import com.filipmajewski.jeggerweb.entity.User;
import com.filipmajewski.jeggerweb.repository.SessionRepository;
import com.filipmajewski.jeggerweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;

@Component
public class LogoutSuccessListener implements LogoutHandler {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Timestamp loginDate = (Timestamp) session.getAttribute("loginDate");
            Timestamp logoutDate = new Timestamp(System.currentTimeMillis());
            User user = getAuthenticatedUser();
            Session userSession = new Session(user.getId(), loginDate, logoutDate);
            sessionRepository.save(userSession);
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
}
