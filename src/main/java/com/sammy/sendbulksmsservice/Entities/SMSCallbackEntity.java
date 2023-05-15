package com.postbank.callback.Dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TB_SMS_CALLBACK")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SMSCallbackEntity extends BaseEntity {

    @Column(name = "REQUESTID")
    private String requestId;
    @Column(name = "TIMESTAMP")
    private String timestamp;
    @Column(name = "CHANNEL")
    private String channel;
    @Column(name = "OPERATION")
    private String operation;
    @Column(name = "TRACEID")
    private String traceID;
    @Column(name = "REQUESTPARAM")
    private String requestParam;
    @Column(name = "MSISDN")
    private String msisdn;
    @Column(name = "CORRELATORID")
    private String correlatorId;
    @Column(name = "DESCRIPTION")
    private String Description;
    @Column(name = "DELIVERYSTATUS")
    private String deliveryStatus;
    @Column(name = "TYPE")
    private String Type;


}
