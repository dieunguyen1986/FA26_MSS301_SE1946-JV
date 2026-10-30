package fu.ats.infrastructure.messaging;

import fu.ats.shared.commonweb.messaging.rabbitmq.RabbitMessagingNames;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Basic RabbitMQ setup for the CV producer.
 * The parser service declares the queue and binding.
 */
@Configuration(proxyBeanMethods = false)
public class CandidateRabbitConfiguration {

    @Bean
    public DirectExchange cvExchange() {
        return new DirectExchange(RabbitMessagingNames.CV_EXCHANGE);
    }

    @Bean
    public Queue cvQueue() {
        return QueueBuilder.durable(RabbitMessagingNames.CV_PARSE_QUEUE)
                .withArgument("x-message-ttl", 60000)
                .withArgument("x-dead-letter-exchange", RabbitMessagingNames.CV_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMessagingNames.CV_PARSE_DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding cvBinding(DirectExchange cvExchange, Queue cvQueue) {
        return BindingBuilder.bind(cvQueue).to(cvExchange).with(RabbitMessagingNames.CV_PARSE_ROUTING_KEY);
    }

    @Bean
    MessageConverter rabbitMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
