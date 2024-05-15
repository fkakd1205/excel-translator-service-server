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
import com.excel_translator_service.server.model.excel_translator_header.dto.ExcelTranslatorHeaderDto;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


@Entity
@Getter
@ToString
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = "excel_translator_header")
public class ExcelTranslatorHeaderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid")
    private Integer cid;

    @Type(type = "uuid-char")
    @Column(name = "id")
    private UUID id;

    @Setter
    @Column(name = "upload_header_title")
    private String uploadHeaderTitle;

    @Setter
    @Column(name = "download_header_title")
    private String downloadHeaderTitle;

    @Setter
    @Type(type = "json")
    @Column(name = "upload_header_detail")
    private ExcelTranslatorUploadHeaderDetailDto uploadHeaderDetail = new ExcelTranslatorUploadHeaderDetailDto();

    @Setter
    @Type(type = "json")
    @Column(name = "download_header_detail")
    private ExcelTranslatorDownloadHeaderDetailDto downloadHeaderDetail = new ExcelTranslatorDownloadHeaderDetailDto();

    @Setter
    @Column(name = "row_start_number")
    private Integer rowStartNumber;

    public static ExcelTranslatorHeaderEntity toEntity(ExcelTranslatorHeaderDto dto) {
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
