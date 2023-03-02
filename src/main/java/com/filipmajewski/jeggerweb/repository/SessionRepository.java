package com.filipmajewski.jeggerweb.repository;

import com.filipmajewski.jeggerweb.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Integer> {
}
