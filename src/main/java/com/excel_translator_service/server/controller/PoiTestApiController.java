package com.excel_translator_service.server.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
@Slf4j
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
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment");
        
        try{
            Workbook workbook = new SXSSFWorkbook(1000);
            poiTestService.downloadExcelFile(workbook, rowDtos);
            workbook.write(response.getOutputStream());
            workbook.close();
            ((SXSSFWorkbook)workbook).dispose();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    @PostMapping("/sxssf")
    public void sxssfDownload(HttpServletResponse response, @RequestParam("file") MultipartFile file) throws Exception {
        Message message = new Message();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment");

        log.info("start");
        try{
            List<RowDto> rowDtos = poiTestService.uploadExcelFile(file);
            Workbook workbook = new SXSSFWorkbook(1000);

            poiTestService.downloadExcelFile(workbook, rowDtos);
            workbook.write(response.getOutputStream());
            workbook.close();
            ((SXSSFWorkbook)workbook).dispose();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
        log.info("end");
    }

    @PostMapping("/xssf")
    public void xssfDownload(HttpServletResponse response, @RequestParam("file") MultipartFile file) throws Exception {
        Message message = new Message();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment");

        log.info("start");
        try{
            List<RowDto> rowDtos = poiTestService.uploadExcelFile(file);
            Workbook workbook = new XSSFWorkbook();

            poiTestService.downloadExcelFile(workbook, rowDtos);
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
        log.info("end");
    }
}
