package com.example.demo.exception.legacy;

class ForbiddenRequestException extends RuntimeException {
    public ForbiddenRequestException() {
        super("현재 받을 수 없는 요청입니다.");
    }
    public ForbiddenRequestException(String message) {
        super(message);
    }
}
