package com.excel_translator_service.server.controller;

import com.excel_translator_service.server.model.excel_data.dto.RowDto;
import com.excel_translator_service.server.service.poi_test.PoiTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/poi-test")
@RequiredArgsConstructor
@Slf4j
public class PoiTestApiController {
    private final PoiTestService poiTestService;

    @PostMapping("/upload")
    public Object uploadExcelFile(@RequestParam("file") MultipartFile file) throws Exception {
        try{
            return poiTestService.uploadExcelFile(file);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
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
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment");

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
    }

    @PostMapping("/xssf")
    public void xssfDownload(HttpServletResponse response, @RequestParam("file") MultipartFile file) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment");

        try{
            List<RowDto> rowDtos = poiTestService.uploadExcelFile(file);
            Workbook workbook = new XSSFWorkbook();

            poiTestService.downloadExcelFile(workbook, rowDtos);
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }
}
