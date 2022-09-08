package com.sammy.sendbulksmsservice.SendSms;

import com.google.gson.JsonObject;
import com.sammy.sendbulksmsservice.v2.UniversalResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.SchemaOutputResolver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

@Component
public class SendSms {
    String username = "e011edb66a40";
    String password = "e393b760-e634-4e34-b548-dd06cf75466b";
    String esbCallbackUrl = "http://192.168.20.41:8790/api/service/SAF001-Send-Saf-SMS";

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
//                    case NUMERIC:
//
//                        double phn = cell.getNumericCellValue();
//                        int fin = Integer.valueOf((int) phn);
//
//                        String finalNumber = String.valueOf(fin);
//
//
//
//                        if (finalNumber.startsWith("07") ) {
//                            System.out.println("gets in "+ finalNumber);
//                            finalNumber = removeLeadingZeros(finalNumber);
//                            finalNumber = "254" + finalNumber;
//                        }
//                        edited = finalNumber;
//                        System.out.println(edited + "\n");
//                        break;
                    case STRING:
                        String finalNumber = cell.getStringCellValue();
                        if (finalNumber.startsWith("254")) {
                            edited = finalNumber;
                        }
                        if (finalNumber.startsWith("07") || finalNumber.startsWith("01")) {
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
                routeSms(edited, message);

            }
        }

        file.close();

        return true;
    }


    public String removeLeadingZeros(String digits) {
        String regex = "^0+";
        return digits.replaceAll(regex, "");
    }


    public UniversalResponse routeSms(String phone, String message) throws IOException {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("to", phone);
        jsonObject.addProperty("message", message);

        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity =
                new HttpEntity<String>(jsonObject.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseFromEsb = restTemplate.postForEntity(esbCallbackUrl, requestEntity, String.class);

        if (responseFromEsb.getStatusCode().value() == 200) {
            System.out.println("==> MESSAGE SENT " + message);
            return new UniversalResponse(200, "Message sent", null);
        }

        return new UniversalResponse(responseFromEsb.getStatusCode().value(), "Message sending failed", null);

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
}
