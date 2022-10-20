package com.sammy.sendbulksmsservice.Service;

import com.sammy.sendbulksmsservice.Wrappers.BulkWrapper;
import com.sammy.sendbulksmsservice.v2.UniversalResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ApiCallInterface {

    UniversalResponse processSmsFromDatabase(BulkWrapper bulkWrapper);
    UniversalResponse AsyncProcessSmsFromDatabase(BulkWrapper bulkWrapper);

    UniversalResponse AsyncProcessSmsFromExcel(MultipartFile file);
    UniversalResponse processSmsFromExcel(MultipartFile file);
}
