package com.example.shoppingmall_comp.global.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
//    public BusinessException(ErrorCode errorCode) {
//    super(errorCode);
//        this.errorCode = errorCode;
//    }

    public BusinessException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        // todo: 에러 코드 정의 필요
        return errorCode;
    }
}

