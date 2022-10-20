package com.sammy.sendbulksmsservice.Service;


import com.google.gson.JsonObject;
import com.sammy.sendbulksmsservice.SendBulkSmsServiceApplication;
import com.sammy.sendbulksmsservice.SendSms.SendSms;
import com.sammy.sendbulksmsservice.Wrappers.BulkWrapper;
import com.sammy.sendbulksmsservice.v2.FileStorageService;
import com.sammy.sendbulksmsservice.v2.FilesStorageServiceImpl;
import com.sammy.sendbulksmsservice.v2.UniversalResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ApiCall implements ApiCallInterface {
    final SendSms sendSms;
    final RestTemplate restTemplate;
    final FilesStorageServiceImpl filesStorageService;
    @Resource
    FileStorageService storageService;
    @Value("${DB.API.URL}")
    String DBAPIURL;

    public ApiCall(SendSms sendSms, RestTemplate restTemplate, FilesStorageServiceImpl filesStorageService) {
        this.sendSms = sendSms;
        this.restTemplate = restTemplate;
        this.filesStorageService = filesStorageService;
    }

    public List<?> getPhones() throws JSONException, IOException {
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

        Map<String, Object> map;
        map = response.getBody();
        JSONObject jsn = new JSONObject(map);

        JSONArray jsonArray;
        jsonArray = jsn.getJSONArray("data");
        JSONObject inner;

        for (int j = 0; j < jsonArray.length(); j++) {
            inner = jsonArray.getJSONObject(j);
            PhoneNumbers.add(inner.get("PHONENUMBER"));
        }
        return PhoneNumbers;

    }

    @Override
    public UniversalResponse processSmsFromDatabase(BulkWrapper bulkWrapper) {
        try {

            List<?> list;
            list = getPhones();

            list.forEach(o -> SendBulkSmsServiceApplication.executor.execute(() -> processSms(bulkWrapper.getMessage(), o)));

            return new UniversalResponse(200, "Messages Sent ", "to " + list.size() + " Customers");

        } catch (IOException e) {
            e.printStackTrace();
            return new UniversalResponse(500, "Sending messages failed", null);
        }
    }

    private void processSms(String message, Object p) {
        log.info("==> PHONE NUMBER ==> > >> " + p);
        try {
            sendSms.routeSms(String.valueOf(p), message);

            // RETRIES

            // DB STORAGE
        } catch (Exception e) {
            e.printStackTrace();
            log.info("==>error sending sms ==> > >> " + p);
        }
    }

    @Override
    public UniversalResponse AsyncProcessSmsFromDatabase(BulkWrapper bulkWrapper) {

        if (bulkWrapper.getMessage().length() > 255)
            return new UniversalResponse(403, "Message too large, Please shorten and try again");

        CompletableFuture.runAsync(() -> processSmsFromDatabase(bulkWrapper));

        return new UniversalResponse(200, "Bulk SMS process initiated"
                , "");

    }


    @Override
    public UniversalResponse AsyncProcessSmsFromExcel(MultipartFile file) {
        if (file.isEmpty())
            return new UniversalResponse(400, "Cannot upload an empty file");


        CompletableFuture.runAsync(() -> processSmsFromExcel(file));
        return new UniversalResponse(200, "Bulk SMS process initiated"
                , "");

    }

    @Override
    public UniversalResponse processSmsFromExcel(MultipartFile file) {
        try {
            if (!storageService.save(file))
                return new UniversalResponse(400, "Failed to save the file for processing"
                        , "");

            if (sendSms.loadXlsxData()) {
                boolean check = filesStorageService.deleteAll();
                if (check)
                    log.info("deleted the folder successfully after completion"
                            + "\n");
            }
        } catch (Exception e) {
            return new UniversalResponse(200, "Bulk SMS process failed"
                    , "");
        }

        return new UniversalResponse(200, "Bulk SMS process completed"
                , "");

    }


}
