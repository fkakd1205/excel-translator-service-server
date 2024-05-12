package com.excel_translator_service.server.model.excel_translator_header.dto;

import java.util.UUID;

import com.excel_translator_service.server.model.excel_translator_header.entity.ExcelTranslatorHeaderEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ExcelTranslatorHeaderDto {
    private Integer cid;
    private UUID id;
    private String uploadHeaderTitle;
    private String downloadHeaderTitle;
    @Builder.Default
    private ExcelTranslatorUploadHeaderDetailDto uploadHeaderDetail = new ExcelTranslatorUploadHeaderDetailDto();
    @Builder.Default
    private ExcelTranslatorDownloadHeaderDetailDto downloadHeaderDetail = new ExcelTranslatorDownloadHeaderDetailDto();
    private Integer rowStartNumber;

    public static ExcelTranslatorHeaderDto toDto(ExcelTranslatorHeaderEntity entity) {
        ExcelTranslatorHeaderDto dto = ExcelTranslatorHeaderDto.builder()
            .id(entity.getId())
            .uploadHeaderTitle(entity.getUploadHeaderTitle())
            .downloadHeaderTitle(entity.getDownloadHeaderTitle())
            .uploadHeaderDetail(entity.getUploadHeaderDetail())
            .downloadHeaderDetail(entity.getDownloadHeaderDetail())
            .rowStartNumber(entity.getRowStartNumber())
            .build();

        return dto;
    }
}
