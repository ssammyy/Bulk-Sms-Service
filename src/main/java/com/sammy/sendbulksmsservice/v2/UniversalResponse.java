package com.sammy.sendbulksmsservice.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
@ToString
public class UniversalResponse {
    private int status; //200 , 404 - not found , 400 -bad request
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private JsonObject dataObject;
    private String timestamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object metadata;

    public UniversalResponse() {
        this.timestamp = new Date().toString();
    }

    public UniversalResponse(int status, String message, Object data) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.timestamp = new Date().toString();
    }

    public UniversalResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date().toString();
    }
}
