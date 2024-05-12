package com.excel_translator_service.server.service.poi_test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.excel_translator_service.server.model.excel_data.dto.DetailDto;
import com.excel_translator_service.server.model.excel_data.dto.RowDto;

@Service
public class PoiTestService {

    public List<RowDto> uploadExcelFile(MultipartFile file) {
        Workbook workbook = null;
        List<RowDto> excelDto = null;

        try{
            // MultipartFile을 읽어 Workbook 생성
            workbook = WorkbookFactory.create(file.getInputStream());
            
            // Workbook의 첫번째 시트를 읽는다
            Sheet sheet = workbook.getSheetAt(0);
            excelDto = this.getUploadedExcelData(sheet);
            workbook.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("엑셀 파일 업로드 오류");
        }
        
        return excelDto;
    }

    private List<RowDto> getUploadedExcelData(Sheet worksheet) {
        List<RowDto> dtos = new ArrayList<>();
        List<DetailDto> detailDtos = null;

        // sheet의 Row 개수만큼 반복
        for(int i = 0; i < worksheet.getPhysicalNumberOfRows(); i++) {
            Row row = worksheet.getRow(i);
            detailDtos = new ArrayList<>();
            
            if (row == null) break;

            // 현재 Row의 Cell 개수만큼 반복
            for(int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                Object cellObj = new Object();

                // Cell 타입에 따라 값을 읽는다
                if(cell == null || cell.getCellType().equals(CellType.BLANK)) {
                    cellObj = "";
                } else if (cell.getCellType().equals(CellType.STRING)) {
                    cellObj = cell.getStringCellValue();
                } else if (cell.getCellType().equals(CellType.NUMERIC)) {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cellObj = cell.getDateCellValue();
                    } else {
                        cellObj = cell.getNumericCellValue();
                    }
                }

                DetailDto detailDto = DetailDto.builder().colData(cellObj).cellType(cellObj.getClass().getSimpleName()).build();  
                detailDtos.add(detailDto);
            }
            // Row별로 저장할 DetailDto들을 설정한다
            RowDto uploadedData = RowDto.builder().details(detailDtos).build();
            dtos.add(uploadedData);
        }
        return dtos;
    }

    public Workbook downloadExcelFile(List<RowDto> rowDtos) {
        // Workbook, Sheet 생성
        Workbook workbook = new XSSFWorkbook();     // .xlsx
        Sheet sheet = workbook.createSheet("Sheet1");
        Row row = null;
        Cell cell = null;

        for(int i = 0; i < rowDtos.size(); i++) {
            row = sheet.createRow(i);
            List<DetailDto> detailDtos = rowDtos.get(i).getDetails();
            
            for(int j = 0; j < detailDtos.size(); j++) {
                cell = row.createCell(j);
                sheet.autoSizeColumn(j);
                cell.setCellValue(detailDtos.get(j).getColData().toString());
            }
        }

        return workbook;
    }
}
