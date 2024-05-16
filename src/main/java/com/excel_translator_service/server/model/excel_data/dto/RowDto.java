package com.excel_translator_service.server.model.excel_data.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RowDto {
    @Builder.Default
    List<DetailDto> details = new ArrayList<>();
}

