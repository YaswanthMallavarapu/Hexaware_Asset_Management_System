package com.asset.demo.config;

import com.asset.demo.exceptions.DuplicateRequestException;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.exceptions.UserNotActivatedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private final String message = "message";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ){
        log.atWarn().setCause(e)
                .log("Validation failed in handleMethodArgumentNotValidException()");

        Map<String,Object> map = new HashMap<>();
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> list = bindingResult.getFieldErrors();

        for(FieldError error : list){
            map.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(map);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e
    ){
        log.atWarn().setCause(e)
                .log("Error in handleHttpMessageNotReadableException()");

        Map<String,Object> map = new HashMap<>();
        map.put(message, e.getMessage());

        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(map);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(
            ResourceNotFoundException e
    ){
        log.atWarn().setCause(e)
                .log("Resource not found in handleResourceNotFoundException()");

        Map<String,String> map = new HashMap<>();
        map.put(message, e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(map);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException e
    ){
        log.atWarn().setCause(e)
                .log("Illegal argument in handleIllegalArgumentException()");

        Map<String,Object> map = new HashMap<>();
        map.put(message, e.getMessage());

        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(map);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> HttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e
    ){
        log.atWarn().setCause(e)
                .log("Method not supported in HttpRequestMethodNotSupportedException()");

        Map<String,Object> map = new HashMap<>();
        map.put(message, e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(map);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException e
    ){
        log.atWarn().setCause(e)
                .log("Runtime exception in handleRuntimeException()");

        Map<String,Object> map = new HashMap<>();
        map.put(message, e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(map);
    }

    @ExceptionHandler(UserNotActivatedException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotActivatedException(
            UserNotActivatedException e
    ){
        log.atWarn().setCause(e)
                .log("User not activated in handleUserNotActivatedException()");

        Map<String,Object> map = new HashMap<>();
        map.put(message, e.getMessage());

        return ResponseEntity
                .status(HttpStatus.LOCKED)
                .body(map);
    }

    @ExceptionHandler(DuplicateRequestException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateRequestException(
            DuplicateRequestException e
    ){
        log.atWarn().setCause(e)
                .log("Duplicate request in handleDuplicateRequestException()");

        Map<String,Object> map = new HashMap<>();
        map.put(message, e.getMessage());

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(map);
    }
}