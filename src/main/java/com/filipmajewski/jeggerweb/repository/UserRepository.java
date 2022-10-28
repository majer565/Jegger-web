package com.filipmajewski.jeggerweb.repository;

import com.filipmajewski.jeggerweb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
