package fu.ats.shared.commonweb.messaging.kafka;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InterviewScheduledEvent(
        UUID eventId,
        int schemaVersion,
        OffsetDateTime occurredAt,
        Long applicationId,
        Long interviewId,
        Long interviewTemplateId,
        String interviewType,
        OffsetDateTime scheduledAt,
        String meetingLink,
        String actorId,
        String correlationId) implements ApplicationEvent {

    @Override
    public String eventType() {
        return "InterviewScheduled";
    }

    public static InterviewScheduledEvent create(
            Long applicationId,
            Long interviewId,
            Long interviewTemplateId,
            String interviewType,
            OffsetDateTime scheduledAt,
            String meetingLink,
            String actorId,
            String correlationId) {
        return new InterviewScheduledEvent(
                UUID.randomUUID(), 1, OffsetDateTime.now(), applicationId,
                interviewId, interviewTemplateId, interviewType, scheduledAt,
                meetingLink, actorId, correlationId);
    }
}
