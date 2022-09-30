package com.ps.itp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No Employee Found")
    @ExceptionHandler(EmployeeNotFoundException.class)
    public void employeeNotFound(){

    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such designation defined")
    @ExceptionHandler(DesignationNotFoundException.class)
    public void designationNotFound(){

    }
}