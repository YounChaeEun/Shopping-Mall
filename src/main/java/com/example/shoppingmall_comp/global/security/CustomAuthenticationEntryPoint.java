package com.example.shoppingmall_comp.global.security;

import com.example.shoppingmall_comp.global.exception.ErrorCode;
import com.example.shoppingmall_comp.global.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = (String) request.getAttribute("exception");

        if (exception == null) {
            setResponse(response, ErrorCode.SERVER_ERROR);
        } else if (exception.equals(ErrorCode.INVALID_JWT_ERROR.getMessage())) {
            setResponse(response, ErrorCode.INVALID_JWT_ERROR);
        } else if (exception.equals(ErrorCode.EXPIRED_JWT_ERROR.getMessage())) {
            setResponse(response, ErrorCode.EXPIRED_JWT_ERROR);
        } else if (exception.equals(ErrorCode.UNSUPPORTED_JWT_TOKEN.getMessage())) {
            setResponse(response, ErrorCode.UNSUPPORTED_JWT_TOKEN);
        } else if (exception.equals(ErrorCode.TOKEN_CLAIM_EMPTY.getMessage())) {
            setResponse(response, ErrorCode.TOKEN_CLAIM_EMPTY);
        } else if (exception.equals(ErrorCode.USER_AUTH_ERROR.getMessage())){
            setResponse(response, ErrorCode.USER_AUTH_ERROR);
        }
    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        ErrorResponse error = new ErrorResponse(errorCode.getStatus(), errorCode.getMessage());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(errorCode.getStatus().value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
    }
}
