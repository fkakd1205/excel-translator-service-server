package com.excel_translator_service.server.model.excel_translator_header.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.excel_translator_service.server.model.excel_translator_header.dto.ExcelTranslatorUploadHeaderDetailDto;
import com.excel_translator_service.server.model.excel_translator_header.dto.ExcelTranslatorDownloadHeaderDetailDto;
import com.excel_translator_service.server.model.excel_translator_header.dto.ExcelTranslatorHeaderGetDto;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Entity
@Data
@Table(name = "excel_translator_header")
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class ExcelTranslatorHeaderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid")
    private Integer cid;

    @Type(type = "uuid-char")
    @Column(name = "id")
    private UUID id;

    @Column(name = "upload_header_title")
    private String uploadHeaderTitle;

    @Column(name = "download_header_title")
    private String downloadHeaderTitle;

    @Type(type = "json")
    @Column(name = "upload_header_detail", columnDefinition = "json")
    private ExcelTranslatorUploadHeaderDetailDto uploadHeaderDetail = new ExcelTranslatorUploadHeaderDetailDto();

    @Type(type = "json")
    @Column(name = "download_header_detail", columnDefinition = "json")
    private ExcelTranslatorDownloadHeaderDetailDto downloadHeaderDetail = new ExcelTranslatorDownloadHeaderDetailDto();

    @Column(name = "row_start_number")
    private Integer rowStartNumber;

    public static ExcelTranslatorHeaderEntity toEntity(ExcelTranslatorHeaderGetDto dto) {
        ExcelTranslatorHeaderEntity entity = ExcelTranslatorHeaderEntity.builder()
            .id(dto.getId())
            .uploadHeaderTitle(dto.getUploadHeaderTitle())
            .downloadHeaderTitle(dto.getDownloadHeaderTitle())
            .uploadHeaderDetail(dto.getUploadHeaderDetail())
            .downloadHeaderDetail(dto.getDownloadHeaderDetail())
            .rowStartNumber(dto.getRowStartNumber())
            .build();

        return entity;
    }
}
