package fu.ats.shared.commonweb.messaging.kafka;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Common contract for application lifecycle events published to Kafka.
 * Consumers should use eventId for idempotency and applicationId as the Kafka
 * record key to preserve ordering for one application.
 */
public interface ApplicationEvent {

    UUID eventId();

    String eventType();

    int schemaVersion();

    OffsetDateTime occurredAt();

    Long applicationId();

    String correlationId();
}
