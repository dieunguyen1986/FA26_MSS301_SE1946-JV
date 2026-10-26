package fu.ats.shared.commonweb.messaging.kafka;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InterviewFeedbackSubmittedEvent(
        UUID eventId,
        int schemaVersion,
        OffsetDateTime occurredAt,
        Long applicationId,
        Long interviewId,
        String result,
        Integer score,
        String interviewerId,
        String correlationId) implements ApplicationEvent {

    @Override
    public String eventType() {
        return "InterviewFeedbackSubmitted";
    }

    public static InterviewFeedbackSubmittedEvent create(
            Long applicationId,
            Long interviewId,
            String result,
            Integer score,
            String interviewerId,
            String correlationId) {
        return new InterviewFeedbackSubmittedEvent(
                UUID.randomUUID(), 1, OffsetDateTime.now(), applicationId,
                interviewId, result, score, interviewerId, correlationId);
    }
}
