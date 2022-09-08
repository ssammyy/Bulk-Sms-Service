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
    @Resource
    FileStorageService storageService;
    @Autowired
    SendSms sendSms;
    @Autowired
    FilesStorageServiceImpl filesStorageServiceimpl;
    @Autowired
    ApiCall apiCall;


    @PostMapping("/uploadFile")

    public Object uploadFile(@FormDataParam("file") MultipartFile file) {

        JsonObject response = new JsonObject();
        response.addProperty("status", 400);
        response.addProperty("message", "File upload failed");
        String message = "";
        try {
            storageService.save(file, "Incoming");
            try {
                if (sendSms.loadXlsxData()) {
                    boolean check = filesStorageServiceimpl.deleteAll();
                    if (check)
                        log.info("deleted the folder successfully after completion" + "\n");
                }
            } catch (Exception e) {
                message = "Could not send sms file: " + e.getMessage();
                response.addProperty("status", 400);
                response.addProperty("message", message);
            }
            message = "Uploaded the file successfully: ";
            response.addProperty("status", 200);
            response.addProperty("message", message);
        } catch (Exception e) {
            message = "Could not upload the file: ";
            System.out.println(e.getMessage());
            response.addProperty("status", 400);
            response.addProperty("message", message + e.getMessage());
        }

        return response.toString();
    }

    @PostMapping("/customers/send-notification")
    public UniversalResponse exportToExcel(@RequestBody BulkWrapper bulkWrapper) throws IOException {

        try {
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormatter.format(new Date());
            String message = bulkWrapper.getMessage();


            List<?> list = new ArrayList();
            list=apiCall.getPhones(new RestTemplate());
            for (Object p : list) {
                System.out.println("==> PHONE NUMBER ==> > >> "+ p);
                sendSms.routeSms(String.valueOf(p), message);
            }
            return new UniversalResponse(200, "Messages Sent " , null);

        } catch (IOException e) {
            e.printStackTrace();
            return new UniversalResponse(500, "Sending messages failed>", null);
        }

    }
}
