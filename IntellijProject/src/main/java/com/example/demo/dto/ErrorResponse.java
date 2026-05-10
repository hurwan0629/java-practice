package com.example.demo.dto;

import com.example.demo.exception.ErrorCode;

public class ErrorResponse {

    private String code;
    private String message;
    private int status;
    private Object detail;

    public ErrorResponse(String code, String message, int status, Object detail) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.detail = detail;
    }

    public static ErrorResponse from(ErrorCode errorCode, Object detail) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                errorCode.getStatus(),
                detail
        );
    }

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                errorCode.getStatus(),
                null
        );
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
    public Object getDetail() {
        return detail;
    }
}
