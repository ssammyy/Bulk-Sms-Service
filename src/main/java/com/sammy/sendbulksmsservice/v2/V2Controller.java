package com.sammy.sendbulksmsservice.v2;

import com.google.gson.JsonObject;
import com.sammy.sendbulksmsservice.SendSms.SendSms;
import com.sammy.sendbulksmsservice.Service.ApiCall;
import com.sammy.sendbulksmsservice.Wrappers.BulkWrapper;
import com.sun.jersey.multipart.FormDataParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class V2Controller {

    @Autowired
    SendSms sendSms;
    @Autowired
    FilesStorageServiceImpl filesStorageServiceimpl;
    @Autowired
    ApiCall apiCall;


    @PostMapping("/uploadFile")

    public Object uploadFile(@FormDataParam("file") MultipartFile file) {
        return apiCall.AsyncProcessSmsFromExcel(file);
    }

    @PostMapping("/customers/send-notification")
    public UniversalResponse exportToExcel(@RequestBody BulkWrapper bulkWrapper) throws IOException {
        return apiCall.AsyncProcessSmsFromDatabase(bulkWrapper);
    }
}
