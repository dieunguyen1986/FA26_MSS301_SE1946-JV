package fu.ats.shared.commonweb.messaging.rabbitmq;

/**
 * Stable RabbitMQ names shared by the CV producer and parser services.
 * Keep infrastructure names in one place to avoid string drift between
 * producer and consumer deployments.
 */
public final class RabbitMessagingNames {

    private RabbitMessagingNames() {
    }

    public static final String CV_EXCHANGE = "ats.cv.exchange";
    public static final String CV_DEAD_LETTER_EXCHANGE = "ats.cv.dlx";

    public static final String CV_PARSE_QUEUE = "ats.cv.parse.q";
    public static final String CV_RESULT_QUEUE = "ats.cv.result.q";
    public static final String CV_PARSE_DEAD_LETTER_QUEUE = "ats.cv.parse.dlq";
    public static final String CV_RESULT_DEAD_LETTER_QUEUE = "ats.cv.result.dlq";

    public static final String CV_PARSE_ROUTING_KEY = "cv.parse";
    public static final String CV_RESULT_ROUTING_KEY = "cv.result";
    public static final String CV_PARSE_DEAD_LETTER_ROUTING_KEY = "cv.parse.failed";
    public static final String CV_RESULT_DEAD_LETTER_ROUTING_KEY = "cv.result.failed";
}
