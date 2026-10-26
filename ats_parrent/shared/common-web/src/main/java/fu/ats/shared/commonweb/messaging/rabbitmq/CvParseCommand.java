package fu.ats.shared.commonweb.messaging.rabbitmq;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * RabbitMQ command asking cv-parse-service to parse one stored CV.
 *
 * <p>Only metadata is transported. The original CV binary remains in object
 * storage and is addressed by filePath.</p>
 */
public record CvParseCommand(
        UUID messageId,
        String correlationId,
        Long cvId,
        Long candidateId,
        String filePath,
        String fileType,
        Integer schemaVersion,
        OffsetDateTime occurredAt) {

    public CvParseCommand {
        Objects.requireNonNull(messageId, "messageId is required");
        Objects.requireNonNull(cvId, "cvId is required");
        Objects.requireNonNull(candidateId, "candidateId is required");
        Objects.requireNonNull(filePath, "filePath is required");
        Objects.requireNonNull(schemaVersion, "schemaVersion is required");
        Objects.requireNonNull(occurredAt, "occurredAt is required");
    }

    public static CvParseCommand create(
            String correlationId,
            Long cvId,
            Long candidateId,
            String filePath,
            String fileType) {
        return new CvParseCommand(
                UUID.randomUUID(),
                correlationId,
                cvId,
                candidateId,
                filePath,
                fileType,
                1,
                OffsetDateTime.now());
    }
}
