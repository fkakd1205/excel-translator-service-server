package com.excel_translator_service.server.model.excel_translator_header.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UploadDetailDto {
    private UUID id;
    private String headerName;
    private Integer cellNumber;
    private String cellType;
}
