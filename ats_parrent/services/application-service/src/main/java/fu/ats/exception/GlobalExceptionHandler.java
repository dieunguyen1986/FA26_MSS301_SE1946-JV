package fu.ats.exception;

import fu.ats.dto.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {//extends ResponseEntityExceptionHandler {

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ApiError> handleException(JobNotFoundException e) {
        e.printStackTrace();
        log.error(e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(e.getCode(), e.getMessage(), e.getTraceId(), e.getTimestamp()));
    }
}
