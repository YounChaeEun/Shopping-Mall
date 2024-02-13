package com.example.shoppingmall_comp.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        log.error("request: %s , message: %s".formatted(request.getRequestURI(), ex.getLocalizedMessage()), ex);
        var error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.error("Request: {}, Message: {}", request.getRequestURI(), ex.getMessage(), ex);
        var error = new ErrorResponse(ex.getErrorCode().getStatus(), ex.getErrorCode().getMessage());
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(error);
    }
}

