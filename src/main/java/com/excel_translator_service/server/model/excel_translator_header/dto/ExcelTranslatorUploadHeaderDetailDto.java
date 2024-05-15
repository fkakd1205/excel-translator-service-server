package com.excel_translator_service.server.model.excel_translator_header.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ExcelTranslatorUploadHeaderDetailDto {
    @Setter
    private List<UploadDetailDto> details;
}
