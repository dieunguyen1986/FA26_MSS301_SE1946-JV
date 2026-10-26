package fu.ats.shared.commonweb.messaging.kafka;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ApplicationSubmittedEvent(
        UUID eventId,
        int schemaVersion,
        OffsetDateTime occurredAt,
        Long applicationId,
        Long candidateId,
        UUID jobId,
        Long cvId,
        UUID departmentId,
        String actorId,
        String correlationId) implements ApplicationEvent {

    @Override
    public String eventType() {
        return "ApplicationSubmitted";
    }

    public static ApplicationSubmittedEvent create(
            Long applicationId,
            Long candidateId,
            UUID jobId,
            Long cvId,
            UUID departmentId,
            String actorId,
            String correlationId) {
        return new ApplicationSubmittedEvent(
                UUID.randomUUID(), 1, OffsetDateTime.now(), applicationId,
                candidateId, jobId, cvId, departmentId, actorId, correlationId);
    }
}
