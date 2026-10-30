package fu.ats.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "candidate.storage")
public record CandidateStorageProperties(
        String endpoint,
        String accessKey,
        String secretKey,
        String bucket,
        DataSize maxFileSize
) {
}
