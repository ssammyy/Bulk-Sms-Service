package com.sammy.sendbulksmsservice.SendSms;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sammy.sendbulksmsservice.Entities.BUlkSMsLog;
import com.sammy.sendbulksmsservice.Entities.SmsRepo;
import com.sammy.sendbulksmsservice.SendBulkSmsServiceApplication;
import com.sammy.sendbulksmsservice.v2.UniversalResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Objects;

@Component
public class SendSms {

    private final SmsRepo smsRepo;
    private final RestTemplate restTemplate;
    String username = "e011edb66a40";
    String password = "e393b760-e634-4e34-b548-dd06cf75466b";
    @Value("${esb.smsurl}")
    String esbCallbackUrl;

    public SendSms(SmsRepo smsRepo, RestTemplate restTemplate) {
        this.smsRepo = smsRepo;
        this.restTemplate = restTemplate;
    }

    public boolean loadXlsxData() throws IOException {

        FileInputStream file = null;
        try {
            file = new FileInputStream(new File("uploads/BulkSms.xlsx"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        //Create Workbook instance holding reference to .xlsx file
        XSSFWorkbook workbook = new XSSFWorkbook(file);

        //Get first/desired sheet from the workbook
        XSSFSheet sheet = workbook.getSheetAt(0);

        //Iterate through each rows one by one
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            String edited = null;
            String message = null;

            Row row = rowIterator.next();
            //For each row, iterate through all the columns
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {


                Cell cell = cellIterator.next();
                //Check the cell type and format accordingly
                switch (cell.getCellType()) {

                    case STRING:
                        String finalNumber = cell.getStringCellValue();
                        if (finalNumber.startsWith("254")) {
                            edited = finalNumber;
                        }
                        if (finalNumber.startsWith("07") || finalNumber.startsWith("01") || finalNumber.startsWith("7")) {
                            finalNumber = removeLeadingZeros(finalNumber);
                            finalNumber = "254" + finalNumber;
                            edited = finalNumber;

                        } else {
                            message = cell.getStringCellValue();
                            System.out.println(edited + "\n");
                            System.out.print(cell.getStringCellValue() + "\n");
                        }

                        break;
                    default:
                }
            }
            workbook.close();

            if (edited != null) {
                String finalMessage = message;
                String finalEdited = edited;
                SendBulkSmsServiceApplication.executor.execute(() -> {
                    try {
                        routeSms(finalEdited, finalMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        file.close();
        return true;
    }
    public String removeLeadingZeros(String digits) {
        String regex = "^0+";
        return digits.replaceAll(regex, "");
    }


    public void routeSms(String phone, String message) throws IOException {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("to", phone);
        jsonObject.addProperty("message", message);

        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity =
                new HttpEntity<String>(jsonObject.toString(), headers);

        BUlkSMsLog bUlkSMsLog = new BUlkSMsLog();
        boolean b1 = message.toLowerCase().contains("otp")
                || message.toLowerCase().contains("pin");
        try {

            ResponseEntity<String> responseFromEsb = restTemplate.postForEntity(esbCallbackUrl, requestEntity, String.class);

            JsonObject responseObject = JsonParser.parseString(Objects.requireNonNull(responseFromEsb.getBody())).getAsJsonObject();

            bUlkSMsLog.setPhone(phone);
            bUlkSMsLog.setEsbStatusCode(responseObject.has("esbStatusCode")
                    ? responseObject.get("esbStatusCode").getAsString(): "500");

            bUlkSMsLog.setEsbTransactionReference(responseObject.has("esbTransactionReference")
                    ? responseObject.get("esbTransactionReference").getAsString(): "0");

            bUlkSMsLog.setStatusCode(responseObject.has("statusCode")
                    ? responseObject.get("statusCode").getAsString(): "0");

            bUlkSMsLog.setStatus(responseObject.has("status")
                    ? responseObject.get("status").getAsString(): "FAILED");

            bUlkSMsLog.setSenstiveData(b1);

            bUlkSMsLog.setMessage(bUlkSMsLog.isSenstiveData() ? convertToBase64(message) : message);

            bUlkSMsLog.setHttpStatusCode(String.valueOf(responseFromEsb.getStatusCodeValue()));

        }catch (HttpClientErrorException | HttpServerErrorException e){
            bUlkSMsLog.setHttpStatusCode(String.valueOf(e.getRawStatusCode()));

            bUlkSMsLog.setPhone(phone);
            bUlkSMsLog.setEsbStatusCode("500");

            bUlkSMsLog.setEsbTransactionReference("0");

            bUlkSMsLog.setStatusCode("0");

            bUlkSMsLog.setStatus("FAILED");

            bUlkSMsLog.setSenstiveData(b1);

            bUlkSMsLog.setMessage(bUlkSMsLog.isSenstiveData() ? convertToBase64(message) : message);

            bUlkSMsLog.setErrorMessage(e.getLocalizedMessage());
        }

        smsRepo.save(bUlkSMsLog);
    }

    HttpHeaders createHeaders() {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
        }};
    }

    public String convertToBase64(String message){
        return java.util.Base64.getEncoder().encodeToString(message.getBytes(StandardCharsets.UTF_8));
    }
}
