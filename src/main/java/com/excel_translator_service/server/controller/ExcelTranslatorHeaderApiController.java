package com.excel_translator_service.server.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import com.excel_translator_service.server.exception.ExcelFileUploadException;
import com.excel_translator_service.server.model.excel_translator_data.dto.DownloadExcelDataGetDto;
import com.excel_translator_service.server.model.excel_translator_data.dto.UploadedDetailDto;
import com.excel_translator_service.server.model.excel_translator_header.dto.DownloadDetailDto;
import com.excel_translator_service.server.model.excel_translator_header.dto.ExcelTranslatorHeaderGetDto;
import com.excel_translator_service.server.model.excel_translator_header.dto.UploadDetailDto;
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
import org.springframework.web.bind.annotation.PathVariable;
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
     * @see ExcelTranslatorHeaderService#searchAll
     */
    @GetMapping("/all")
    public ResponseEntity<?> searchExcelTranslatorHeader() {
        Message message = new Message();

        message.setData(excelTranslatorHeaderService.searchAll());
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
     * @param headerId : UUID
     * @see ExcelTranslatorHeaderService#deleteOne
     */
    @DeleteMapping("/one/{headerId}")
    public ResponseEntity<?> deleteExcelTranslatorHeader(@PathVariable UUID headerId) {
        Message message = new Message();

        excelTranslatorHeaderService.deleteOne(headerId);
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
            throw new ExcelFileUploadException("설정된 양식과 동일한 엑셀 파일을 업로드해주세요.");
        } catch (NullPointerException e) {
            throw new ExcelFileUploadException("설정된 양식과 동일한 엑셀 파일을 업로드해주세요.");
        }

        return new ResponseEntity<>(message, message.getStatus());
    }

    /**
     * Change one api for upload detail of excel translator header.
     * 
     * @param headerId : UUID
     * @param dtos : List::UploadDetailDto::
     * @see ExcelTranslatorHeaderService#updateUploadHeaderDetail
     */
    @PutMapping("/header/upload/one/{headerId}")
    public ResponseEntity<?> updateUploadHeaderDetails(@PathVariable UUID headerId, @RequestBody List<UploadDetailDto> dtos) {
        Message message = new Message();

        try {
            excelTranslatorHeaderService.updateUploadHeaderDetails(headerId, dtos);
            message.setStatus(HttpStatus.OK);
            message.setMessage("success");
        } catch (NullPointerException e) {
            throw new ExcelFileUploadException("올바르지 않은 값이 존재합니다. 다시 등록해주세요.");
        }

        return new ResponseEntity<>(message, message.getStatus());
    }

    /**
     * Change one api for download detail of excel translator header.
     * 
     * @param headerId : UUID
     * @param dtos : List::DownloadDetailDto::
     * @see ExcelTranslatorHeaderService#updateDownloadHeaderDetail
     */
    @PutMapping("/header/download/one/{headerId}")
    public ResponseEntity<?> updateDownloadHeaderDetail(@PathVariable UUID headerId, @RequestBody List<DownloadDetailDto> dtos) {
        Message message = new Message();

        excelTranslatorHeaderService.updateDownloadHeaderDetail(headerId, dtos);
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
        // 엑셀 생성
        Workbook workbook = new XSSFWorkbook();     // .xlsx
        Sheet sheet = workbook.createSheet("Sheet1");
        Row row = null;
        Cell cell = null;
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        // 날짜 변환 형식 지정
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        for(int i = 0; i < dtos.size(); i++){
            sheet.autoSizeColumn(i);
        }
        
        for(int i = 0; i < dtos.size(); i++) {
            for(int j = 0; j < dtos.get(i).getTranslatedData().getDetails().size(); j++) {
                // 엑셀 데이터는 header의 다음 row부터 기입
                row = sheet.getRow(j);
                if(row == null) {
                    row = sheet.createRow(j);
                }
                cell = row.createCell(i);
                
                // 데이터 타입에 맞춰 엑셀 항목 작성.
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
                    throw new ExcelFileUploadException("데이터 변환에 오류가 생겼습니다. 다시 시도해주세요.");
                }
            }
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
     * @param headerId : UUID
     */
    @GetMapping("/header/upload/download/{headerId}")
    public void downloadUploadedDetails(HttpServletResponse response, @PathVariable UUID headerId) {
        ExcelTranslatorHeaderGetDto dto = excelTranslatorHeaderService.searchOne(headerId);
        List<UploadDetailDto> dtos = dto.getUploadHeaderDetail().getDetails();

        // 엑셀 생성
        Workbook workbook = new XSSFWorkbook();     // .xlsx
        Sheet sheet = workbook.createSheet("Sheet1");
        Row row = null;
        Cell cell = null;

        row = sheet.createRow(0);

        for(int i = 0; i < dtos.size(); i++) {
            cell = row.createCell(i);
            // cell에 데이터를 입력하기 전에 autoSizeColumn설정
            sheet.autoSizeColumn(i);
            cell.setCellValue(dtos.get(i).getHeaderName());
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
