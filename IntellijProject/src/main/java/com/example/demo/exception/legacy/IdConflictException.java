package com.example.demo.exception.legacy;

class IdConflictException extends RuntimeException {
    public IdConflictException() {
        super("아이디가 중복되었습니다");
    }
    public IdConflictException(String message) {
        super(message);
    }
}
