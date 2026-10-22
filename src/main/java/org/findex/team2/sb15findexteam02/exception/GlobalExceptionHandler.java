package org.findex.team2.sb15findexteam02.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.findex.team2.sb15findexteam02.dto.response.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArgument(
            IllegalArgumentException exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(DateTimeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDate(
            DateTimeException exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "날짜 형식이 올바르지 않습니다.", request);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NoSuchElementException exception, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataConflict(
            DataIntegrityViolationException exception, HttpServletRequest request) {
        return response(HttpStatus.CONFLICT, "데이터 제약 조건에 위배되는 요청입니다.", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception exception, HttpServletRequest request) {
        log.error("요청 처리 실패: {} {}", request.getMethod(), request.getRequestURI(), exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.", request);
    }

    // Spring MVC의 바인딩, JSON 파싱, HTTP 메서드 오류 등도 같은 응답 형식으로 변환한다.
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        if (request instanceof ServletWebRequest servletRequest
                && servletRequest.getResponse() != null
                && servletRequest.getResponse().isCommitted()) {
            return null;
        }

        HttpStatus httpStatus = HttpStatus.resolve(status.value());
        String message = httpStatus == null ? "요청 처리에 실패했습니다." : httpStatus.getReasonPhrase();
        if (status.is5xxServerError()) {
            log.error("Spring MVC 요청 처리 실패", exception);
            message = status.value() == 502
                    ? "외부 API 응답을 처리하지 못했습니다."
                    : "서버 내부 오류가 발생했습니다.";
        }
        String details = request instanceof ServletWebRequest servletRequest
                ? servletRequest.getRequest().getRequestURI() : request.getDescription(false);
        return new ResponseEntity<>(new ErrorResponse(
                LocalDateTime.now(), status.value(), message, details), headers, status);
    }

    private ResponseEntity<ErrorResponse> response(
            HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ErrorResponse(
                LocalDateTime.now(), status.value(),
                message == null || message.isBlank() ? status.getReasonPhrase() : message,
                request.getRequestURI()));
    }

}
