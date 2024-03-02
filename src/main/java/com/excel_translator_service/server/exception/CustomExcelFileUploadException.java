package com.excel_translator_service.server.exception;

public class CustomExcelFileUploadException extends RuntimeException {
    public CustomExcelFileUploadException(String message) {
        super(message);
    }

    public CustomExcelFileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
