package com.filipmajewski.jeggerweb.repository;

import com.filipmajewski.jeggerweb.entity.DealerHandlowcy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealerHandlowcyRepository extends JpaRepository<DealerHandlowcy, Integer> {

    List<DealerHandlowcy> findAllByDealerID(int id);

}
