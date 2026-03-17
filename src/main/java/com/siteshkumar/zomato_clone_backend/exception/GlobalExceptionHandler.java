package com.siteshkumar.zomato_clone_backend.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex){
        log.warn("EmailAlreadyExistsException: {}", ex.getMessage());
        ApiError apiError = new ApiError(ex.getMessage(), HttpStatus.CONFLICT);
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PhoneAlreadyExistsException.class)
    public ResponseEntity<ApiError> handlePhoneAlreadyExistsException(PhoneAlreadyExistsException ex){
        log.warn("PhoneAlreadyExistsException: {}", ex.getMessage());
        ApiError apiError = new ApiError(ex.getMessage(), HttpStatus.CONFLICT);
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserAlreadyBlockedException.class)
    public ResponseEntity<ApiError> handleUserAlreadyBlockedException(UserAlreadyBlockedException ex){
        log.warn("UserAlreadyBlockedException: {}", ex.getMessage());
        ApiError apiError = new ApiError(ex.getMessage(), HttpStatus.CONFLICT);
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex){
        log.warn("ResourceNotFoundException: {}", ex.getMessage());
        ApiError apiError = new ApiError(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ApiError> handleAddressNotFoundException(AddressNotFoundException ex){
        log.warn("AddressNotFoundException: {}", ex.getMessage());
        ApiError apiError = new ApiError(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex){
        log.warn("IllegalArgumentException: {}", ex.getMessage());
        ApiError apiError = new ApiError(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex){
        log.warn("MethodArgumentTypeMismatchException: {}", ex.getMessage());
        ApiError apiError = new ApiError(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleBadRequestException(ConflictException ex){
        log.warn("ConflictException: {}", ex.getMessage());
        ApiError apiError = new ApiError(ex.getMessage(), HttpStatus.CONFLICT);
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccountNotApprovedException.class)
    public ResponseEntity<ApiError> handleAccountNotApprovedException(AccountNotApprovedException ex){
        log.warn("AccountNotApprovedException: {}", ex.getMessage());
        ApiError apiError = new ApiError(ex.getMessage(), HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex){
        log.warn("AccessDeniedException: {}", ex.getMessage());
        ApiError apiError = new ApiError(ex.getMessage(), HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(Exception ex) {
        log.error("Unhandled Exception occurred", ex); // full stack trace
        ApiError apiError = new ApiError("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}