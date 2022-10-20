package com.sammy.sendbulksmsservice.Entities;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SmsRepo extends JpaRepository<BUlkSMsLog,Long> {

}

