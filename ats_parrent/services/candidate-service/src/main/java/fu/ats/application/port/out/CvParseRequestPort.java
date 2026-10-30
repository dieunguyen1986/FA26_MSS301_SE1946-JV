package fu.ats.application.port.out;

import fu.ats.shared.commonweb.messaging.rabbitmq.CvParseCommand;

/**
 * Boundary for publishing a CV parse request.
 * The application layer does not depend directly on RabbitMQ.
 */
public interface CvParseRequestPort {

    void requestParsing(CvParseCommand command);
}
