package com.excel_translator_service.server.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.excel_translator_service.server.exception.ExcelFileUploadException;
import com.excel_translator_service.server.model.excel_translator_data.dto.DownloadExcelDataGetDto;
import com.excel_translator_service.server.model.excel_translator_data.dto.UploadedDetailDto;
import com.excel_translator_service.server.model.excel_translator_header.dto.ExcelTranslatorHeaderGetDto;
import com.excel_translator_service.server.model.message.Message;
import com.excel_translator_service.server.service.excel_translator.ExcelTranslatorHeaderService;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/excel-translator")
public class ExcelTranslatorHeaderApiController {
    
    @Autowired
    private ExcelTranslatorHeaderService excelTranslatorHeaderService;

    /**
     * Create one api for excel translator header.
     * 
     * @param dto : ExcelTranslatorHeaderGetDto
     * @see ExcelTranslatorHeaderSerivce#createTitle
     */
    @PostMapping("/one")
    public ResponseEntity<?> createExcelTranslatorHeaderTitle(@RequestBody ExcelTranslatorHeaderGetDto dto) {
        Message message = new Message();

        excelTranslatorHeaderService.createTitle(dto);
        message.setStatus(HttpStatus.OK);
        message.setMessage("success");

        return new ResponseEntity<>(message, message.getStatus());
    }

    /**
     * Search list api for excel translator header.
     * 
     * @see ExcelTranslatorHeaderService#searchList
     */
    @GetMapping("/list")
    public ResponseEntity<?> searchExcelTranslatorHeader() {
        Message message = new Message();

        message.setData(excelTranslatorHeaderService.searchList());
        message.setStatus(HttpStatus.OK);
        message.setMessage("success");

        return new ResponseEntity<>(message, message.getStatus());
    }

    /**
     * Change one api for excel translator header.
     * 
     * @see ExcelTranslatorHeaderService#changeOne
     */
    @PutMapping("/one")
    public ResponseEntity<?> changeExcelTranslatorHeader(@RequestBody ExcelTranslatorHeaderGetDto dto) {
        Message message = new Message();

        excelTranslatorHeaderService.changeOne(dto);
        message.setStatus(HttpStatus.OK);
        message.setMessage("success");

        return new ResponseEntity<>(message, message.getStatus());
    }

    /**
     * Delete one api for excel translator header.
     * 
     * @query query : Map::String, Object::
     * @see ExcelTranslatorHeaderService#deleteOne
     */
    @DeleteMapping("/one")
    public ResponseEntity<?> deleteExcelTranslatorHeader(@RequestParam Map<String, Object> query) {
        Message message = new Message();

        excelTranslatorHeaderService.deleteOne(query);
        message.setStatus(HttpStatus.OK);
        message.setMessage("success");

        return new ResponseEntity<>(message, message.getStatus());
    }

    /**
     * Upload a free-form excel file.
     * 
     * @param file : MultipartFile
     * @param dto : ExcelTranslatorHeaderGetDto
     * @see ExcelTranslatorHeaderService#uploadExcelFile
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcelFile(@RequestParam("file") MultipartFile file, @RequestPart ExcelTranslatorHeaderGetDto dto) {
        Message message = new Message();

        try{
            message.setData(excelTranslatorHeaderService.uploadExcelFile(file, dto));
            message.setStatus(HttpStatus.OK);
            message.setMessage("success");
        } catch (IllegalArgumentException e) {
            throw new ExcelFileUploadException("????????? ????????? ????????? ?????? ????????? ?????????????????????.");
        } catch (NullPointerException e) {
            throw new ExcelFileUploadException("????????? ????????? ????????? ?????? ????????? ?????????????????????.");
        }

        return new ResponseEntity<>(message, message.getStatus());
    }

    /**
     * Change one api for upload detail of excel translator header.
     * 
     * @param dto : ExcelTranslatorHeaderGetDto
     * @see ExcelTranslatorHeaderService#updateUploadHeaderDetailOfExcelTranslator
     */
    @PutMapping("/header/upload/one")
    public ResponseEntity<?> updateUploadHeaderDetailOfExcelTranslator(@RequestBody ExcelTranslatorHeaderGetDto dto) {
        Message message = new Message();

        try {
            excelTranslatorHeaderService.updateUploadHeaderDetailOfExcelTranslator(dto);
            message.setStatus(HttpStatus.OK);
            message.setMessage("success");
        } catch (NullPointerException e) {
            throw new ExcelFileUploadException("???????????? ?????? ?????? ???????????????. ?????? ??????????????????.");
        }

        return new ResponseEntity<>(message, message.getStatus());
    }

