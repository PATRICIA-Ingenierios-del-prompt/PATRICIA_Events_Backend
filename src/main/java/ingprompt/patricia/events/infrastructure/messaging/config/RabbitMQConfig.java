package ingprompt.patricia.events.infrastructure.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // ---------- Outbound exchange (owned by this service) ----------
    public static final String EVENT_EXCHANGE = "event.events";

    public static final String EVENT_LINKED_ROUTING_KEY = "event.linked.to.parche";
    public static final String EVENT_DELETED_ROUTING_KEY = "event.deleted";

    // ---------- Inbound exchange (owned by Parches MS) ----------
    public static final String PARCHE_EXCHANGE = "parche.events";

    public static final String PARCHE_CREATED_ROUTING_KEY = "parche.created";
    public static final String PARCHE_MEMBER_JOINED_ROUTING_KEY = "parche.member.joined";
    public static final String PARCHE_MEMBER_EXPELLED_ROUTING_KEY = "parche.member.expelled";
    public static final String PARCHE_DELETED_ROUTING_KEY = "parche.deleted";

    public static final String PARCHE_CREATED_QUEUE = "events.parche.created.queue";
    public static final String PARCHE_MEMBER_JOINED_QUEUE = "events.parche.member.joined.queue";
    public static final String PARCHE_MEMBER_EXPELLED_QUEUE = "events.parche.member.expelled.queue";
    public static final String PARCHE_DELETED_QUEUE = "events.parche.deleted.queue";

    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange(EVENT_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange parcheExchange() {
        return new TopicExchange(PARCHE_EXCHANGE, true, false);
    }

    @Bean
    public Queue parcheCreatedQueue() {
        return new Queue(PARCHE_CREATED_QUEUE, true);
    }

    @Bean
    public Queue parcheMemberJoinedQueue() {
        return new Queue(PARCHE_MEMBER_JOINED_QUEUE, true);
    }

    @Bean
    public Queue parcheMemberExpelledQueue() {
        return new Queue(PARCHE_MEMBER_EXPELLED_QUEUE, true);
    }

    @Bean
    public Queue parcheDeletedQueue() {
        return new Queue(PARCHE_DELETED_QUEUE, true);
    }

    @Bean
    public Binding parcheCreatedBinding() {
        return BindingBuilder.bind(parcheCreatedQueue()).to(parcheExchange()).with(PARCHE_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding parcheMemberJoinedBinding() {
        return BindingBuilder.bind(parcheMemberJoinedQueue()).to(parcheExchange()).with(PARCHE_MEMBER_JOINED_ROUTING_KEY);
    }

    @Bean
    public Binding parcheMemberExpelledBinding() {
        return BindingBuilder.bind(parcheMemberExpelledQueue()).to(parcheExchange()).with(PARCHE_MEMBER_EXPELLED_ROUTING_KEY);
    }

    @Bean
    public Binding parcheDeletedBinding() {
        return BindingBuilder.bind(parcheDeletedQueue()).to(parcheExchange()).with(PARCHE_DELETED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
