package com.filipmajewski.jeggerweb.repository;

import com.filipmajewski.jeggerweb.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Integer> {

    List<History> findAllByOrderID(int orderID);

}
