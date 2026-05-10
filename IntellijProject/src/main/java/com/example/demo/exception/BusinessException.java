package com.example.demo.exception;

public class BusinessException extends RuntimeException{

    private final ErrorCode errorCode;
    private final Object detail;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detail = null;
    }

    public BusinessException(ErrorCode errorCode, Object detail) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detail = detail;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public Object getDetail() {
        return this.detail;
    }
}
