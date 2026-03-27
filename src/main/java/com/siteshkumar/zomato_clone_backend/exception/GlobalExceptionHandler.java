package com.siteshkumar.zomato_clone_backend.exception;

import org.slf4j.MDC;
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

    private ResponseEntity<ApiError> buildErrorResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(new ApiError(message, status), status);
    }

    // CONFLICT

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex){
        log.warn("[EmailAlreadyExists] {} | reqId={}", ex.getMessage(), MDC.get("requestId"));
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PhoneAlreadyExistsException.class)
    public ResponseEntity<ApiError> handlePhoneAlreadyExistsException(PhoneAlreadyExistsException ex){
        log.warn("[PhoneAlreadyExists] {} | reqId={}", ex.getMessage(), MDC.get("requestId"));
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserAlreadyBlockedException.class)
    public ResponseEntity<ApiError> handleUserAlreadyBlockedException(UserAlreadyBlockedException ex){
        log.warn("[UserAlreadyBlocked] {} | reqId={}", ex.getMessage(), MDC.get("requestId"));
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflictException(ConflictException ex){
        log.warn("[Conflict] {} | reqId={}", ex.getMessage(), MDC.get("requestId"));
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // NOT FOUND

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex){
        log.warn("[ResourceNotFound] {} | reqId={}", ex.getMessage(), MDC.get("requestId"));
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ApiError> handleAddressNotFoundException(AddressNotFoundException ex){
        log.warn("[AddressNotFound] {} | reqId={}", ex.getMessage(), MDC.get("requestId"));
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // BAD REQUEST

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex){
        log.warn("[IllegalArgument] {} | reqId={}", ex.getMessage(), MDC.get("requestId"));
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex){
        log.warn("[TypeMismatch] {} | reqId={}", ex.getMessage(), MDC.get("requestId"));
        return buildErrorResponse("Invalid request parameter", HttpStatus.BAD_REQUEST);
    }

    // FORBIDDEN

    @ExceptionHandler(AccountNotApprovedException.class)
    public ResponseEntity<ApiError> handleAccountNotApprovedException(AccountNotApprovedException ex){
        log.warn("[AccountNotApproved] {} | reqId={}", ex.getMessage(), MDC.get("requestId"));
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex){
        log.warn("[AccessDenied] {} | reqId={}", ex.getMessage(), MDC.get("requestId"));
        return buildErrorResponse("You are not authorized to access this resource", HttpStatus.FORBIDDEN);
    }

    // GLOBAL

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(Exception ex) {
        log.error("[UnhandledException] | reqId={}", MDC.get("requestId"), ex); // full stack trace
        return buildErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}