package com.excel_translator_service.server.model.message;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private HttpStatus status;
    private String message;
    private String memo;
    private Object data;
    private Object pagenation;
}
