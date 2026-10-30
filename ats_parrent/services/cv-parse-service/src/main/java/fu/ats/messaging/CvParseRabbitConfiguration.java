package fu.ats.messaging;

import fu.ats.shared.commonweb.messaging.rabbitmq.RabbitMessagingNames;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class CvParseRabbitConfiguration {

    @Bean
    public DirectExchange cvExchange() {
        return new DirectExchange(RabbitMessagingNames.CV_EXCHANGE);
    }

    @Bean
    public Queue cvParseQueue() {
        return QueueBuilder.durable(RabbitMessagingNames.CV_PARSE_QUEUE)
                .withArgument("x-message-ttl", 60000)
                .withArgument("x-parse-dead-letter-queue",RabbitMessagingNames.CV_PARSE_DEAD_LETTER_QUEUE)
                .withArgument("x-parse-dead-letter-routing-key", RabbitMessagingNames.CV_PARSE_DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding cvBinding(@Qualifier("cvParseQueue") Queue cvParseQueue, DirectExchange cvExchange) {
        return BindingBuilder.bind(cvParseQueue)
                .to(cvExchange)
                .with(RabbitMessagingNames.CV_PARSE_ROUTING_KEY);
    }

    @Bean
    public Queue cvResultQueue() {
        return QueueBuilder.durable(RabbitMessagingNames.CV_RESULT_QUEUE)
                .withArgument("x-cvparse-ttl", 60000)
                .withArgument("x-result-dead-leter-queue",RabbitMessagingNames.CV_RESULT_DEAD_LETTER_QUEUE)
                .withArgument("x-parse-dead-leter-routing-key", RabbitMessagingNames.CV_RESULT_DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    MessageConverter rabbitMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter rabbitMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(rabbitMessageConverter);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
