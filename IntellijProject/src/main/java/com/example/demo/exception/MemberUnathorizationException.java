package com.example.demo.exception;

public class MemberUnathorizationException extends RuntimeException{
    public MemberUnathorizationException() {
        super("해당 기능을 사용할 권한이 없습니다.");
    }
    public MemberUnathorizationException(String message) {
        super(message);
    }
}
