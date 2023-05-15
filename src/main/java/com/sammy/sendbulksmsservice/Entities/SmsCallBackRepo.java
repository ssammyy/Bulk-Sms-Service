package com.postbank.callback.Dao.Repo;

import com.postbank.callback.Dao.SMSCallbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


    @Repository
    public interface SmsCallBackRepo extends JpaRepository<SMSCallbackEntity,Long> {

    }

