package org.example.controllers;

import org.example.domain.Result;
import org.example.domain.ResultType;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    Result<?> result = new Result<>();

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Object> handleException(DataAccessException ex) {
        result.addMessage(ResultType.ERROR,"We can't show you the details, but something went wrong in our database. Sorry :(");


        return ErrorResponse.build(result);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleException(IllegalArgumentException ex) {
        result.addMessage(ResultType.ERROR, ex.getMessage());


        return ErrorResponse.build(result);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        result.addMessage(ResultType.ERROR,"Something went wrong on our end. Your request failed. :(");

        return ErrorResponse.build(result);
    }
}