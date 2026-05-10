package com.example.demo.exception;

public enum ErrorCode {

    BAD_REQUEST_PARAM(400, "BAD_REQUEST_PARAM", "요청 값이 올바르지 않습니다."),
    AUTHORIZATION_REQUIRED(401, "AUTHORIZATION_REQUIRED", "로그인이 필요한 작업입니다."),
    LOGIN_FAILED(401, "LOGIN_FAILED", "아이디 또는 비밀번호가 올바르지 않습니다."),
    FORBIDDEN_REQUEST(403, "FORBIDDEN_REQUEST", "권한을 벗어난 요청입니다."),
    POST_NOT_FOUND(404, "POST_NOT_FOUND", "게시글을 찾을 수 없습니다."),
    ID_CONFLICT(409, "ID_CONFLICT", "이미 사용 중인 아이디입니다."),
    // 아래부터는 스프링 서버용 에러
    BAD_REQUEST(400, "BAD_REQUEST", "파라미터의 형태가 올바르지 않습니다"),
    MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION(400, "MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION", "필수 요청 값이 누락되었습니다."),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 에러입니다.");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
