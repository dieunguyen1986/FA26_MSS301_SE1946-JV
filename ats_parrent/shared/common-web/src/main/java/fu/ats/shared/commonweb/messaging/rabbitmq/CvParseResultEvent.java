package fu.ats.shared.commonweb.messaging.rabbitmq;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Result event emitted by cv-parse-service and consumed by candidate-service.
 * The same command messageId is retained so the consumer can correlate and
 * deduplicate a redelivered result.
 */
public record CvParseResultEvent(
        UUID messageId,
        String correlationId,
        Long cvId,
        boolean successful,
        Map<String, Object> parsedData,
        String errorCode,
        String errorMessage,
        Integer schemaVersion,
        OffsetDateTime occurredAt) {

    public static CvParseResultEvent success(
            CvParseCommand command,
            Map<String, Object> parsedData) {
        return new CvParseResultEvent(
                command.messageId(),
                command.correlationId(),
                command.cvId(),
                true,
                parsedData,
                null,
                null,
                1,
                OffsetDateTime.now());
    }

    public static CvParseResultEvent failure(
            CvParseCommand command,
            String errorCode,
            String errorMessage) {
        return new CvParseResultEvent(
                command.messageId(),
                command.correlationId(),
                command.cvId(),
                false,
                Map.of(),
                errorCode,
                errorMessage,
                1,
                OffsetDateTime.now());
    }
}