    /**
     * Change one api for download detail of excel translator header.
     * 
     * @param dto : ExcelTranslatorHeaderGetDto
     * @see ExcelTranslatorHeaderService#updateDownloadHeaderDetailOfExcelTranslator
     */
    @PutMapping("/header/download/one")
    public ResponseEntity<?> updateDownloadHeaderDetailOfExcelTranslator(@RequestBody ExcelTranslatorHeaderGetDto dto) {
        Message message = new Message();

        excelTranslatorHeaderService.updateDownloadHeaderDetailOfExcelTranslator(dto);
        message.setStatus(HttpStatus.OK);
        message.setMessage("success");

        return new ResponseEntity<>(message, message.getStatus());
    }

    /**
     * Download excel file.
     * 
     * @param response : HttpServletResponse
     * @param dtos : List::DownloadExcelDataGetDto::
     */
    @PostMapping("/download")
    public void downloadExcelFile(HttpServletResponse response, @RequestBody List<DownloadExcelDataGetDto> dtos) {

        // ?????? ??????
        Workbook workbook = new XSSFWorkbook();     // .xlsx
        Sheet sheet = workbook.createSheet("Sheet1");
        Row row = null;
        Cell cell = null;
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        // ?????? ?????? ?????? ??????
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        for(int i = 0; i < dtos.size(); i++) {
            for(int j = 0; j < dtos.get(i).getTranslatedData().getDetails().size(); j++) {
                // ?????? ???????????? header??? ?????? row?????? ??????
                row = sheet.getRow(j);
                if(row == null) {
                    row = sheet.createRow(j);
                }
                cell = row.createCell(i);
                
                // ????????? ????????? ?????? ?????? ?????? ??????.
                UploadedDetailDto detailDto = dtos.get(i).getTranslatedData().getDetails().get(j);
                try{
                    if(detailDto.getCellType().equals("String")) {
                        cell.setCellValue(detailDto.getColData().toString());
                    }else if(detailDto.getCellType().equals("Date")) {
                        Date data = format.parse(detailDto.getColData().toString());
                        cell.setCellValue(outputFormat.format(data));
                    }else if(detailDto.getCellType().equals("Double")) {
                        cell.setCellValue((int)detailDto.getColData());
                    }
                } catch(ParseException e) {
                    throw new ExcelFileUploadException("????????? ????????? ????????? ???????????????. ?????? ??????????????????.");
                }
            }
        }

        for(int i = 0; i < dtos.size(); i++){
            sheet.autoSizeColumn(i);
        }

        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename=example.xlsx");

        try{
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Download excel file.
     * 
     * @param response : HttpServletResponse
     * @param dtos : List::DownloadExcelDataGetDto::
     */
    @PostMapping("/header/upload/download")
    public void downloadUploadedDetails(HttpServletResponse response, @RequestBody List<UploadedDetailDto> dtos) {

        // ?????? ??????
        Workbook workbook = new XSSFWorkbook();     // .xlsx
        Sheet sheet = workbook.createSheet("Sheet1");
        Row row = null;
        Cell cell = null;

        row = sheet.createRow(0);

        for(int i = 0; i < dtos.size(); i++) {
            cell = row.createCell(i);
            cell.setCellValue(dtos.get(i).getColData().toString());
            sheet.autoSizeColumn(i);
        }

        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename=example.xlsx");

        try{
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }
}
