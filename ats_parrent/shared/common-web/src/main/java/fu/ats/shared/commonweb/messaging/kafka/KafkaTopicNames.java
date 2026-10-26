package fu.ats.shared.commonweb.messaging.kafka;

/**
 * Versioned Kafka topic names owned by the ATS event contracts.
 */
public final class KafkaTopicNames {

    private KafkaTopicNames() {
    }

    public static final String APPLICATION_EVENTS_V1 = "ats.application.events.v1";
    public static final String INTERVIEW_EVENTS_V1 = "ats.interview.events.v1";

    public static final String APPLICATION_EVENTS_V1_DLT = APPLICATION_EVENTS_V1 + ".dlt";
    public static final String INTERVIEW_EVENTS_V1_DLT = INTERVIEW_EVENTS_V1 + ".dlt";
}
