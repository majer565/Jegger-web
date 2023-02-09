package com.filipmajewski.jeggerweb.repository;

import com.filipmajewski.jeggerweb.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Integer> {

    PasswordReset findByVerifyCode(int code);

}
