package fu.ats.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Small, explicit configuration object for the demo CV parser.
 */
@ConfigurationProperties(prefix = "cv-parse")
public record CvParseProperties(
        String mode,
        MinioProperties minio
) {

    public record MinioProperties(
            String endpoint,
            String accessKey,
            String secretKey,
            String bucket
    ) {
    }
}
