package com.filipmajewski.jeggerweb.repository;

import com.filipmajewski.jeggerweb.entity.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Integer> {

    Dealer findByNip(long nip);

    Dealer findByCompany(String company);

}
