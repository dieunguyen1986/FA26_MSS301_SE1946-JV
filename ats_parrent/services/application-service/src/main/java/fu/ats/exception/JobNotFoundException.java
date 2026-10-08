package fu.ats.exception;

import lombok.Getter;

import java.time.Instant;

@Getter
public class JobNotFoundException extends RuntimeException {
    private String code;
    private String message;
    private String traceId;
    private Instant timestamp;

    public JobNotFoundException() {
    }

    public JobNotFoundException(String code, String message, String traceId, Instant timestamp) {
        super(message);
        this.code = code;
        this.message = message;
        this.traceId = traceId;
        this.timestamp = timestamp;
    }
}
