package com.sammy.sendbulksmsservice.Service;


import com.google.gson.JsonObject;
import com.sammy.sendbulksmsservice.SendSms.SendSms;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service

public class ApiCall {
    @Autowired
    SendSms sendSms;
    @Value("${DB.API.URL}")
    String DBAPIURL;
    public List<?> getPhones(RestTemplate restTemplate) throws JSONException, IOException {
        ResponseEntity<Map> response = null;
        List PhoneNumbers = new ArrayList();
        try {

            HttpHeaders httpHeaders = new HttpHeaders();
            JsonObject body = new JsonObject();
            body.addProperty("query", "GET_PHONES");


            JsonObject jsonObject = new JsonObject();

            body.add("data", jsonObject);

            httpHeaders.set("accept", "*/*");
            final HttpEntity<String> entity = new HttpEntity<String>(body.toString(), httpHeaders);

            //Execute the method writing your HttpEntity to the request
            response = restTemplate.exchange(DBAPIURL, HttpMethod.POST, entity, Map.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
//        System.out.println(response.getBody());

        Map jo;
        jo=response.getBody();

        Map<String, Object> map = new HashMap();
        map = response.getBody();
        String str ="";



        JSONObject jsn = new JSONObject(map);


        JSONArray jsonArray = new JSONArray();
        jsonArray=jsn.getJSONArray("data");
        JSONObject inner = new JSONObject();

        for(int j= 0 ; j<jsonArray.length(); j++){
             inner = jsonArray.getJSONObject(j);
            PhoneNumbers.add(inner.get("PHONENUMBER"));
        }



        for(Object p : PhoneNumbers)
            sendSms.routeSms(String.valueOf(p), "hello");

        return PhoneNumbers;

    }
}
