package fu.ats.client.job;

import feign.Response;
import feign.codec.ErrorDecoder;
import fu.ats.exception.DownStreamUnavailableException;
import fu.ats.exception.JobNotFoundException;

import java.io.IOException;
import java.time.Instant;

public class JobErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {

        switch (response.status()) {
            case 404 -> {
                try {
                    return new JobNotFoundException("JOB_NOT_FOUND", response.body().asInputStream().readAllBytes().toString(), response.reason(), Instant.now());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case 401, 403, 409 -> {
                return new RuntimeException(response.reason());
            }
            case 500, 503, 504 -> {
                return new DownStreamUnavailableException("job-service", response.reason());
            }

            default -> throw new RuntimeException(response.reason());

        }
    }
}
