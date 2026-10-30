package fu.ats.infrastructure.messaging;

import fu.ats.application.port.out.CvParseRequestPort;
import fu.ats.shared.commonweb.messaging.rabbitmq.CvParseCommand;
import fu.ats.shared.commonweb.messaging.rabbitmq.RabbitMessagingNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes a request for cv-parse-service to the shared CV exchange.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitCvParseRequestAdapter implements CvParseRequestPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void requestParsing(CvParseCommand command) {
        rabbitTemplate.convertAndSend(
                RabbitMessagingNames.CV_EXCHANGE,
                RabbitMessagingNames.CV_PARSE_ROUTING_KEY,
                command
        );

        log.info(
                "CV parse command published: messageId={}, cvId={}, candidateId={}",
                command.messageId(),
                command.cvId(),
                command.candidateId()
        );
    }
}
