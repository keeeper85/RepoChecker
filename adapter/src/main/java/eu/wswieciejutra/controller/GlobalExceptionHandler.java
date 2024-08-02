package eu.wswieciejutra.controller;

import eu.wswieciejutra.LoggerUtility;
import eu.wswieciejutra.exception.ApiLimitReachedException;
import eu.wswieciejutra.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<?> handleUserNotFoundException(UserNotFoundException e) {
        LoggerUtility.LOGGER.info("User not found", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "status", HttpStatus.NOT_FOUND.value(),
                "message", e.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> handleOtherException(Exception e) {
        LoggerUtility.LOGGER.warn("Resource not found", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "status", HttpStatus.NOT_FOUND.value(),
                "message", e.getMessage()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> handleIllegalArgumentException(Exception e) {
        LoggerUtility.LOGGER.error("Service not available", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "message", e.getMessage()
                ));
    }

    @ExceptionHandler(ApiLimitReachedException.class)
    ResponseEntity<?> handleApiLimitException(Exception e) {
        LoggerUtility.LOGGER.error("API limit reached: ", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "status", HttpStatus.FORBIDDEN.value(),
                "message", e.getMessage()
                ));
    }


}
