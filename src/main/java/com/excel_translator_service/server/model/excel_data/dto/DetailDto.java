package com.excel_translator_service.server.model.excel_data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailDto {
    private Object colData;
    private String cellType;
}
