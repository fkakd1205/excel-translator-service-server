package com.excel_translator_service.server.model.excel_translator_header.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DownloadDetailDto {
    private UUID id;
    private String headerName;
    private Integer targetCellNumber;
    private String fixedValue;
    private UUID uploadHeaderId;
}
