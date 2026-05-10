package com.example.demo.exception;

public class BadRequestParamException extends RuntimeException {
    public BadRequestParamException() {
        super("잘못된 형태의 요청입니다. 파라미터를 확인해주세요");
    }

    public BadRequestParamException(String message) {
        super(message);
    }
}
