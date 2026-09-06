package com.pragma.powerup.infrastructure.exceptionhandler;

import com.pragma.powerup.domain.exception.ValidationException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvisor {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> invalidRequest(MethodArgumentNotValidException exception) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> fields.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Collections.singletonMap("errors", fields));
    }

    @ExceptionHandler(ValidationException.class)
    ResponseEntity<Map<String, Object>> businessValidation(ValidationException exception) {
        return ResponseEntity.badRequest().body(errorBody(exception.getMessage()));
    }

    private Map<String, Object> errorBody(String message) {
        return Collections.singletonMap("errors", Collections.singletonMap("message", message));
    }
}
