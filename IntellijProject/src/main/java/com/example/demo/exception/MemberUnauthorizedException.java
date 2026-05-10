package com.example.demo.exception;

public class MemberUnauthorizedException extends RuntimeException{
    public MemberUnauthorizedException() {
        super("해당 기능을 사용할 권한이 없습니다.");
    }
    public MemberUnauthorizedException(String message) {
        super(message);
    }
}
