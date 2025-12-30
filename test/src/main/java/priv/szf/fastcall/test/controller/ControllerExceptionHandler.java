package priv.szf.fastcall.test.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import priv.szf.fastcall.core.common.FcBizException;
import priv.szf.fastcall.core.common.FcDataNotFoundException;
import priv.szf.fastcall.core.common.FcUnexpectedException;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder message = new StringBuilder();
                ex.getBindingResult().getFieldErrors()
                .forEach(error -> message.append(error.getDefaultMessage()).append("|"));
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("parameter validation failed", message.toString()));
    }

    @ExceptionHandler(FcDataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(FcDataNotFoundException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(FcBizException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(FcBizException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler({RuntimeException.class, FcUnexpectedException.class})
    public ResponseEntity<ErrorResponse> handleBusinessException(RuntimeException ex) {
        log.error("unexpected error", ex);
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of("unexpected error", ex.getMessage()));
    }

    @Getter
    private static class ErrorResponse {

        private final String code;

        private final String message;

        private ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

        private static ErrorResponse of(String code, String message) {
            return new ErrorResponse(code, message);
        }
    }

}
