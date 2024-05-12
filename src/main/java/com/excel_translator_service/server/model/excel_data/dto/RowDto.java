package com.excel_translator_service.server.model.excel_data.dto;

import java.util.List;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RowDto {
    @Type(type = "jsonb")
    List<DetailDto> details;
}

