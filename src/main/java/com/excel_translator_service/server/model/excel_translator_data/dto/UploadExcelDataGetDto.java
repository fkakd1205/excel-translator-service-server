package com.excel_translator_service.server.model.excel_translator_data.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UploadExcelDataGetDto {
    private UUID id;
    private UploadExcelDataDetailDto uploadedData = new UploadExcelDataDetailDto();
}
