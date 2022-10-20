package com.sammy.sendbulksmsservice.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "BULK_SMS_LOG")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BUlkSMsLog extends BaseEntity {
    @Column(name = "PHONE")
    private String phone;
    @Column(name = "message")
    private String message;
    private String status;
    private String statusCode;
    private String esbTransactionReference;
    private String esbStatusCode;
    @Column(columnDefinition = "char(1) default 0")
    private boolean senstiveData;
    private String httpStatusCode;
    private String errorMessage;

}
