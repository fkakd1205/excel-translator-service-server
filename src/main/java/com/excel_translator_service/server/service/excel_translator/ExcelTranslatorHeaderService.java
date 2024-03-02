package com.excel_translator_service.server.service.excel_translator;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.excel_translator_service.server.exception.CustomExcelFileUploadException;
import com.excel_translator_service.server.model.excel_translator_data.dto.UploadExcelDataDetailDto;
import com.excel_translator_service.server.model.excel_translator_data.dto.UploadExcelDto;
import com.excel_translator_service.server.model.excel_translator_data.dto.UploadedDetailDto;
import com.excel_translator_service.server.model.excel_translator_header.dto.DownloadDetailDto;
import com.excel_translator_service.server.model.excel_translator_header.dto.ExcelTranslatorHeaderDto;
import com.excel_translator_service.server.model.excel_translator_header.dto.UploadDetailDto;
import com.excel_translator_service.server.model.excel_translator_header.entity.ExcelTranslatorHeaderEntity;
import com.excel_translator_service.server.model.excel_translator_header.repository.ExcelTranslatorHeaderRepository;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExcelTranslatorHeaderService {
    
    @Autowired
    private ExcelTranslatorHeaderRepository excelTranslatorHeaderRepository;

    /**
     * <b>DB Insert Related Method</b>
     * headerId에 대응되는 엑셀 변환기 데이터 조회
     * 
     * @param headerId : UUID
     * @return ExcelTranslatorHeaderDto
     */
    @Transactional(readOnly = true)
    public ExcelTranslatorHeaderDto searchOne(UUID headerId) {
        Optional<ExcelTranslatorHeaderEntity> entityOpt = excelTranslatorHeaderRepository.findById(headerId);

        if (entityOpt.isPresent()) {
            return ExcelTranslatorHeaderDto.toDto(entityOpt.get());
        } else {
            throw new NullPointerException();
        }
    }

    /**
     * <b>DB Insert Related Method</b>
     * 엑셀 변환기를 생성해 타이틀 및 데이터 시작행을 저장한다.
     * 
     * @param dto : ExcelTranslatorHeaderDto
     */
    @Transactional
    public void createTitle(ExcelTranslatorHeaderDto dto) {
        ExcelTranslatorHeaderEntity entity = ExcelTranslatorHeaderEntity.toEntity(dto);
        excelTranslatorHeaderRepository.save(entity);
    }

    /**
     * <b>DB Select Related Method</b>
     * 엑셀 변환기 헤더 데이터를 조회한다.
     * 
     * @return List::ExcelTranslatorHeaderDto::
     */
    @Transactional(readOnly = true)
    public List<ExcelTranslatorHeaderDto> searchAll() {
        List<ExcelTranslatorHeaderEntity> entities = excelTranslatorHeaderRepository.findAll();
        List<ExcelTranslatorHeaderDto> dtos = entities.stream().map(r -> ExcelTranslatorHeaderDto.toDto(r)).collect(Collectors.toList());
        return dtos;
    }

    /**
     * <b>DB Update Related Method</b>
     * 생성된 엑셀 변환기의 타이틀 및 데이터 시작행을 수정한다.
     * 
     * @param dto : ExcelTranslatorHeaderDto
     */
    @Transactional
    public void changeOne(ExcelTranslatorHeaderDto dto) {
        ExcelTranslatorHeaderEntity entity = ExcelTranslatorHeaderEntity.toEntity(dto);

        excelTranslatorHeaderRepository.findById(entity.getId()).ifPresent(header -> {
            header.setDownloadHeaderTitle(entity.getDownloadHeaderTitle())
                .setUploadHeaderTitle(entity.getUploadHeaderTitle())
                .setRowStartNumber(entity.getRowStartNumber());

            excelTranslatorHeaderRepository.save(header);
        });
    }

    /**
     * <b>DB Delete Related Method</b>
     * headerId에 대응하는 엑셀 변환기 데이터를 삭제한다.
     * 
     * @param headerId : UUID
     */
    @Transactional
    public void deleteOne(UUID headerId) {
        excelTranslatorHeaderRepository.findById(headerId).ifPresent(header -> {
            excelTranslatorHeaderRepository.delete(header);
        });
    }

    /**
     * <b>Data Processing Related Method</b>
     * 업로드된 엑셀 파일을 읽는다.
     * 
     * @param file : MultipartFile
     * @param dto : ExcelTranslatorHeaderDto
     * @return List::UploadExcelDto::
     */
    public List<UploadExcelDto> uploadExcelFile(MultipartFile file, ExcelTranslatorHeaderDto dto) {
        Workbook workbook = null;
        try{
            workbook = WorkbookFactory.create(file.getInputStream());
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
        
        Sheet sheet = workbook.getSheetAt(0);
        this.checkUploaderHeaderForm(sheet, dto);
        List<UploadExcelDto> excelDto = this.getUploadedExcelData(sheet, dto);
        return excelDto;
    }

    private void checkUploaderHeaderForm(Sheet worksheet, ExcelTranslatorHeaderDto dto) {
        List<UploadDetailDto> uploadDetailDtos = dto.getUploadHeaderDetail().getDetails();
        Row headerRow = worksheet.getRow(dto.getRowStartNumber()-1);

        if(uploadDetailDtos.size() == 0 || uploadDetailDtos.size() == 0) {
            throw new CustomExcelFileUploadException("설정된 양식과 동일한 엑셀 파일을 업로드해주세요.");
        }

        if(uploadDetailDtos.size() != headerRow.getLastCellNum()) {
            throw new CustomExcelFileUploadException("설정된 양식과 동일한 엑셀 파일을 업로드해주세요.");
        }

        for(int i = 0; i < uploadDetailDtos.size(); i ++) {
            if(!headerRow.getCell(i).toString().equals(uploadDetailDtos.get(i).getHeaderName())) {
                throw new CustomExcelFileUploadException("업로더 양식 [" + uploadDetailDtos.get(i).getHeaderName() + "] 불일치. \n설정된 양식과 동일한 엑셀 파일을 업로드해주세요.");
            }
        }
    }

    /**
     * <b>Data Processing Related Method</b>
     * 업로드된 엑셀 파일을 데이터 시작 행부터 읽어 dto로 반환한다.
     * 
     * @param worksheet : Sheet
     * @param dto : ExcelTranslatorHeaderDto
     * @return List::UploadExcelDto::
     */
    private List<UploadExcelDto> getUploadedExcelData(Sheet worksheet, ExcelTranslatorHeaderDto dto) {
        List<UploadExcelDto> dtos = new ArrayList<>();

        for(int i = dto.getRowStartNumber(); i < worksheet.getPhysicalNumberOfRows(); i++) {
            Row row = worksheet.getRow(i);
            List<UploadedDetailDto> uploadedDetailDtos = new ArrayList<>();

            for(int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                Object cellObj = new Object();

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

                UploadedDetailDto detailDto = UploadedDetailDto.builder().id(UUID.randomUUID()).colData(cellObj).cellType(cellObj.getClass().getSimpleName()).build();  
                uploadedDetailDtos.add(detailDto);
            }
            UploadExcelDataDetailDto uploadedData = UploadExcelDataDetailDto.builder().details(uploadedDetailDtos).build();
            UploadExcelDto dataDto = UploadExcelDto.builder().id(UUID.randomUUID()).uploadedData(uploadedData).build();
            dtos.add(dataDto);
        }
        return dtos;
    }

    /**
     * <b>DB Update Related Method</b>
     * 엑셀 변환기 업로드 헤더 양식을 수정한다. 다운로드 헤더 상세는 초기화시킨다.
     * 
     * @param headerId : UUID
     * @param dtos : List::UploadDetailDto::
     */
    @Transactional
    public void updateUploadHeaderDetails(UUID headerId, List<UploadDetailDto> dtos) {
        Optional<ExcelTranslatorHeaderEntity> entityOpt = excelTranslatorHeaderRepository.findById(headerId);

        if (entityOpt.isPresent()) {
            ExcelTranslatorHeaderEntity entity = entityOpt.get();
            entity.getUploadHeaderDetail().setDetails(dtos);
            entity.getDownloadHeaderDetail().setDetails(new ArrayList<>());
            excelTranslatorHeaderRepository.save(entity);
        } else {
            throw new NullPointerException();
        }
    }

    /**
     * <b>DB Update Related Method</b>
     * 엑셀 변환기 헤더 데이터의 다운로드 헤더 상세를 업데이트한다.
     * 
     * @param headerId : UUID
     * @param dtos : List::DownloadDetailDto::
     */
    @Transactional
    public void updateDownloadHeaderDetail(UUID headerId, List<DownloadDetailDto> dtos) {
        Optional<ExcelTranslatorHeaderEntity> entityOpt = excelTranslatorHeaderRepository.findById(headerId);

        if (entityOpt.isPresent()) {
            ExcelTranslatorHeaderEntity entity = entityOpt.get();
            entity.getDownloadHeaderDetail().setDetails(dtos);
            excelTranslatorHeaderRepository.save(entity);
        } else {
            throw new NullPointerException();
        }
    }
    
    /**
     * 엑셀변환기 업로드 양식을 엑셀다운로드 한다.
     * 
     * @param headerId : UUID
     * @see ExcelTranslatorHeaderService#searchOne
     * @return Workbook
     */
    @Transactional(readOnly = true)
    public Workbook getWorkbookForTranslatorUploaderForm(UUID headerId) {
        ExcelTranslatorHeaderDto dto = this.searchOne(headerId);
        List<UploadDetailDto> detailDtos = dto.getUploadHeaderDetail().getDetails();

        // 엑셀 생성
        Workbook workbook = new XSSFWorkbook();     // .xlsx
        Sheet sheet = workbook.createSheet("Sheet1");
        Row row = null;
        Cell cell = null;

        row = sheet.createRow(0);

        for(int i = 0; i < detailDtos.size(); i++) {
            cell = row.createCell(i);
            sheet.autoSizeColumn(i);
            cell.setCellValue(detailDtos.get(i).getHeaderName());
        }

        return workbook;
    }

    /**
     * 업로드된 데이터를 엑셀변환기 양식에 따라 변환 후 다운로드 한다.
     * 
     * @param headerId : UUID
     * @param dtos : List::UploadExcelDto::
     * @see ExcelTranslatorHeaderService#searchOne
     * @return Workbook
     */
    @Transactional(readOnly = true)
    public Workbook getWorkbookForTranslatedData(UUID headerId, List<UploadExcelDto> dtos) {
        ExcelTranslatorHeaderDto dto = this.searchOne(headerId);
        List<DownloadDetailDto> downloadDetailDtos = dto.getDownloadHeaderDetail().getDetails();
        
        // 엑셀 생성
        Workbook workbook = new XSSFWorkbook();     // .xlsx
        Sheet sheet = workbook.createSheet("Sheet1");
        int rowNum = 0;
        Row row = sheet.createRow(rowNum++);;
        Cell cell = null;
    
        // 날짜 변환 형식 지정
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        
        for (int i = 0; i < downloadDetailDtos.size(); i++) {
            DownloadDetailDto downloadDetailDto = downloadDetailDtos.get(i);
            // 첫번째 row에 헤더 값 세팅
            row = sheet.getRow(0);
            cell = row.createCell(i);
            sheet.autoSizeColumn(i);
            cell.setCellValue(downloadDetailDto.getHeaderName());
            
            for (int j = 0; j < dtos.size(); j++) {
                // 엑셀 데이터는 header의 다음 row부터 기입
                row = sheet.getRow(j+1);
                if (row == null) {
                    row = sheet.createRow(j+1);
                }
                cell = row.createCell(i);
                sheet.autoSizeColumn(i);

                if (downloadDetailDto.getTargetCellNumber() == -1) {
                    cell.setCellValue(downloadDetailDto.getFixedValue());   // 고정값 컬럼이라면 설정된 고정값으로 채운다
                } else {
                    UploadedDetailDto detailDto = dtos.get(j).getUploadedData().getDetails().get(downloadDetailDto.getTargetCellNumber());

                    try {
                        String cellType = detailDto.getCellType();
                        if (cellType.equals("String")) {
                            cell.setCellValue(detailDto.getColData().toString());
                        } else if (cellType.equals("Date")) {
                            Date data = format.parse(detailDto.getColData().toString());
                            cell.setCellValue(outputFormat.format(data));
                        } else if (cellType.equals("Double")) {
                            cell.setCellValue((int)detailDto.getColData());
                        }
                    } catch (ParseException e) {
                        throw new CustomExcelFileUploadException("데이터 변환에 오류. 다시 시도해주세요.");
                    }
                }
            }
        }

        return workbook;
    }
}
