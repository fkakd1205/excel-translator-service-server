package com.excel_translator_service.server.model.excel_translator_header.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExcelTranslatorDownloadHeaderDetailDto {
    @Setter
    private List<DownloadDetailDto> details;
}
