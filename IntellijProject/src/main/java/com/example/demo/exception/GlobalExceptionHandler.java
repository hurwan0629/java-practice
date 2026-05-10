package com.example.demo.exception;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        Object detail = e.getDetail();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(ErrorResponse.from(errorCode, detail)));
    }

    // ErrorCode  BAD_REQEUST_PARAM
    // 400 - 사용자용 유효성검사
//    @ExceptionHandler(BadRequestParamException.class)
//    public ResponseEntity<Map<String, String>> handleBadRequestUdf(
//            BadRequestParamException e
//    ) {
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(Map.of("message", e.getMessage()));
//    }

    // ErrorCode AUTHORIZATION_REQUIRED 로 변경
    // 401 - 권한이 필요한 경우에 사용
//    @ExceptionHandler(MemberUnauthorizedException.class)
//    public ResponseEntity<Map<String, String>> handleUnauthorized(
//            MemberUnauthorizedException e
//    ) {
//        return ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED)
//                .body(Map.of("message", e.getMessage()));
//    }

    // ErrorCode LOGIN_FAILED 로 변경
    // 401 - 로그인 시도중 id/pw 문제일 때 사용
//    @ExceptionHandler(LoginFailedException.class)
//    public ResponseEntity<Map<String, String>> handleLoginFailed(
//            LoginFailedException e
//    ) {
//        return ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED)
//                .body(Map.of("message", e.getMessage()));
//    }

    // ErrorCode.FORBIDDEN_REQUEST 으로 변경
    // 403 - 2026-05-10 기준 jwt토큰검사중 발생
//    @ExceptionHandler(ForbiddenRequestException.class)
//    public ResponseEntity<Map<String, String>> handleForbiddenRequest(
//            ForbiddenRequestException e
//    ) {
//        return ResponseEntity
//                .status(HttpStatus.FORBIDDEN)
//                .body(Map.of("message", e.getMessage()));
//    }

    // ErrorCode.FORBIDDEN_REQUEST로 변경
    // 404 - 없는 GET /post/{post_pk} 에 응답
//    @ExceptionHandler(PostNotFoundException.class)
//    public ResponseEntity<Map<String, String>> handlePostNotFound(
//            PostNotFoundException e
//    ) {
//        return ResponseEntity
//                .status(HttpStatus.NOT_FOUND)
//                .body(Map.of("message", e.getMessage()));
//    }

    // ErrorCode.ID_CONFLICT 으로 변경
    // 409 - 2026-05-10 기준 회원가입/로그인 때 사용
//    @ExceptionHandler(IdConflictException.class)
//    public ResponseEntity<Map<String, String>> handleIdConflict(
//            IdConflictException e
//    ) {
//        return ResponseEntity
//                .status(HttpStatus.CONFLICT)
//                .body(Map.of("message", e.getMessage()));
//    }

    /*
     *
     *
     * ------------------------------------------------------
     * --------------  아래는 서버용 에러 덮어씌우기  ------------
     * ------------------------------------------------------
     */

    // 400 (스프링용 파라미터 타입 오류)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException e
    ) {
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(ErrorResponse.from(errorCode)));
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(Map.of("message", "요청 파라미터 타입이 올바르지 않습니다."));
    }

    // 400 (스프링용 파라미터 없을때)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(
            MissingServletRequestParameterException e
    ) {
        ErrorCode errorCode = ErrorCode.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(ErrorResponse.from(errorCode)));
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(Map.of("message", "필수 요청 파라미터가 누락되었습니다."));
    }

    // 500 (전체)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("[" + now + "] [ERROR] " + e.getMessage());

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(ErrorResponse.from(errorCode)));
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(Map.of("message", "서버 오류가 발생했습니다."));
    }
}
