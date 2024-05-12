package com.excel_translator_service.server.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.excel_translator_service.server.model.excel_data.dto.RowDto;
import com.excel_translator_service.server.model.message.Message;
import com.excel_translator_service.server.service.poi_test.PoiTestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/poi-test")
@RequiredArgsConstructor
public class PoiTestApiController {
    private final PoiTestService poiTestService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcelFile(@RequestParam("file") MultipartFile file) throws Exception {
        Message message = new Message();

        try{
            message.setData(poiTestService.uploadExcelFile(file));
            message.setStatus(HttpStatus.OK);
            message.setMessage("success");
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return new ResponseEntity<>(message, message.getStatus());
    }

    @PostMapping("/download")
    public void downloadUploadedDetails(HttpServletResponse response, @RequestBody List<RowDto> rowDtos) {
        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename=cheago_excel.xlsx");
        
        try{
            Workbook workbook = poiTestService.downloadExcelFile(rowDtos);
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }
}
