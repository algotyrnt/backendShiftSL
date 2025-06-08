package com.shiftsl.backend.Exceptions;

import com.google.api.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // General Exception handler
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleUnknownException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(ex.getMessage());
    }

    // Not found exception handlers
    @ExceptionHandler({LeaveNotFoundException.class})
    public ResponseEntity<Object> handleLeaveNotFoundException(LeaveNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler({ShiftNotFoundException.class})
    public ResponseEntity<Object> handleShiftNotFoundException(ShiftNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler({ShiftsNotFoundException.class})
    public ResponseEntity<Object> handleShiftsNotFoundException(ShiftsNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler({ShiftSwapNotFoundException.class})
    public ResponseEntity<Object> handleShiftSwapNotFoundException(ShiftSwapNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler({WardNotFoundException.class})
    public ResponseEntity<Object> handleWardNotFoundException(WardNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler({UserNotUpdatedException.class})
    public ResponseEntity<Object> handleUserNotUpdatedException(UserNotUpdatedException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }


    // Server/Logic Error handlers
    @ExceptionHandler({AccountNotCreatedException.class})
    public ResponseEntity<Object> handleAccountNotCreatedException(AccountNotCreatedException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

    @ExceptionHandler({DoctorCountExceededException.class})
    public ResponseEntity<Object> handleDoctorCountExceededException(DoctorCountExceededException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

    @ExceptionHandler({ShiftClaimFailedException.class})
    public ResponseEntity<Object> handleShiftClaimFailedException(ShiftClaimFailedException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

    @ExceptionHandler({NotAWardAdminException.class})
    public ResponseEntity<Object> handleNotAWardAdminException(NotAWardAdminException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }

    @ExceptionHandler({PhoneAlreadyInUseException.class})
    public ResponseEntity<Object> handlePhoneAlreadyInUseException(PhoneAlreadyInUseException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }
}
