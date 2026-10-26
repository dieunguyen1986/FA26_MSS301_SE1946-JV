package fu.ats.shared.commonweb.messaging.kafka;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ApplicationStageChangedEvent(
        UUID eventId,
        int schemaVersion,
        OffsetDateTime occurredAt,
        Long applicationId,
        Long candidateId,
        UUID jobId,
        String fromStage,
        String toStage,
        String actorId,
        String correlationId) implements ApplicationEvent {

    @Override
    public String eventType() {
        return "ApplicationStageChanged";
    }

    public static ApplicationStageChangedEvent create(
            Long applicationId,
            Long candidateId,
            UUID jobId,
            String fromStage,
            String toStage,
            String actorId,
            String correlationId) {
        return new ApplicationStageChangedEvent(
                UUID.randomUUID(), 1, OffsetDateTime.now(), applicationId,
                candidateId, jobId, fromStage, toStage, actorId, correlationId);
    }
}
