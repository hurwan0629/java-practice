package com.example.demo.exception;

public class ForbiddenRequestException extends RuntimeException {
    public ForbiddenRequestException() {
        super("현재 받을 수 없는 요청입니다.");
    }
}
