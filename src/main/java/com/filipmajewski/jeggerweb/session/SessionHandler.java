package com.filipmajewski.jeggerweb.session;

import com.filipmajewski.jeggerweb.entity.Session;
import com.filipmajewski.jeggerweb.repository.SessionRepository;

public class SessionHandler {

    private Session session;

    private SessionRepository sessionRepository;

    public SessionHandler(Session session, SessionRepository sessionRepository) {
        this.session = session;
        this.sessionRepository = sessionRepository;
    }



    public Session getSession() {
        return session;
    }

    public SessionRepository getSessionRepository() {
        return sessionRepository;
    }
}
