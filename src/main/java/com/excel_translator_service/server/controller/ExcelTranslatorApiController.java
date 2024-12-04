package com.excel_translator_service.server.controller;

import com.excel_translator_service.server.exception.CustomExcelFileUploadException;
import com.excel_translator_service.server.model.excel_translator_data.dto.UploadExcelDto;
import com.excel_translator_service.server.model.excel_translator_header.dto.DownloadDetailDto;
import com.excel_translator_service.server.model.excel_translator_header.dto.ExcelTranslatorHeaderDto;
import com.excel_translator_service.server.model.excel_translator_header.dto.UploadDetailDto;
import com.excel_translator_service.server.model.message.Message;
import com.excel_translator_service.server.service.excel_translator.ExcelTranslatorHeaderService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/excel-translator")
@RequiredArgsConstructor
public class ExcelTranslatorApiController {
    private final ExcelTranslatorHeaderService excelTranslatorHeaderService;

    /**
     * 엑셀변환기 단일 생성
     * (업로드 양식, 다운로드 양식, 데이터 시작)을 저장한다
     * 
     * @param dto : ExcelTranslatorHeaderDto
     * @see ExcelTranslatorHeaderSerivce#createTitle
     */
    @PostMapping("/one")
    public ResponseEntity<?> createTitle(@RequestBody ExcelTranslatorHeaderDto dto) {
        Message message = new Message();

        excelTranslatorHeaderService.createTitle(dto);
        message.setStatus(HttpStatus.OK);
        message.setMessage("success");

        return new ResponseEntity<>(message, message.getStatus());
    }

    /**
     * 엑셀변환기 전체 조회
     * 
     * @see ExcelTranslatorHeaderService#searchAll
     */
    @GetMapping("/all")
    public ResponseEntity<?> searchAll() {
        Message message = new Message();

        message.setData(excelTranslatorHeaderService.searchAll());
        message.setStatus(HttpStatus.OK);
        message.setMessage("success");

        return new ResponseEntity<>(message, message.getStatus());
    }

    /**
     * 엑셀변환기 단일 수정
     * (업로드 양식, 다운로드 양식, 데이터 시작)을 수정한다
     * 
     * @see ExcelTranslatorHeaderService#changeOne
     */
    @PutMapping("/one")
    public ResponseEntity<?> changeTitle(@RequestBody ExcelTranslatorHeaderDto dto) {
        Message message = new Message();

        excelTranslatorHeaderService.changeOne(dto);
        message.setStatus(HttpStatus.OK);
        message.setMessage("success");

        return new ResponseEntity<>(message, message.getStatus());
    }

    /**
     * 엑셀변환기 단일 삭제
     * 
     * @param headerId : UUID
     * @see ExcelTranslatorHeaderService#deleteOne
     */
    @DeleteMapping("/one/{headerId}")
    public ResponseEntity<?> deleteOne(@PathVariable UUID headerId) {
        Message message = new Message();

        excelTranslatorHeaderService.deleteOne(headerId);
        message.setStatus(HttpStatus.OK);
        message.setMessage("success");

        return new ResponseEntity<>(message, message.getStatus());
    }

    /**
     * 엑셀변환기 업로드 양식에 맞춰 엑셀파일 업로드
     * 업로드된 데이터를 dto로 반환한다
     * 
     * @param file : MultipartFile
     * @param dto : ExcelTranslatorHeaderDto
     * @see ExcelTranslatorHeaderService#uploadExcelFile
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcelFile(@RequestParam("file") MultipartFile file, @RequestPart ExcelTranslatorHeaderDto dto) {
        Message message = new Message();

        try{
            message.setData(excelTranslatorHeaderService.uploadExcelFile(file, dto));
            message.setStatus(HttpStatus.OK);
            message.setMessage("success");
        } catch (IllegalArgumentException e) {
            throw new CustomExcelFileUploadException("설정된 양식과 동일한 엑셀 파일을 업로드해주세요.");
        } catch (NullPointerException e) {
            throw new CustomExcelFileUploadException("설정된 양식과 동일한 엑셀 파일을 업로드해주세요.");
        }

        return new ResponseEntity<>(message, message.getStatus());
    }

    /**
     * 엑셀변환기 업로드 양식 수정
     * 
     * @param headerId : UUID
     * @param dtos : List::UploadDetailDto::
     * @see ExcelTranslatorHeaderService#updateUploadHeaderDetail
     */
    @PutMapping("/header/upload-form/one/{headerId}")
    public ResponseEntity<?> updateUploadHeader(@PathVariable UUID headerId, @RequestBody List<UploadDetailDto> dtos) {
        Message message = new Message();

        try {
            excelTranslatorHeaderService.updateUploadHeaderDetails(headerId, dtos);
            message.setStatus(HttpStatus.OK);
            message.setMessage("success");
        } catch (NullPointerException e) {
            throw new CustomExcelFileUploadException("올바르지 않은 값이 존재합니다. 다시 등록해주세요.");
        }

        return new ResponseEntity<>(message, message.getStatus());
    }

    /**
     * 엑셀변환기 다운로드 양식 수정
     * 
     * @param headerId : UUID
     * @param dtos : List::DownloadDetailDto::
     * @see ExcelTranslatorHeaderService#updateDownloadHeaderDetail
     */
    @PutMapping("/header/download-form/one/{headerId}")
    public ResponseEntity<?> updateDownloadHeader(@PathVariable UUID headerId, @RequestBody List<DownloadDetailDto> dtos) {
        Message message = new Message();

        excelTranslatorHeaderService.updateDownloadHeaderDetail(headerId, dtos);
        message.setStatus(HttpStatus.OK);
        message.setMessage("success");

        return new ResponseEntity<>(message, message.getStatus());
    }

    /**
     * 엑셀변환기 업로더 양식 다운로드
     * 
     * @param response : HttpServletResponse
     * @param headerId : UUID
     * @see ExcelTranslatorHeaderService#getWorkbookForTranslatorUploaderForm
     */
    @GetMapping("/header/upload-form/download/{headerId}")
    public void downloadUploadedDetails(HttpServletResponse response, @PathVariable UUID headerId) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment");
        
        try{
            Workbook workbook = new XSSFWorkbook();     // .xlsx
            excelTranslatorHeaderService.getWorkbookForTranslatorUploaderForm(workbook, headerId);
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
     * @param dtos : List::UploadExcelDto::
     * @see ExcelTranslatorHeaderService#setWorkbookForTranslatedData
     */
    @PostMapping("/download/{headerId}")
    public void downloadExcelFile(HttpServletResponse response, @PathVariable UUID headerId, @RequestBody List<UploadExcelDto> dtos) {        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment");
        
        try{
            Workbook workbook = new SXSSFWorkbook(1000);     // .xlsx, 메모리에 1000개 행 유지
            excelTranslatorHeaderService.setWorkbookForTranslatedData(workbook, headerId, dtos);
            workbook.write(response.getOutputStream());
            workbook.close();
            ((SXSSFWorkbook)workbook).dispose();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }
}
